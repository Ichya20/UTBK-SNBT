package com.aknaf.utbk_snbt;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AdminManageQuestionsActivity extends AppCompatActivity {

    private EditText edtQuestionId, edtQuestion, edtQuestionImageName;
    private EditText edtOptionA, edtOptionB, edtOptionC, edtOptionD, edtOptionE;
    private EditText edtOptionAImageName, edtOptionBImageName, edtOptionCImageName, edtOptionDImageName, edtOptionEImageName;
    private EditText edtExplanation, edtExplanationImageName;
    private Spinner spSubject, spAnswer;
    private Button btnLoadQuestion, btnSaveQuestion, btnDeleteQuestion, btnClearQuestion, btnRefreshQuestionList, btnBackQuestion;
    private TextView tvQuestionList, tvQuestionStatus;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private final String COLLECTION = "dynamic_questions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_questions);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        bindViews();
        setupSpinners();

        btnLoadQuestion.setOnClickListener(view -> loadQuestionById());
        btnSaveQuestion.setOnClickListener(view -> saveQuestion());
        btnDeleteQuestion.setOnClickListener(view -> confirmDeleteQuestion());
        btnClearQuestion.setOnClickListener(view -> clearForm());
        btnRefreshQuestionList.setOnClickListener(view -> loadQuestionList());
        btnBackQuestion.setOnClickListener(view -> finish());

        checkAdminThenLoad();
    }

    private void bindViews() {
        edtQuestionId = findViewById(R.id.edtQuestionId);
        edtQuestion = findViewById(R.id.edtQuestion);
        edtQuestionImageName = findViewById(R.id.edtQuestionImageName);

        edtOptionA = findViewById(R.id.edtOptionA);
        edtOptionB = findViewById(R.id.edtOptionB);
        edtOptionC = findViewById(R.id.edtOptionC);
        edtOptionD = findViewById(R.id.edtOptionD);
        edtOptionE = findViewById(R.id.edtOptionE);

        edtOptionAImageName = findViewById(R.id.edtOptionAImageName);
        edtOptionBImageName = findViewById(R.id.edtOptionBImageName);
        edtOptionCImageName = findViewById(R.id.edtOptionCImageName);
        edtOptionDImageName = findViewById(R.id.edtOptionDImageName);
        edtOptionEImageName = findViewById(R.id.edtOptionEImageName);

        edtExplanation = findViewById(R.id.edtExplanation);
        edtExplanationImageName = findViewById(R.id.edtExplanationImageName);

        spSubject = findViewById(R.id.spSubject);
        spAnswer = findViewById(R.id.spAnswer);

        btnLoadQuestion = findViewById(R.id.btnLoadQuestion);
        btnSaveQuestion = findViewById(R.id.btnSaveQuestion);
        btnDeleteQuestion = findViewById(R.id.btnDeleteQuestion);
        btnClearQuestion = findViewById(R.id.btnClearQuestion);
        btnRefreshQuestionList = findViewById(R.id.btnRefreshQuestionList);
        btnBackQuestion = findViewById(R.id.btnBackQuestion);

        tvQuestionList = findViewById(R.id.tvQuestionList);
        tvQuestionStatus = findViewById(R.id.tvQuestionStatus);
    }

    private void setupSpinners() {
        String[] subjects = new String[]{
                "PU", "PPU", "PBM", "PK", "LBI", "LBE", "PM"
        };

        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                subjects
        );
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSubject.setAdapter(subjectAdapter);

        String[] answers = new String[]{
                "optionA", "optionB", "optionC", "optionD", "optionE"
        };

        ArrayAdapter<String> answerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                answers
        );
        answerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAnswer.setAdapter(answerAdapter);
    }

    private void checkAdminThenLoad() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Silakan login admin dulu.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        setStatus("Memeriksa akses admin...");

        db.collection("users")
                .document(auth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String role = documentSnapshot.getString("role");
                    if ("admin".equalsIgnoreCase(role)) {
                        setStatus("Akses admin aktif.");
                        loadQuestionList();
                    } else {
                        Toast.makeText(this, "Akses ditolak. Akun bukan admin.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                })
                .addOnFailureListener(error -> {
                    setStatus("Gagal cek admin: " + error.getMessage());
                    Toast.makeText(this, "Gagal cek akses admin.", Toast.LENGTH_LONG).show();
                    finish();
                });
    }

    private void loadQuestionById() {
        String id = clean(edtQuestionId.getText().toString());

        if (id.isEmpty()) {
            Toast.makeText(this, "Isi Question ID dulu.", Toast.LENGTH_SHORT).show();
            return;
        }

        setStatus("Memuat soal...");

        db.collection(COLLECTION)
                .document(id)
                .get()
                .addOnSuccessListener(document -> {
                    if (!document.exists()) {
                        setStatus("Soal tidak ditemukan.");
                        Toast.makeText(this, "Soal tidak ditemukan.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Map<String, Object> data = document.getData();
                    if (data == null) {
                        setStatus("Data soal kosong.");
                        return;
                    }

                    edtQuestionId.setText(document.getId());
                    edtQuestion.setText(value(data.get("question")));
                    edtQuestionImageName.setText(value(data.get("questionImageName")));

                    edtOptionA.setText(value(data.get("optionA")));
                    edtOptionB.setText(value(data.get("optionB")));
                    edtOptionC.setText(value(data.get("optionC")));
                    edtOptionD.setText(value(data.get("optionD")));
                    edtOptionE.setText(value(data.get("optionE")));

                    edtOptionAImageName.setText(value(data.get("optionAImageName")));
                    edtOptionBImageName.setText(value(data.get("optionBImageName")));
                    edtOptionCImageName.setText(value(data.get("optionCImageName")));
                    edtOptionDImageName.setText(value(data.get("optionDImageName")));
                    edtOptionEImageName.setText(value(data.get("optionEImageName")));

                    edtExplanation.setText(value(data.get("explanation")));
                    edtExplanationImageName.setText(value(data.get("explanationImageName")));

                    setSpinnerValue(spSubject, value(data.get("subject")));
                    setSpinnerValue(spAnswer, value(data.get("answer")));

                    setStatus("Soal berhasil dimuat.");
                })
                .addOnFailureListener(error -> {
                    Log.e("ADMIN_QUESTIONS", "Gagal load soal", error);
                    setStatus("Gagal load soal: " + error.getMessage());
                });
    }

    private void saveQuestion() {
        String id = clean(edtQuestionId.getText().toString());

        if (id.isEmpty()) {
            Toast.makeText(this, "Question ID wajib diisi.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (clean(edtQuestion.getText().toString()).isEmpty()) {
            Toast.makeText(this, "Pertanyaan wajib diisi.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("subject", spSubject.getSelectedItem().toString());
        data.put("question", clean(edtQuestion.getText().toString()));
        data.put("questionImageName", cleanImageName(edtQuestionImageName.getText().toString()));
        data.put("questionImageUrl", "");

        data.put("optionA", clean(edtOptionA.getText().toString()));
        data.put("optionB", clean(edtOptionB.getText().toString()));
        data.put("optionC", clean(edtOptionC.getText().toString()));
        data.put("optionD", clean(edtOptionD.getText().toString()));
        data.put("optionE", clean(edtOptionE.getText().toString()));

        data.put("optionAImageName", cleanImageName(edtOptionAImageName.getText().toString()));
        data.put("optionBImageName", cleanImageName(edtOptionBImageName.getText().toString()));
        data.put("optionCImageName", cleanImageName(edtOptionCImageName.getText().toString()));
        data.put("optionDImageName", cleanImageName(edtOptionDImageName.getText().toString()));
        data.put("optionEImageName", cleanImageName(edtOptionEImageName.getText().toString()));

        data.put("optionAImageUrl", "");
        data.put("optionBImageUrl", "");
        data.put("optionCImageUrl", "");
        data.put("optionDImageUrl", "");
        data.put("optionEImageUrl", "");

        data.put("answer", spAnswer.getSelectedItem().toString());
        data.put("explanation", clean(edtExplanation.getText().toString()));
        data.put("explanationImageName", cleanImageName(edtExplanationImageName.getText().toString()));
        data.put("explanationImageUrl", "");

        data.put("updatedAt", FieldValue.serverTimestamp());

        setStatus("Menyimpan soal...");

        db.collection(COLLECTION)
                .document(id)
                .set(data)
                .addOnSuccessListener(unused -> {
                    setStatus("Soal berhasil disimpan.");
                    Toast.makeText(this, "Soal berhasil disimpan.", Toast.LENGTH_SHORT).show();
                    loadQuestionList();
                })
                .addOnFailureListener(error -> {
                    Log.e("ADMIN_QUESTIONS", "Gagal simpan soal", error);
                    setStatus("Gagal simpan soal: " + error.getMessage());
                    Toast.makeText(this, "Gagal simpan soal.", Toast.LENGTH_LONG).show();
                });
    }

    private void confirmDeleteQuestion() {
        String id = clean(edtQuestionId.getText().toString());

        if (id.isEmpty()) {
            Toast.makeText(this, "Isi Question ID dulu.", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Hapus Soal")
                .setMessage("Yakin hapus soal dengan ID:\n" + id + "?")
                .setPositiveButton("Hapus", (dialog, which) -> deleteQuestion(id))
                .setNegativeButton("Batal", null)
                .show();
    }

    private void deleteQuestion(String id) {
        setStatus("Menghapus soal...");

        db.collection(COLLECTION)
                .document(id)
                .delete()
                .addOnSuccessListener(unused -> {
                    setStatus("Soal berhasil dihapus.");
                    Toast.makeText(this, "Soal berhasil dihapus.", Toast.LENGTH_SHORT).show();
                    clearForm();
                    loadQuestionList();
                })
                .addOnFailureListener(error -> {
                    setStatus("Gagal hapus soal: " + error.getMessage());
                    Toast.makeText(this, "Gagal hapus soal.", Toast.LENGTH_LONG).show();
                });
    }

    private void loadQuestionList() {
        tvQuestionList.setText("Memuat daftar soal...");

        db.collection(COLLECTION)
                .orderBy("subject", Query.Direction.ASCENDING)
                .limit(80)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    StringBuilder builder = new StringBuilder();

                    for (int i = 0; i < querySnapshot.getDocuments().size(); i++) {
                        Map<String, Object> data = querySnapshot.getDocuments().get(i).getData();
                        String id = querySnapshot.getDocuments().get(i).getId();

                        builder.append(i + 1)
                                .append(". ")
                                .append(id)
                                .append("\nSubject: ")
                                .append(value(data == null ? null : data.get("subject")))
                                .append("\nJawaban: ")
                                .append(value(data == null ? null : data.get("answer")))
                                .append("\nSoal: ")
                                .append(shortText(value(data == null ? null : data.get("question")), 110))
                                .append("\n\n");
                    }

                    if (builder.length() == 0) {
                        tvQuestionList.setText("Belum ada soal di dynamic_questions.");
                    } else {
                        tvQuestionList.setText(builder.toString().trim());
                    }
                })
                .addOnFailureListener(error -> {
                    tvQuestionList.setText("Gagal memuat soal.\nError: " + error.getMessage()
                            + "\n\nJika error index/orderBy, Firestore Rules/index perlu dicek.");
                });
    }

    private void clearForm() {
        edtQuestionId.setText("");
        edtQuestion.setText("");
        edtQuestionImageName.setText("");

        edtOptionA.setText("");
        edtOptionB.setText("");
        edtOptionC.setText("");
        edtOptionD.setText("");
        edtOptionE.setText("");

        edtOptionAImageName.setText("");
        edtOptionBImageName.setText("");
        edtOptionCImageName.setText("");
        edtOptionDImageName.setText("");
        edtOptionEImageName.setText("");

        edtExplanation.setText("");
        edtExplanationImageName.setText("");

        spSubject.setSelection(0);
        spAnswer.setSelection(0);
        setStatus("Form dikosongkan.");
    }

    private void setSpinnerValue(Spinner spinner, String value) {
        if (value == null) return;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (value.equalsIgnoreCase(String.valueOf(spinner.getItemAtPosition(i)))) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    private String clean(String text) {
        return text == null ? "" : text.trim();
    }

    private String cleanImageName(String text) {
        String result = clean(text);
        if (result.endsWith(".png")) {
            result = result.substring(0, result.length() - 4);
        }
        if (result.endsWith(".jpg")) {
            result = result.substring(0, result.length() - 4);
        }
        if (result.endsWith(".jpeg")) {
            result = result.substring(0, result.length() - 5);
        }
        return result.toLowerCase(Locale.ROOT);
    }

    private String value(Object object) {
        return object == null ? "" : String.valueOf(object);
    }

    private String shortText(String text, int max) {
        if (text == null) return "";
        if (text.length() <= max) return text;
        return text.substring(0, max) + "...";
    }

    private void setStatus(String message) {
        if (tvQuestionStatus != null) {
            tvQuestionStatus.setText(message == null ? "" : message);
        }
    }
}
