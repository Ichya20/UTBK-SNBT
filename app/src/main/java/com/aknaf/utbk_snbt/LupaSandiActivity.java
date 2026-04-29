package com.aknaf.utbk_snbt;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LupaSandiActivity extends AppCompatActivity {

    EditText etEmailAtauWA;
    Button btnKirimOtp;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lupa_sandi);

        mAuth = FirebaseAuth.getInstance();
        etEmailAtauWA = findViewById(R.id.etEmailAtauWA);
        btnKirimOtp = findViewById(R.id.btnKirimOtp);

        btnKirimOtp.setOnClickListener(v -> {
            String email = etEmailAtauWA.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                etEmailAtauWA.setError("Masukkan email yang terdaftar!");
                return;
            }

            // PROSES KIRIM EMAIL RESET PASSWORD ASLI DARI GOOGLE
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LupaSandiActivity.this,
                                    "Link reset sandi sudah dikirim ke Gmail kamu!",
                                    Toast.LENGTH_LONG).show();

                            // Setelah sukses, arahkan balik ke halaman Login
                            finish();
                        } else {
                            Toast.makeText(LupaSandiActivity.this,
                                    "Gagal mengirim: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}