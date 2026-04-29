package com.aknaf.utbk_snbt;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

// Import Gemini (Wajib ada setelah Sync Gradle)
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    RecyclerView rvChat;
    EditText etMessage;
    ImageButton btnSend;
    ChatAdapter chatAdapter;
    List<ChatMessage> chatList;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rvChat = findViewById(R.id.rvChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(layoutManager);
        rvChat.setAdapter(chatAdapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
            listenForMessages();
        } else {
            Toast.makeText(this, "Sesi berakhir, silakan login ulang", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnSend.setOnClickListener(v -> {
            String pesan = etMessage.getText().toString().trim();
            if (!pesan.isEmpty()) {
                sendMessageToFirebase(pesan, "user");
                etMessage.setText("");
                panggilBotAI(pesan);
            }
        });
    }

    private void listenForMessages() {
        db.collection("chats").document(userId).collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null) {
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                ChatMessage chat = dc.getDocument().toObject(ChatMessage.class);
                                chatList.add(chat);
                                chatAdapter.notifyItemInserted(chatList.size() - 1);
                                rvChat.scrollToPosition(chatList.size() - 1);
                            }
                        }
                    }
                });
    }

    private void sendMessageToFirebase(String pesan, String sender) {
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("message", pesan);
        messageData.put("sender", sender);
        messageData.put("timestamp", System.currentTimeMillis());

        db.collection("chats").document(userId).collection("messages")
                .add(messageData);
    }

    private void panggilBotAI(String pesanUser) {
        GenerativeModel gm = new GenerativeModel(
                "gemini-3-flash-preview",
                "AIzaSyDFdN9wekG2zmbYHf_g-HSq1pgcsEqlGNk"
        );

        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        new Thread(() -> {
            try {
                Content content = new Content.Builder()
                        .addText("Kamu adalah asisten ahli UTBK-SNBT. Bantulah user menjawab materi UTBK secara ringkas: " + pesanUser)
                        .build();

                GenerateContentResponse response = model.generateContent(content).get();
                String balasanBeneran = response.getText();

                runOnUiThread(() -> {
                    if (balasanBeneran != null && !balasanBeneran.isEmpty()) {
                        sendMessageToFirebase(balasanBeneran, "bot");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        sendMessageToFirebase("Maaf, koneksi ke otak AI terputus. Coba lagi ya!", "bot")
                );
            }
        }).start();
    }
}