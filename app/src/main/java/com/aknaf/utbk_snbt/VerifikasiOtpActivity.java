package com.aknaf.utbk_snbt;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class VerifikasiOtpActivity extends AppCompatActivity {

    EditText etInputOtp;
    Button btnVerifikasi;
    String otpDariHalamanSebelumnya;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifikasi_otp);

        etInputOtp = findViewById(R.id.etInputOtp);
        btnVerifikasi = findViewById(R.id.btnVerifikasi);

        // Mengambil kode OTP yang dikirim melalui Intent dari LupaSandiActivity
        otpDariHalamanSebelumnya = getIntent().getStringExtra("OTPSent");

        btnVerifikasi.setOnClickListener(v -> {
            String otpInput = etInputOtp.getText().toString().trim();

            if (otpInput.isEmpty()) {
                Toast.makeText(this, "Masukkan kode OTP!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Mencocokkan input user dengan kode yang dikirim tadi
            if (otpInput.equals(otpDariHalamanSebelumnya)) {
                Toast.makeText(this, "Verifikasi Berhasil!", Toast.LENGTH_SHORT).show();

                // Jika benar, lanjut ke halaman ganti password baru
                Intent intent = new Intent(VerifikasiOtpActivity.this, RestartPasswordActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Kode OTP Salah! Cek kembali WA kamu.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}