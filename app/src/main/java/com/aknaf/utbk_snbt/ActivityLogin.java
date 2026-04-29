package com.aknaf.utbk_snbt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // Tambahan log
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class ActivityLogin extends AppCompatActivity {

    EditText email, password;
    Button btnMasuk, btnDaftar, btnGoogle;
    TextView lupaSandi;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        String myWebClientId = "429157683797-k2iv0ckt6a7t27lnlt0h1oe9dbujnk82.apps.googleusercontent.com";

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(myWebClientId)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(ActivityLogin.this, MainActivity.class));
            finish();
            return;
        }

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnMasuk = findViewById(R.id.btnMasuk);
        btnDaftar = findViewById(R.id.btnDaftar);
        lupaSandi = findViewById(R.id.lupaSandi);
        btnGoogle = findViewById(R.id.btnGoogle);

        // FIX: Tombol Lupa Sandi sekarang berfungsi
        lupaSandi.setOnClickListener(view -> {
            Intent intent = new Intent(ActivityLogin.this, LupaSandiActivity.class);
            startActivity(intent);
        });

        btnGoogle.setOnClickListener(view -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        btnMasuk.setOnClickListener(view -> {
            String inputEmail = email.getText().toString().trim();
            String inputPassword = password.getText().toString().trim();
            if (!inputEmail.isEmpty() && !inputPassword.isEmpty()) {
                loginManual(inputEmail, inputPassword);
            }
        });

        btnDaftar.setOnClickListener(v -> startActivity(new Intent(this, DaftarActivity.class)));
    }

    private void loginManual(String email, String pass) {
        btnMasuk.setEnabled(false);
        btnMasuk.setText("Loading...");
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                btnMasuk.setEnabled(true);
                btnMasuk.setText("Masuk");
                Toast.makeText(this, "Gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Log tambahan untuk cek error aslinya di Logcat
                Log.e("DEBUG_GOOGLE", "Status Code: " + e.getStatusCode());
                Toast.makeText(this, "Gagal Login Google (Code: " + e.getStatusCode() + ")", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                startActivity(new Intent(ActivityLogin.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Gagal Autentikasi Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }
}