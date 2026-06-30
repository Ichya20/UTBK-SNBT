package com.aknaf.utbk_snbt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aknaf.utbk_snbt.ads.InterstitialAdManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ActivityLogin extends AppCompatActivity {

    EditText email, password;
    Button btnMasuk, btnDaftar, btnGoogle;
    TextView lupaSandi, btnAdmin, txtVersiBeta;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        InterstitialAdManager.loadAd(this);

        String myWebClientId = "429157683797-k2iv0ckt6a7t27lnlt0h1oe9dbujnk82.apps.googleusercontent.com";

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(myWebClientId)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        if (mAuth.getCurrentUser() != null) {
            redirectAfterLogin(mAuth.getCurrentUser(), "auto_login");
            return;
        }

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnMasuk = findViewById(R.id.btnMasuk);
        btnDaftar = findViewById(R.id.btnDaftar);
        lupaSandi = findViewById(R.id.lupaSandi);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnAdmin = findViewById(R.id.btnAdmin);
        txtVersiBeta = findViewById(R.id.txtVersiBeta);

        if (txtVersiBeta != null) {
            txtVersiBeta.setText("VERSION " + BuildConfig.VERSION_NAME);
        }

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

            if (inputEmail.isEmpty() || inputPassword.isEmpty()) {
                Toast.makeText(this, "Email dan password wajib diisi.", Toast.LENGTH_SHORT).show();
                return;
            }

            loginManual(inputEmail, inputPassword);
        });

        btnDaftar.setOnClickListener(v -> startActivity(new Intent(this, DaftarActivity.class)));

        // Admin sekarang berbasis mobile dalam 1 APK, bukan membuka web.
        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent adminIntent = new Intent(ActivityLogin.this, AdminLoginActivity.class);
                startActivity(adminIntent);
            }
        });
    }

    private void loginManual(String emailInput, String pass) {
        btnMasuk.setEnabled(false);
        btnMasuk.setText("Loading...");

        mAuth.signInWithEmailAndPassword(emailInput, pass).addOnCompleteListener(task -> {
            btnMasuk.setEnabled(true);
            btnMasuk.setText("Masuk");

            if (task.isSuccessful()) {
                redirectAfterLogin(mAuth.getCurrentUser(), "email_password");
            } else {
                Toast.makeText(this, "Gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void redirectAfterLogin(FirebaseUser user, String provider) {
        if (user == null) {
            return;
        }

        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String role = documentSnapshot.exists()
                            ? documentSnapshot.getString("role")
                            : null;

                    if ("admin".equalsIgnoreCase(role)) {
                        updateAdminLastLogin(user, provider);
                        goToAdminDashboard();
                    } else {
                        simpanAtauUpdateUserLogin(user, provider);
                        goToMainWithInterstitial();
                    }
                })
                .addOnFailureListener(error -> {
                    Log.e("LOGIN_ROLE", "Gagal cek role, masuk sebagai user", error);
                    simpanAtauUpdateUserLogin(user, provider);
                    goToMainWithInterstitial();
                });
    }

    private void goToAdminDashboard() {
        Intent intent = new Intent(ActivityLogin.this, AdminDashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToMainWithInterstitial() {
        InterstitialAdManager.showAd(this, () -> {
            Intent intent = new Intent(ActivityLogin.this, MainActivity.class);
            startActivity(intent);
            finish();
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
                Log.e("DEBUG_GOOGLE", "Status Code: " + e.getStatusCode());
                Toast.makeText(this, "Gagal Login Google (Code: " + e.getStatusCode() + ")", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                redirectAfterLogin(mAuth.getCurrentUser(), "google");
            } else {
                Toast.makeText(this, "Gagal Autentikasi Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void simpanAtauUpdateUserLogin(FirebaseUser user, String provider) {
        if (user == null) return;

        Map<String, Object> dataUser = new HashMap<>();
        dataUser.put("uid", user.getUid());
        dataUser.put("email", user.getEmail() != null ? user.getEmail() : "");
        dataUser.put("provider", provider);
        dataUser.put("lastLoginAt", FieldValue.serverTimestamp());

        if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            dataUser.put("nama", user.getDisplayName());
        }

        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
            dataUser.put("telepon", user.getPhoneNumber());
        }

        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Jangan update field role di sini agar akun admin tidak berubah jadi user.
                        db.collection("users")
                                .document(user.getUid())
                                .update(dataUser)
                                .addOnSuccessListener(unused -> Log.d("USER_DATA", "Data user berhasil diupdate"))
                                .addOnFailureListener(error -> {
                                    Log.e("USER_DATA", "Gagal update data user", error);
                                    Toast.makeText(ActivityLogin.this,
                                            "Login berhasil, tapi update data user gagal: " + error.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                });
                    } else {
                        dataUser.put("createdAt", FieldValue.serverTimestamp());
                        dataUser.put("role", "user");

                        if (!dataUser.containsKey("nama")) {
                            dataUser.put("nama", "");
                        }

                        if (!dataUser.containsKey("telepon")) {
                            dataUser.put("telepon", "");
                        }

                        dataUser.put("tanggal", "");

                        db.collection("users")
                                .document(user.getUid())
                                .set(dataUser)
                                .addOnSuccessListener(unused -> Log.d("USER_DATA", "Data user berhasil dibuat"))
                                .addOnFailureListener(error -> {
                                    Log.e("USER_DATA", "Gagal membuat data user", error);
                                    Toast.makeText(ActivityLogin.this,
                                            "Login berhasil, tapi data user gagal disimpan: " + error.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                });
                    }
                })
                .addOnFailureListener(error -> {
                    Log.e("USER_DATA", "Gagal cek data user", error);
                    Toast.makeText(ActivityLogin.this,
                            "Login berhasil, tapi gagal cek data user: " + error.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void updateAdminLastLogin(FirebaseUser user, String provider) {
        if (user == null) return;

        Map<String, Object> adminUpdate = new HashMap<>();
        adminUpdate.put("uid", user.getUid());
        adminUpdate.put("email", user.getEmail() != null ? user.getEmail() : "");
        adminUpdate.put("provider", provider);
        adminUpdate.put("lastLoginAt", FieldValue.serverTimestamp());

        if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            adminUpdate.put("nama", user.getDisplayName());
        }

        db.collection("users")
                .document(user.getUid())
                .update(adminUpdate)
                .addOnSuccessListener(unused -> Log.d("ADMIN_LOGIN", "Login admin berhasil diupdate"))
                .addOnFailureListener(error -> Log.e("ADMIN_LOGIN", "Gagal update login admin", error));
    }
}
