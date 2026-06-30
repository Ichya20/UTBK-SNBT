package com.aknaf.utbk_snbt;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class AdminManageMotivationActivity extends AppCompatActivity {

    private EditText edtMotivationId, edtMotivationTitle, edtMotivationMessage, edtMotivationAuthor;
    private Spinner spMotivationCategory;
    private Switch swMotivationActive;
    private Button btnLoadMotivation, btnSaveMotivation, btnDeleteMotivation, btnClearMotivation, btnRefreshMotivationList, btnBackMotivation;
    private TextView tvMotivationStatus, tvMotivationList;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private final String COLLECTION = "motivations";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_motivation);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        bindViews();
        setupSpinner();

        btnLoadMotivation.setOnClickListener(view -> loadMotivationById());
        btnSaveMotivation.setOnClickListener(view -> saveMotivation());
        btnDeleteMotivation.setOnClickListener(view -> confirmDeleteMotivation());
        btnClearMotivation.setOnClickListener(view -> clearForm());
        btnRefreshMotivationList.setOnClickListener(view -> loadMotivationList());
        btnBackMotivation.setOnClickListener(view -> finish());

        checkAdminThenLoad();
    }

    private void bindViews() {
        edtMotivationId = findViewById(R.id.edtMotivationId);
        edtMotivationTitle = findViewById(R.id.edtMotivationTitle);
        edtMotivationMessage = findViewById(R.id.edtMotivationMessage);
        edtMotivationAuthor = findViewById(R.id.edtMotivationAuthor);

        spMotivationCategory = findViewById(R.id.spMotivationCategory);
        swMotivationActive = findViewById(R.id.swMotivationActive);

        btnLoadMotivation = findViewById(R.id.btnLoadMotivation);
        btnSaveMotivation = findViewById(R.id.btnSaveMotivation);
        btnDeleteMotivation = findViewById(R.id.btnDeleteMotivation);
        btnClearMotivation = findViewById(R.id.btnClearMotivation);
        btnRefreshMotivationList = findViewById(R.id.btnRefreshMotivationList);
        btnBackMotivation = findViewById(R.id.btnBackMotivation);

        tvMotivationStatus = findViewById(R.id.tvMotivationStatus);
        tvMotivationList = findViewById(R.id.tvMotivationList);
    }

    private void setupSpinner() {
        String[] categories = new String[]{
                "umum", "belajar", "tryout", "utbk", "pengingat", "semangat"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMotivationCategory.setAdapter(adapter);
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
                        loadMotivationList();
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

    private void loadMotivationById() {
        String id = clean(edtMotivationId.getText().toString());

        if (id.isEmpty()) {
            Toast.makeText(this, "Isi Motivation ID dulu.", Toast.LENGTH_SHORT).show();
            return;
        }

        setStatus("Memuat motivasi...");

        db.collection(COLLECTION)
                .document(id)
                .get()
                .addOnSuccessListener(document -> {
                    if (!document.exists()) {
                        setStatus("Motivasi tidak ditemukan.");
                        Toast.makeText(this, "Motivasi tidak ditemukan.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Map<String, Object> data = document.getData();
                    if (data == null) {
                        setStatus("Data motivasi kosong.");
                        return;
                    }

                    edtMotivationId.setText(document.getId());
                    edtMotivationTitle.setText(firstValue(data, "title", "judul"));
                    edtMotivationMessage.setText(firstValue(data, "message", "text", "isi"));
                    edtMotivationAuthor.setText(firstValue(data, "author", "penulis"));
                    setSpinnerValue(spMotivationCategory, firstValue(data, "category", "kategori"));

                    Object active = data.get("isActive");
                    if (active instanceof Boolean) {
                        swMotivationActive.setChecked((Boolean) active);
                    } else {
                        swMotivationActive.setChecked(!"false".equalsIgnoreCase(String.valueOf(active)));
                    }

                    setStatus("Motivasi berhasil dimuat.");
                })
                .addOnFailureListener(error -> {
                    Log.e("ADMIN_MOTIVATION", "Gagal load motivasi", error);
                    setStatus("Gagal load motivasi: " + error.getMessage());
                });
    }

    private void saveMotivation() {
        String id = clean(edtMotivationId.getText().toString());

        if (id.isEmpty()) {
            Toast.makeText(this, "Motivation ID wajib diisi.", Toast.LENGTH_SHORT).show();
            return;
        }

        String message = clean(edtMotivationMessage.getText().toString());

        if (message.isEmpty()) {
            Toast.makeText(this, "Isi motivasi wajib diisi.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("title", clean(edtMotivationTitle.getText().toString()));
        data.put("judul", clean(edtMotivationTitle.getText().toString()));
        data.put("message", message);
        data.put("text", message);
        data.put("isi", message);
        data.put("author", clean(edtMotivationAuthor.getText().toString()));
        data.put("penulis", clean(edtMotivationAuthor.getText().toString()));
        data.put("category", spMotivationCategory.getSelectedItem().toString());
        data.put("kategori", spMotivationCategory.getSelectedItem().toString());
        data.put("isActive", swMotivationActive.isChecked());
        data.put("updatedAt", FieldValue.serverTimestamp());

        setStatus("Menyimpan motivasi...");

        db.collection(COLLECTION)
                .document(id)
                .set(data)
                .addOnSuccessListener(unused -> {
                    setStatus("Motivasi berhasil disimpan.");
                    Toast.makeText(this, "Motivasi berhasil disimpan.", Toast.LENGTH_SHORT).show();
                    loadMotivationList();
                })
                .addOnFailureListener(error -> {
                    Log.e("ADMIN_MOTIVATION", "Gagal simpan motivasi", error);
                    setStatus("Gagal simpan motivasi: " + error.getMessage());
                    Toast.makeText(this, "Gagal simpan motivasi.", Toast.LENGTH_LONG).show();
                });
    }

    private void confirmDeleteMotivation() {
        String id = clean(edtMotivationId.getText().toString());

        if (id.isEmpty()) {
            Toast.makeText(this, "Isi Motivation ID dulu.", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Hapus Motivasi")
                .setMessage("Yakin hapus motivasi dengan ID:\n" + id + "?")
                .setPositiveButton("Hapus", (dialog, which) -> deleteMotivation(id))
                .setNegativeButton("Batal", null)
                .show();
    }

    private void deleteMotivation(String id) {
        setStatus("Menghapus motivasi...");

        db.collection(COLLECTION)
                .document(id)
                .delete()
                .addOnSuccessListener(unused -> {
                    setStatus("Motivasi berhasil dihapus.");
                    Toast.makeText(this, "Motivasi berhasil dihapus.", Toast.LENGTH_SHORT).show();
                    clearForm();
                    loadMotivationList();
                })
                .addOnFailureListener(error -> {
                    setStatus("Gagal hapus motivasi: " + error.getMessage());
                    Toast.makeText(this, "Gagal hapus motivasi.", Toast.LENGTH_LONG).show();
                });
    }

    private void loadMotivationList() {
        tvMotivationList.setText("Memuat daftar motivasi...");

        db.collection(COLLECTION)
                .orderBy("updatedAt", Query.Direction.DESCENDING)
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
                                .append("\nJudul: ")
                                .append(firstValue(data, "title", "judul"))
                                .append("\nKategori: ")
                                .append(firstValue(data, "category", "kategori"))
                                .append("\nAktif: ")
                                .append(value(data == null ? null : data.get("isActive")))
                                .append("\nIsi: ")
                                .append(shortText(firstValue(data, "message", "text", "isi"), 120))
                                .append("\n\n");
                    }

                    if (builder.length() == 0) {
                        tvMotivationList.setText("Belum ada data di collection motivations.");
                    } else {
                        tvMotivationList.setText(builder.toString().trim());
                    }
                })
                .addOnFailureListener(error -> {
                    tvMotivationList.setText("Gagal memuat motivasi.\nError: " + error.getMessage()
                            + "\n\nJika error index/orderBy, Firestore Rules/index perlu dicek.");
                });
    }

    private void clearForm() {
        edtMotivationId.setText("");
        edtMotivationTitle.setText("");
        edtMotivationMessage.setText("");
        edtMotivationAuthor.setText("");
        spMotivationCategory.setSelection(0);
        swMotivationActive.setChecked(true);
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

    private String value(Object object) {
        return object == null ? "" : String.valueOf(object);
    }

    private String firstValue(Map<String, Object> data, String... keys) {
        if (data == null) return "";

        for (String key : keys) {
            Object value = data.get(key);
            if (value != null && !String.valueOf(value).trim().isEmpty()) {
                return String.valueOf(value).trim();
            }
        }

        return "";
    }

    private String shortText(String text, int max) {
        if (TextUtils.isEmpty(text)) return "";
        if (text.length() <= max) return text;
        return text.substring(0, max) + "...";
    }

    private void setStatus(String message) {
        if (tvMotivationStatus != null) {
            tvMotivationStatus.setText(message == null ? "" : message);
        }
    }
}
