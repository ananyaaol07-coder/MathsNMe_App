package com.ananya.mathsnme;

import android.os.Bundle;
import android.content.Intent;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.CheckBox;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

public class LoginActivity extends AppCompatActivity {
    private static final String ADMIN_EMAIL = "mathsnme2020@gmail.com";
    private boolean adminLogin;
    private TextInputEditText email;
    private TextInputEditText password;
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_login);
        View root = findViewById(R.id.login_root);
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets keyboard = insets.getInsets(WindowInsetsCompat.Type.ime());
            view.setPadding(bars.left, bars.top, bars.right, Math.max(bars.bottom, keyboard.bottom));
            return insets;
        });
        WindowCompat.getInsetsController(getWindow(), root).setAppearanceLightStatusBars(true);
        WindowCompat.getInsetsController(getWindow(), root).setAppearanceLightNavigationBars(true);
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        emailLayout = findViewById(R.id.login_email_layout);
        passwordLayout = findViewById(R.id.login_password_layout);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        ((CheckBox) findViewById(R.id.login_remember)).setChecked(
                getSharedPreferences("session", MODE_PRIVATE).getBoolean("remember_login", true));
        adminLogin = savedInstanceState != null && savedInstanceState.getBoolean("admin_login");
        updateMode();
        findViewById(R.id.login_home).setOnClickListener(v -> finish());
        findViewById(R.id.login_logo).setOnClickListener(v -> finish());
        findViewById(R.id.login_mode_toggle).setOnClickListener(v -> startActivity(new Intent(this, AdminLoginActivity.class)));
        findViewById(R.id.login_submit).setOnClickListener(v -> submitLogin());
        password.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) { submitLogin(); return true; }
            return false;
        });
        findViewById(R.id.login_create).setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        findViewById(R.id.login_forgot).setOnClickListener(v -> sendPasswordReset());
    }

    private void updateMode() {
        ((TextView) findViewById(R.id.login_mode_label)).setText(adminLogin ? R.string.login_admin_label : R.string.login_student_label);
        ((TextView) findViewById(R.id.login_title)).setText(adminLogin ? R.string.login_admin_title : R.string.login_title);
        ((TextView) findViewById(R.id.login_mode_toggle)).setText(adminLogin ? R.string.login_student_switch : R.string.login_admin);
    }

    private void submitLogin() {
        String address = adminLogin ? ADMIN_EMAIL : (email.getText() == null ? "" : email.getText().toString().trim());
        boolean validEmail = adminLogin || Patterns.EMAIL_ADDRESS.matcher(address).matches();
        boolean hasPassword = password.getText() != null && password.getText().length() > 0;
        emailLayout.setError(validEmail ? null : getString(R.string.login_invalid_email));
        passwordLayout.setError(hasPassword ? null : getString(R.string.login_required_password));
        if (!validEmail) { email.requestFocus(); return; }
        if (!hasPassword) { password.requestFocus(); return; }
        findViewById(R.id.login_submit).setEnabled(false);
        firebaseAuth.signInWithEmailAndPassword(address, password.getText().toString())
                .addOnCompleteListener(task -> {
                    findViewById(R.id.login_submit).setEnabled(true);
                    if (task.isSuccessful() && adminLogin) {
                        verifyAdminAccess();
                    } else if (task.isSuccessful()) {
                        saveSession("student");
                        startActivity(new Intent(this, StudentDashboardActivity.class));
                        finish();
                    } else {
                        passwordLayout.setError(loginFailureMessage(task.getException()));
                    }
                });
    }

    private void saveSession(String role) {
        boolean remember = ((CheckBox) findViewById(R.id.login_remember)).isChecked();
        getSharedPreferences("session", MODE_PRIVATE).edit()
                .putBoolean("remember_login", remember)
                .putString("role", role).apply();
    }

    private String loginFailureMessage(Exception error) {
        if (error instanceof FirebaseAuthException) {
            String code = ((FirebaseAuthException) error).getErrorCode();
            if ("ERROR_INVALID_LOGIN_CREDENTIALS".equals(code)
                    || "ERROR_WRONG_PASSWORD".equals(code)
                    || "ERROR_USER_NOT_FOUND".equals(code)) {
                return getString(R.string.login_invalid_credentials);
            }
            if ("ERROR_NETWORK_REQUEST_FAILED".equals(code)) {
                return getString(R.string.login_network_failed);
            }
        }
        return getString(R.string.login_failed);
    }

    private void verifyAdminAccess() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            passwordLayout.setError(getString(R.string.login_admin_failed));
            return;
        }
        firestore.collection("admins").document(user.getUid()).get(Source.SERVER)
                .addOnSuccessListener(document -> {
                    if (Boolean.TRUE.equals(document.getBoolean("active"))) {
                        showNotice(R.string.login_admin_success_title, R.string.login_admin_success);
                    } else {
                        firebaseAuth.signOut();
                        passwordLayout.setError(getString(R.string.login_admin_failed));
                    }
                })
                .addOnFailureListener(error -> {
                    firebaseAuth.signOut();
                    passwordLayout.setError(getString(R.string.login_admin_failed));
                });
    }

    private void sendPasswordReset() {
        String address = email.getText() == null ? "" : email.getText().toString().trim();
        if (!Patterns.EMAIL_ADDRESS.matcher(address).matches()) {
            emailLayout.setError(getString(R.string.login_invalid_email));
            email.requestFocus();
            return;
        }
        firebaseAuth.sendPasswordResetEmail(address).addOnCompleteListener(task ->
                showNotice(R.string.login_reset_title,
                        task.isSuccessful() ? R.string.login_reset_sent : R.string.login_reset_failed));
    }

    private void showNotice(int title, int message) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(message)
                .setPositiveButton(android.R.string.ok, null).show();
    }

    @Override protected void onSaveInstanceState(Bundle state) {
        state.putBoolean("admin_login", adminLogin);
        super.onSaveInstanceState(state);
    }
}
