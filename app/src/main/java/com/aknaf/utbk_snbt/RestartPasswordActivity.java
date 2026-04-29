package com.aknaf.utbk_snbt;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RestartPasswordActivity extends AppCompatActivity {

    EditText etPasswordBaru, etUlangPassword;
    Button btnSimpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restart_password);

        etPasswordBaru = findViewById(R.id.etPasswordBaru);
        etUlangPassword = findViewById(R.id.etUlangPassword);
        btnSimpan = findViewById(R.id.btnSimpan);

        btnSimpan.setOnClickListener(v -> {

            String passwordBaru = etPasswordBaru.getText().toString().trim();
            String ulangPassword = etUlangPassword.getText().toString().trim();

            if (TextUtils.isEmpty(passwordBaru) || TextUtils.isEmpty(ulangPassword)) {
                Toast.makeText(this, "Password tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!passwordBaru.equals(ulangPassword)) {
                Toast.makeText(this, "Password tidak sama!", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Password berhasil diubah!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(RestartPasswordActivity.this, ActivityLogin.class);
            startActivity(intent);
            finish();
        });
    }
}