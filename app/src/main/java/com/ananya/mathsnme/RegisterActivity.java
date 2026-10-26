package com.ananya.mathsnme;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

public class RegisterActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_register);
        WindowCompat.getInsetsController(getWindow(), findViewById(R.id.register_root)).setAppearanceLightStatusBars(true);
        WindowCompat.getInsetsController(getWindow(), findViewById(R.id.register_root)).setAppearanceLightNavigationBars(true);
        findViewById(R.id.register_home).setOnClickListener(v -> finish());
        findViewById(R.id.register_logo).setOnClickListener(v -> finish());
        findViewById(R.id.register_submit).setOnClickListener(v -> submit());
    }
    private void submit() {
        EditText parent = findViewById(R.id.register_parent_name);
        EditText email = findViewById(R.id.register_parent_email);
        parent.setError(parent.getText().toString().trim().isEmpty() ? "Enter your name" : null);
        email.setError(Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches() ? null : "Enter a valid email address");
        if (parent.getError() != null) { parent.requestFocus(); return; }
        if (email.getError() != null) { email.requestFocus(); return; }
        new AlertDialog.Builder(this).setTitle(R.string.register_notice_title).setMessage(R.string.register_notice).setPositiveButton(android.R.string.ok, null).show();
    }
}
