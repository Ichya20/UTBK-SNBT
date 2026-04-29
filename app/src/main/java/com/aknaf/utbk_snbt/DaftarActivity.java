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

public class DaftarActivity extends AppCompatActivity {

    EditText nama, telepon, tanggal, email, password, konfirmasi;
    Button btnDaftar;

    // 🔥 DEKLARASI FIREBASE AUTH
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);

        // 🔥 INISIALISASI FIREBASE AUTH
        mAuth = FirebaseAuth.getInstance();

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

                                    // TODO: Simpan data Nama, Telepon, Tanggal ke Firebase Database

                                    Toast.makeText(DaftarActivity.this,
                                            "Pendaftaran berhasil!", Toast.LENGTH_SHORT).show();

                                    // Pindah ke Login
                                    Intent intent = new Intent(DaftarActivity.this, ActivityLogin.class);
                                    startActivity(intent);
                                    finish();
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
}