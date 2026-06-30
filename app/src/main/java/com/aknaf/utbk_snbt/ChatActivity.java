package com.aknaf.utbk_snbt;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ChatActivity extends AppCompatActivity {

    private static final String PRIMARY_MODEL_NAME = "gemini-3.5-flash";
    private static final String[] FALLBACK_MODEL_NAMES = new String[]{
            "gemini-3-flash-preview",
            "gemini-2.5-flash"
    };
    private static final long AI_TIMEOUT_SECONDS = 45L;
    private static final int AI_MAX_RETRY_ATTEMPTS = 2;
    private static final long AI_RETRY_DELAY_MILLIS = 1800L;

    private RecyclerView rvChat;
    private EditText etMessage;
    private ImageButton btnSend;

    private View cardStatus;
    private ProgressBar progressAi;
    private TextView tvAiStatus;
    private Button btnRetry;
    private Button btnHideStatus;

    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatList;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ListenerRegistration chatListener;

    private String userId;
    private String lastUserMessage = "";
    private String lastBotResponse = "";

    private boolean requestInProgress = false;
    private RetryMode retryMode = RetryMode.NONE;

    private enum RetryMode {
        NONE,
        SEND_USER_MESSAGE,
        CALL_AI,
        SAVE_BOT_MESSAGE,
        RELOAD_CHAT
    }

    private interface MessageSaveCallback {
        void onSuccess();
        void onFailure(Exception error);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        bindViews();
        setupRecyclerView();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(
                    this,
                    "Sesi berakhir. Silakan login ulang.",
                    Toast.LENGTH_SHORT
            ).show();
            finish();
            return;
        }

        userId = mAuth.getCurrentUser().getUid();
        listenForMessages();

        btnSend.setOnClickListener(view -> sendCurrentMessage());
        btnRetry.setOnClickListener(view -> retryLastAction());
        btnHideStatus.setOnClickListener(view -> hideStatus());
    }

    private void bindViews() {
        rvChat = findViewById(R.id.rvChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        cardStatus = findViewById(R.id.cardStatus);
        progressAi = findViewById(R.id.progressAi);
        tvAiStatus = findViewById(R.id.tvAiStatus);
        btnRetry = findViewById(R.id.btnRetry);
        btnHideStatus = findViewById(R.id.btnHideStatus);
    }

    private void setupRecyclerView() {
        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatList);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);

        rvChat.setLayoutManager(layoutManager);
        rvChat.setAdapter(chatAdapter);
    }

    private void sendCurrentMessage() {
        if (requestInProgress) {
            return;
        }

        String message = etMessage.getText().toString().trim();

        if (message.isEmpty()) {
            Toast.makeText(
                    this,
                    "Tulis pertanyaan terlebih dahulu.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        lastUserMessage = message;
        saveUserMessageAndCallAi(message);
    }

    private void saveUserMessageAndCallAi(String message) {
        if (!isInternetAvailable()) {
            retryMode = RetryMode.SEND_USER_MESSAGE;
            showError(
                    "Tidak ada koneksi internet. Periksa jaringan lalu tekan Coba Lagi.",
                    true
            );
            setInputEnabled(true);
            return;
        }

        requestInProgress = true;
        setInputEnabled(false);
        showLoading("Mengirim pertanyaan...");

        sendMessageToFirebase(
                message,
                "user",
                new MessageSaveCallback() {
                    @Override
                    public void onSuccess() {
                        etMessage.setText("");
                        callBotAi(message);
                    }

                    @Override
                    public void onFailure(Exception error) {
                        requestInProgress = false;
                        retryMode = RetryMode.SEND_USER_MESSAGE;
                        setInputEnabled(true);
                        showError(
                                "Pesan gagal dikirim. Periksa koneksi lalu coba lagi.",
                                true
                        );
                        android.util.Log.e(
                                "CHAT_FIRESTORE",
                                "Gagal menyimpan pesan user",
                                error
                        );
                    }
                }
        );
    }

    private void listenForMessages() {
        if (chatListener != null) {
            chatListener.remove();
        }

        chatList.clear();
        chatAdapter.notifyDataSetChanged();

        if (!requestInProgress) {
            showLoading("Memuat riwayat chat...");
        }

        chatListener = db.collection("chats")
                .document(userId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        if (!requestInProgress) {
                            retryMode = RetryMode.RELOAD_CHAT;
                            showError(
                                    "Riwayat chat gagal dimuat. Tekan Coba Lagi.",
                                    true
                            );
                        }

                        android.util.Log.e(
                                "CHAT_FIRESTORE",
                                "Gagal memuat riwayat chat",
                                error
                        );
                        return;
                    }

                    if (value == null) {
                        return;
                    }

                    for (DocumentChange change :
                            value.getDocumentChanges()) {
                        if (change.getType()
                                == DocumentChange.Type.ADDED) {
                            ChatMessage chat = change
                                    .getDocument()
                                    .toObject(ChatMessage.class);

                            chatList.add(chat);
                            chatAdapter.notifyItemInserted(
                                    chatList.size() - 1
                            );
                            rvChat.scrollToPosition(
                                    chatList.size() - 1
                            );
                        }
                    }

                    if (!requestInProgress) {
                        retryMode = RetryMode.NONE;
                        hideStatus();
                    }
                });
    }

    private void sendMessageToFirebase(
            String message,
            String sender,
            MessageSaveCallback callback
    ) {
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("message", message);
        messageData.put("sender", sender);
        messageData.put(
                "timestamp",
                System.currentTimeMillis()
        );

        db.collection("chats")
                .document(userId)
                .collection("messages")
                .add(messageData)
                .addOnSuccessListener(reference ->
                        callback.onSuccess()
                )
                .addOnFailureListener(callback::onFailure);
    }

    private void callBotAi(String userMessage) {
        if (!isInternetAvailable()) {
            handleAiFailure(
                    new IllegalStateException("NO_INTERNET")
            );
            return;
        }

        String apiKey = BuildConfig.GEMINI_API_KEY;

        if (apiKey == null || apiKey.trim().isEmpty()) {
            requestInProgress = false;
            retryMode = RetryMode.NONE;
            setInputEnabled(true);
            showError(
                    "Konfigurasi AI belum tersedia. Hubungi admin aplikasi.",
                    false
            );
            return;
        }

        requestInProgress = true;
        setInputEnabled(false);
        showLoading("AI sedang menyusun jawaban...");

        new Thread(() -> {
            Exception lastError = null;
            String[] modelCandidates = buildModelCandidates();

            for (String modelName : modelCandidates) {
                boolean shouldMoveToNextModel = false;

                for (int attempt = 0;
                     attempt <= AI_MAX_RETRY_ATTEMPTS;
                     attempt++) {
                    try {
                        if (attempt > 0) {
                            Thread.sleep(
                                    AI_RETRY_DELAY_MILLIS * attempt
                            );
                        }

                        GenerativeModel generativeModel =
                                new GenerativeModel(
                                        modelName,
                                        apiKey
                                );

                        GenerativeModelFutures model =
                                GenerativeModelFutures.from(
                                        generativeModel
                                );

                        Content content = new Content.Builder()
                                .addText(
                                        "Kamu adalah asisten belajar UTBK-SNBT. " +
                                        "Jawab dalam Bahasa Indonesia secara jelas, " +
                                        "ringkas, ramah, dan sesuai konteks materi. " +
                                        "Jika pertanyaan bukan tentang belajar atau UTBK, " +
                                        "arahkan kembali dengan sopan.\n\n" +
                                        "Pertanyaan user: " + userMessage
                                )
                                .build();

                        GenerateContentResponse response =
                                model.generateContent(content)
                                        .get(
                                                AI_TIMEOUT_SECONDS,
                                                TimeUnit.SECONDS
                                        );

                        String answer = response.getText();

                        if (answer == null
                                || answer.trim().isEmpty()) {
                            throw new IllegalStateException(
                                    "EMPTY_AI_RESPONSE"
                            );
                        }

                        lastBotResponse = answer.trim();

                        android.util.Log.i(
                                "GEMINI_MODEL",
                                "Berhasil memakai model: " + modelName
                        );

                        runOnUiThread(
                                () -> saveBotResponse(
                                        lastBotResponse
                                )
                        );
                        return;
                    } catch (Exception error) {
                        lastError = error;

                        if (isTemporaryGeminiError(error)
                                && attempt < AI_MAX_RETRY_ATTEMPTS) {
                            android.util.Log.w(
                                    "GEMINI_RETRY",
                                    "Model " + modelName +
                                            " sedang sibuk, mencoba ulang ke-" +
                                            (attempt + 1),
                                    error
                            );
                            continue;
                        }

                        if (shouldTryNextGeminiModel(error)) {
                            android.util.Log.w(
                                    "GEMINI_FALLBACK",
                                    "Berpindah dari model " + modelName +
                                            " ke model cadangan.",
                                    error
                            );
                            shouldMoveToNextModel = true;
                            break;
                        }

                        runOnUiThread(
                                () -> handleAiFailure(error)
                        );
                        return;
                    }
                }

                if (!shouldMoveToNextModel) {
                    break;
                }
            }

            Exception finalError = lastError != null
                    ? lastError
                    : new IllegalStateException("UNKNOWN_AI_ERROR");

            runOnUiThread(
                    () -> handleAiFailure(finalError)
            );
        }).start();
    }

    private void saveBotResponse(String response) {
        showLoading("Menyimpan balasan AI...");

        sendMessageToFirebase(
                response,
                "bot",
                new MessageSaveCallback() {
                    @Override
                    public void onSuccess() {
                        requestInProgress = false;
                        retryMode = RetryMode.NONE;
                        setInputEnabled(true);
                        hideStatus();
                    }

                    @Override
                    public void onFailure(Exception error) {
                        requestInProgress = false;
                        retryMode = RetryMode.SAVE_BOT_MESSAGE;
                        setInputEnabled(true);
                        showError(
                                "Balasan AI sudah dibuat, tetapi gagal disimpan. Tekan Coba Lagi.",
                                true
                        );

                        android.util.Log.e(
                                "CHAT_FIRESTORE",
                                "Gagal menyimpan balasan AI",
                                error
                        );
                    }
                }
        );
    }

    private void handleAiFailure(Exception error) {
        requestInProgress = false;
        retryMode = RetryMode.CALL_AI;
        setInputEnabled(true);

        String friendlyMessage =
                getFriendlyAiError(error);

        showError(friendlyMessage, true);

        android.util.Log.e(
                "GEMINI_ERROR",
                "Gagal memanggil Gemini",
                error
        );
    }

    private String getFriendlyAiError(Exception error) {
        if (!isInternetAvailable()) {
            return "Koneksi internet terputus. Sambungkan kembali lalu tekan Coba Lagi.";
        }

        Throwable rootCause = unwrap(error);
        String rawMessage = rootCause.getMessage();
        String lowerMessage = rawMessage == null
                ? ""
                : rawMessage.toLowerCase(Locale.ROOT);

        if (rootCause instanceof TimeoutException
                || lowerMessage.contains("timeout")
                || lowerMessage.contains("timed out")) {
            return "Respons AI terlalu lama. Silakan tekan Coba Lagi.";
        }

        if (lowerMessage.contains("429")
                || lowerMessage.contains("quota")
                || lowerMessage.contains("rate limit")
                || lowerMessage.contains("resource exhausted")) {
            return "Layanan AI sedang sibuk atau batas penggunaan tercapai. Coba lagi beberapa saat.";
        }

        if (isTemporaryGeminiError(rootCause)) {
            return "AI sedang ramai digunakan. Silakan coba lagi beberapa saat.";
        }

        if (lowerMessage.contains("401")
                || lowerMessage.contains("403")
                || lowerMessage.contains("api key")
                || lowerMessage.contains("permission denied")
                || lowerMessage.contains("unauthenticated")) {
            retryMode = RetryMode.NONE;
            return "Konfigurasi layanan AI bermasalah. Hubungi admin aplikasi.";
        }

        if (lowerMessage.contains("safety")
                || lowerMessage.contains("blocked")) {
            return "Pertanyaan tidak dapat diproses. Coba gunakan kalimat lain yang berkaitan dengan pembelajaran.";
        }

        if (lowerMessage.contains("empty_ai_response")) {
            return "AI belum memberikan jawaban. Silakan tekan Coba Lagi.";
        }

        return "AI sedang tidak dapat diakses. Coba lagi beberapa saat.";
    }

    private String[] buildModelCandidates() {
        String[] models =
                new String[FALLBACK_MODEL_NAMES.length + 1];

        models[0] = PRIMARY_MODEL_NAME;

        for (int i = 0; i < FALLBACK_MODEL_NAMES.length; i++) {
            models[i + 1] = FALLBACK_MODEL_NAMES[i];
        }

        return models;
    }

    private boolean shouldTryNextGeminiModel(Throwable error) {
        if (isTemporaryGeminiError(error)) {
            return true;
        }

        Throwable rootCause = unwrap(error);
        String message = rootCause.getMessage();

        if (message == null) {
            return false;
        }

        String lowerMessage =
                message.toLowerCase(Locale.ROOT);

        return lowerMessage.contains("404")
                || lowerMessage.contains("not found")
                || lowerMessage.contains("not_found")
                || lowerMessage.contains("not supported")
                || lowerMessage.contains("unsupported")
                || lowerMessage.contains("deprecated");
    }

    private boolean isTemporaryGeminiError(Throwable error) {
        Throwable rootCause = unwrap(error);
        String message = rootCause.getMessage();

        if (message == null) {
            return false;
        }

        String lowerMessage =
                message.toLowerCase(Locale.ROOT);

        return lowerMessage.contains("503")
                || lowerMessage.contains("unavailable")
                || lowerMessage.contains("high demand")
                || lowerMessage.contains("server is overloaded")
                || lowerMessage.contains("temporarily unavailable")
                || lowerMessage.contains("try again later");
    }

    private Throwable unwrap(Throwable error) {
        Throwable current = error;

        while (current instanceof ExecutionException
                && current.getCause() != null) {
            current = current.getCause();
        }

        return current;
    }

    private void retryLastAction() {
        if (requestInProgress) {
            return;
        }

        switch (retryMode) {
            case SEND_USER_MESSAGE:
                if (!lastUserMessage.isEmpty()) {
                    saveUserMessageAndCallAi(
                            lastUserMessage
                    );
                }
                break;

            case CALL_AI:
                if (!lastUserMessage.isEmpty()) {
                    callBotAi(lastUserMessage);
                }
                break;

            case SAVE_BOT_MESSAGE:
                if (!lastBotResponse.isEmpty()) {
                    requestInProgress = true;
                    setInputEnabled(false);
                    saveBotResponse(lastBotResponse);
                }
                break;

            case RELOAD_CHAT:
                listenForMessages();
                break;

            case NONE:
            default:
                hideStatus();
                break;
        }
    }

    private void showLoading(String message) {
        cardStatus.setVisibility(View.VISIBLE);
        progressAi.setVisibility(View.VISIBLE);
        tvAiStatus.setText(message);
        btnRetry.setVisibility(View.GONE);
        btnHideStatus.setVisibility(View.GONE);
    }

    private void showError(
            String message,
            boolean canRetry
    ) {
        cardStatus.setVisibility(View.VISIBLE);
        progressAi.setVisibility(View.GONE);
        tvAiStatus.setText(message);

        btnRetry.setVisibility(
                canRetry
                        ? View.VISIBLE
                        : View.GONE
        );

        btnHideStatus.setVisibility(View.VISIBLE);
    }

    private void hideStatus() {
        cardStatus.setVisibility(View.GONE);
    }

    private void setInputEnabled(boolean enabled) {
        etMessage.setEnabled(enabled);
        btnSend.setEnabled(enabled);
        btnSend.setAlpha(enabled ? 1.0f : 0.45f);
    }

    private boolean isInternetAvailable() {
        ConnectivityManager manager =
                (ConnectivityManager) getSystemService(
                        Context.CONNECTIVITY_SERVICE
                );

        if (manager == null) {
            return false;
        }

        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.M) {
            Network network =
                    manager.getActiveNetwork();

            if (network == null) {
                return false;
            }

            NetworkCapabilities capabilities =
                    manager.getNetworkCapabilities(
                            network
                    );

            return capabilities != null
                    && capabilities.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_INTERNET
            )
                    && capabilities.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_VALIDATED
            );
        }

        NetworkInfo networkInfo =
                manager.getActiveNetworkInfo();

        return networkInfo != null
                && networkInfo.isConnected();
    }

    @Override
    protected void onDestroy() {
        if (chatListener != null) {
            chatListener.remove();
            chatListener = null;
        }

        super.onDestroy();
    }
}
