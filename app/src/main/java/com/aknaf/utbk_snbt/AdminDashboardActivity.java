package com.aknaf.utbk_snbt;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvAdminEmail, tvTotalUsers, tvAdminInfo, tvUserList;
    private Button btnRefreshUsers, btnKelolaSoal, btnKelolaMotivasi, btnLogoutAdmin;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd MMM yyyy HH:mm", new Locale("id", "ID"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tvAdminEmail = findViewById(R.id.tvAdminEmail);
        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvAdminInfo = findViewById(R.id.tvAdminInfo);
        tvUserList = findViewById(R.id.tvUserList);

        btnRefreshUsers = findViewById(R.id.btnRefreshUsers);
        btnKelolaSoal = findViewById(R.id.btnKelolaSoal);
        btnKelolaMotivasi = findViewById(R.id.btnKelolaMotivasi);
        btnLogoutAdmin = findViewById(R.id.btnLogoutAdmin);

        btnRefreshUsers.setOnClickListener(view -> loadUserData());

        btnKelolaSoal.setOnClickListener(view -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminManageQuestionsActivity.class);
            startActivity(intent);
        });

        btnKelolaMotivasi.setOnClickListener(view -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminManageMotivationActivity.class);
            startActivity(intent);
        });

        btnLogoutAdmin.setOnClickListener(view -> logoutAdmin());

        checkAdminAccess();
    }

    private void checkAdminAccess() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            goToLogin();
            return;
        }

        tvAdminEmail.setText(user.getEmail() != null ? user.getEmail() : "Admin");

        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    String role = document.exists()
                            ? document.getString("role")
                            : null;

                    if ("admin".equalsIgnoreCase(role)) {
                        tvAdminInfo.setText("Status: Administrator aktif");
                        loadUserData();
                    } else {
                        Toast.makeText(this, "Akses ditolak. Akun bukan admin.", Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                        goToLogin();
                    }
                })
                .addOnFailureListener(error -> {
                    Log.e("ADMIN_DASHBOARD", "Gagal cek admin", error);
                    Toast.makeText(this, "Gagal memeriksa akses admin.", Toast.LENGTH_LONG).show();
                    goToLogin();
                });
    }

    private void loadUserData() {
        tvTotalUsers.setText("Memuat...");
        tvUserList.setText("Mengambil data user...");

        db.collection("users")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int totalUsers = 0;
                    int totalAdmins = 0;
                    int totalGoogle = 0;
                    int totalEmail = 0;

                    StringBuilder builder = new StringBuilder();

                    for (int i = 0; i < querySnapshot.getDocuments().size(); i++) {
                        Map<String, Object> data = querySnapshot.getDocuments().get(i).getData();

                        if (data == null) continue;

                        String role = value(data.get("role"));
                        String provider = value(data.get("provider"));

                        if ("admin".equalsIgnoreCase(role)) {
                            totalAdmins++;
                        } else {
                            totalUsers++;
                        }

                        if (provider.toLowerCase(Locale.ROOT).contains("google")) {
                            totalGoogle++;
                        }

                        if (provider.toLowerCase(Locale.ROOT).contains("email")) {
                            totalEmail++;
                        }

                        if (builder.length() < 9000) {
                            builder.append(i + 1).append(". ")
                                    .append(defaultValue(value(data.get("nama")), "Tanpa nama"))
                                    .append("\nEmail: ")
                                    .append(defaultValue(value(data.get("email")), "-"))
                                    .append("\nRole: ")
                                    .append(defaultValue(role, "user"))
                                    .append("\nProvider: ")
                                    .append(defaultValue(provider, "-"))
                                    .append("\nLast Login: ")
                                    .append(formatTime(data.get("lastLoginAt")))
                                    .append("\n\n");
                        }
                    }

                    tvTotalUsers.setText(
                            "Total User: " + totalUsers +
                                    " | Admin: " + totalAdmins +
                                    " | Google: " + totalGoogle +
                                    " | Email: " + totalEmail
                    );

                    if (TextUtils.isEmpty(builder.toString())) {
                        tvUserList.setText("Belum ada data user.");
                    } else {
                        tvUserList.setText(builder.toString().trim());
                    }
                })
                .addOnFailureListener(error -> {
                    Log.e("ADMIN_DASHBOARD", "Gagal mengambil users", error);
                    tvTotalUsers.setText("Gagal memuat data user");
                    tvUserList.setText(
                            "Data user gagal dimuat.\n\nPastikan akun admin memiliki role admin dan Firestore Rules mengizinkan admin membaca collection users.\n\nError: "
                                    + error.getMessage()
                    );
                });
    }

    private String value(Object object) {
        return object == null ? "" : String.valueOf(object);
    }

    private String defaultValue(String value, String fallback) {
        return value == null || value.trim().isEmpty()
                ? fallback
                : value.trim();
    }

    private String formatTime(Object value) {
        if (value == null) {
            return "-";
        }

        try {
            if (value instanceof com.google.firebase.Timestamp) {
                Date date = ((com.google.firebase.Timestamp) value).toDate();
                return dateFormat.format(date);
            }

            if (value instanceof Number) {
                return dateFormat.format(new Date(((Number) value).longValue()));
            }

            return String.valueOf(value);
        } catch (Exception error) {
            return "-";
        }
    }

    private void logoutAdmin() {
        mAuth.signOut();
        goToLogin();
    }

    private void goToLogin() {
        Intent intent = new Intent(AdminDashboardActivity.this, ActivityLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
