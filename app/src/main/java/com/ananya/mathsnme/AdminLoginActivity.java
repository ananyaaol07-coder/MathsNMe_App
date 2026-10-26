package com.ananya.mathsnme;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

public class AdminLoginActivity extends AppCompatActivity {
    private static final String ADMIN_EMAIL = "mathsnme2020@gmail.com";
    private TextInputEditText password;
    private TextInputLayout passwordLayout;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_admin_login);
        View root = findViewById(R.id.admin_login_root);
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
        WindowCompat.getInsetsController(getWindow(), root).setAppearanceLightStatusBars(true);
        WindowCompat.getInsetsController(getWindow(), root).setAppearanceLightNavigationBars(true);
        password = findViewById(R.id.admin_login_password);
        passwordLayout = findViewById(R.id.admin_login_password_layout);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        findViewById(R.id.admin_login_student).setOnClickListener(view -> finish());
        findViewById(R.id.admin_login_submit).setOnClickListener(view -> signIn());
    }

    private void signIn() {
        String value = password.getText() == null ? "" : password.getText().toString();
        passwordLayout.setError(value.isEmpty() ? getString(R.string.login_required_password) : null);
        if (value.isEmpty()) return;
        findViewById(R.id.admin_login_submit).setEnabled(false);
        firebaseAuth.signInWithEmailAndPassword(ADMIN_EMAIL, value).addOnCompleteListener(task -> {
            findViewById(R.id.admin_login_submit).setEnabled(true);
            if (!task.isSuccessful()) {
                passwordLayout.setError(getString(R.string.login_admin_failed));
                return;
            }
            verifyAdmin();
        });
    }

    private void verifyAdmin() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) { passwordLayout.setError(getString(R.string.login_admin_failed)); return; }
        firestore.collection("admins").document(user.getUid()).get(Source.SERVER)
                .addOnSuccessListener(document -> {
                    if (Boolean.TRUE.equals(document.getBoolean("active"))) {
                        startActivity(new Intent(this, AdminDashboardActivity.class));
                        finish();
                    } else {
                        firebaseAuth.signOut();
                        passwordLayout.setError(getString(R.string.login_admin_failed));
                    }
                })
                .addOnFailureListener(error -> { firebaseAuth.signOut(); passwordLayout.setError(getString(R.string.login_admin_failed)); });
    }
}
