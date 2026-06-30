package com.aknaf.utbk_snbt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// 🔥 IMPORT FIREBASE AUTH
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// 🔥 IMPORT FIRESTORE UNTUK SIMPAN DATA USER KE ADMIN
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DaftarActivity extends AppCompatActivity {

    EditText nama, telepon, tanggal, email, password, konfirmasi;
    Button btnDaftar;

    // 🔥 DEKLARASI FIREBASE AUTH
    private FirebaseAuth mAuth;

    // 🔥 FIRESTORE UNTUK DATA USER ADMIN
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);

        // 🔥 INISIALISASI FIREBASE AUTH
        mAuth = FirebaseAuth.getInstance();

        // 🔥 INISIALISASI FIRESTORE
        db = FirebaseFirestore.getInstance();

        nama = findViewById(R.id.nama);
        telepon = findViewById(R.id.telepon);
        tanggal = findViewById(R.id.tanggal);
        email = findViewById(R.id.emailDaftar);
        password = findViewById(R.id.passwordDaftar);
        konfirmasi = findViewById(R.id.konfirmasiPassword);
        btnDaftar = findViewById(R.id.btnDaftarAkun);

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sNama = nama.getText().toString().trim();
                String sTelepon = telepon.getText().toString().trim();
                String sTanggal = tanggal.getText().toString().trim();
                String sEmail = email.getText().toString().trim();
                String sPassword = password.getText().toString().trim();
                String sKonfirmasi = konfirmasi.getText().toString().trim();

                // VALIDASI
                if (sNama.isEmpty() || sTelepon.isEmpty() || sTanggal.isEmpty()
                        || sEmail.isEmpty() || sPassword.isEmpty() || sKonfirmasi.isEmpty()) {

                    Toast.makeText(DaftarActivity.this,
                            "Semua data wajib diisi!", Toast.LENGTH_SHORT).show();

                } else if (!sPassword.equals(sKonfirmasi)) {

                    Toast.makeText(DaftarActivity.this,
                            "Password tidak sama!", Toast.LENGTH_SHORT).show();

                } else if (sPassword.length() < 6) {

                    // Firebase wajibkan password minimal 6 karakter
                    Toast.makeText(DaftarActivity.this,
                            "Password minimal 6 karakter!", Toast.LENGTH_SHORT).show();

                } else {

                    // 🔥 PROSES DAFTAR KE FIREBASE AUTH
                    mAuth.createUserWithEmailAndPassword(sEmail, sPassword)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Kalau sukses bikin akun
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    if (user != null) {
                                        simpanDataUserKeFirestore(
                                                user.getUid(),
                                                sNama,
                                                sTelepon,
                                                sTanggal,
                                                sEmail
                                        );
                                    } else {
                                        Toast.makeText(DaftarActivity.this,
                                                "Pendaftaran berhasil, tapi data user belum terbaca.",
                                                Toast.LENGTH_LONG).show();

                                        Intent intent = new Intent(DaftarActivity.this, ActivityLogin.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                } else {
                                    // Kalau gagal (misal email sudah terdaftar atau format salah)
                                    Toast.makeText(DaftarActivity.this,
                                            "Gagal: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });
    }
    private void simpanDataUserKeFirestore(String uid, String namaUser, String teleponUser,
                                           String tanggalUser, String emailUser) {

        Map<String, Object> dataUser = new HashMap<>();
        dataUser.put("uid", uid);
        dataUser.put("nama", namaUser);
        dataUser.put("telepon", teleponUser);
        dataUser.put("tanggal", tanggalUser);
        dataUser.put("email", emailUser);
        dataUser.put("role", "user");
        dataUser.put("provider", "email_password");
        dataUser.put("createdAt", FieldValue.serverTimestamp());
        dataUser.put("lastLoginAt", FieldValue.serverTimestamp());

        db.collection("users")
                .document(uid)
                .set(dataUser)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(DaftarActivity.this,
                            "Pendaftaran berhasil!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(DaftarActivity.this, ActivityLogin.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(error -> {
                    Toast.makeText(DaftarActivity.this,
                            "Akun berhasil dibuat, tapi data user gagal disimpan: "
                                    + error.getMessage(),
                            Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(DaftarActivity.this, ActivityLogin.class);
                    startActivity(intent);
                    finish();
                });
    }

}