package com.ananya.mathsnme;

import android.os.Bundle;
import android.content.Intent;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private boolean adminLogin;
    private TextInputEditText email;
    private TextInputEditText password;
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private FirebaseAuth firebaseAuth;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
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
        adminLogin = savedInstanceState != null && savedInstanceState.getBoolean("admin_login");
        updateMode();
        findViewById(R.id.login_home).setOnClickListener(v -> finish());
        findViewById(R.id.login_logo).setOnClickListener(v -> finish());
        findViewById(R.id.login_mode_toggle).setOnClickListener(v -> { adminLogin = !adminLogin; updateMode(); });
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
        String address = email.getText() == null ? "" : email.getText().toString().trim();
        boolean validEmail = Patterns.EMAIL_ADDRESS.matcher(address).matches();
        boolean hasPassword = password.getText() != null && password.getText().length() > 0;
        emailLayout.setError(validEmail ? null : getString(R.string.login_invalid_email));
        passwordLayout.setError(hasPassword ? null : getString(R.string.login_required_password));
        if (!validEmail) { email.requestFocus(); return; }
        if (!hasPassword) { password.requestFocus(); return; }
        findViewById(R.id.login_submit).setEnabled(false);
        firebaseAuth.signInWithEmailAndPassword(address, password.getText().toString())
                .addOnCompleteListener(task -> {
                    findViewById(R.id.login_submit).setEnabled(true);
                    if (task.isSuccessful()) {
                        showNotice(R.string.login_success_title, R.string.login_success);
                    } else {
                        passwordLayout.setError(getString(R.string.login_failed));
                    }
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
