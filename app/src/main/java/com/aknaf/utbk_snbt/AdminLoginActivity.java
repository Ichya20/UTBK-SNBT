package com.aknaf.utbk_snbt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText edtAdminEmail, edtAdminPassword;
    private Button btnAdminLogin, btnBackToUserLogin;
    private TextView tvAdminStatus;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        edtAdminEmail = findViewById(R.id.edtAdminEmail);
        edtAdminPassword = findViewById(R.id.edtAdminPassword);
        btnAdminLogin = findViewById(R.id.btnAdminLogin);
        btnBackToUserLogin = findViewById(R.id.btnBackToUserLogin);
        tvAdminStatus = findViewById(R.id.tvAdminStatus);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            checkAdminRole(currentUser, true);
        }

        btnAdminLogin.setOnClickListener(view -> loginAdmin());
        btnBackToUserLogin.setOnClickListener(view -> finish());
    }

    private void loginAdmin() {
        String email = edtAdminEmail.getText().toString().trim();
        String password = edtAdminPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showStatus("Email dan password admin wajib diisi.");
            return;
        }

        setLoading(true, "Memeriksa akun admin...");

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        checkAdminRole(user, false);
                    } else {
                        setLoading(false, "Login admin gagal.");
                        Toast.makeText(
                                this,
                                "Gagal login admin: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void checkAdminRole(FirebaseUser user, boolean fromExistingSession) {
        if (user == null) {
            setLoading(false, "");
            return;
        }

        setLoading(true, "Memvalidasi role admin...");

        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    String role = document.exists()
                            ? document.getString("role")
                            : null;

                    if ("admin".equalsIgnoreCase(role)) {
                        updateAdminLastLogin(user);
                        Intent intent = new Intent(AdminLoginActivity.this, AdminDashboardActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        mAuth.signOut();
                        setLoading(false, "Akun ini bukan administrator.");

                        if (!fromExistingSession) {
                            Toast.makeText(
                                    this,
                                    "Akun ini tidak memiliki akses administrator.",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
                })
                .addOnFailureListener(error -> {
                    Log.e("ADMIN_LOGIN", "Gagal membaca role admin", error);
                    setLoading(false, "Gagal memeriksa role admin. Periksa koneksi atau Firestore Rules.");
                });
    }

    private void updateAdminLastLogin(FirebaseUser user) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("uid", user.getUid());
        updateData.put("email", user.getEmail() != null ? user.getEmail() : "");
        updateData.put("provider", "admin_mobile");
        updateData.put("lastLoginAt", FieldValue.serverTimestamp());

        db.collection("users")
                .document(user.getUid())
                .update(updateData)
                .addOnFailureListener(error ->
                        Log.e("ADMIN_LOGIN", "Gagal update lastLoginAt admin", error)
                );
    }

    private void setLoading(boolean loading, String message) {
        btnAdminLogin.setEnabled(!loading);
        btnAdminLogin.setText(loading ? "Loading..." : "Masuk Admin");
        showStatus(message);
    }

    private void showStatus(String message) {
        if (tvAdminStatus != null) {
            tvAdminStatus.setText(message == null ? "" : message);
        }
    }
}
