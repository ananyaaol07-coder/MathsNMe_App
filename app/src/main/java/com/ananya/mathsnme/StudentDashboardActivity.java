package com.ananya.mathsnme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class StudentDashboardActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_student_dashboard);
        View root = findViewById(R.id.student_dashboard_root);
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
        ViewCompat.requestApplyInsets(root);
        WindowCompat.getInsetsController(getWindow(), root).setAppearanceLightStatusBars(true);
        WindowCompat.getInsetsController(getWindow(), root).setAppearanceLightNavigationBars(true);
        findViewById(R.id.student_menu).setOnClickListener(v -> sidebar(true));
        findViewById(R.id.student_sidebar_close).setOnClickListener(v -> sidebar(false));
        findViewById(R.id.student_sidebar_scrim).setOnClickListener(v -> sidebar(false));
        findViewById(R.id.student_profile).setOnClickListener(v -> profile(true));
        findViewById(R.id.student_profile_card).setOnClickListener(v -> profile(false));
        findViewById(R.id.student_logout).setOnClickListener(v -> logout());
        findViewById(R.id.student_profile_logout).setOnClickListener(v -> logout());
        findViewById(R.id.student_practice).setOnClickListener(v -> openWebsite("/dashboard/practice", "Practice"));
        findViewById(R.id.student_vedic).setOnClickListener(v -> openWebsite("/vedic-preview", "Vedic Mathematics"));
        findViewById(R.id.student_ib).setOnClickListener(v -> openWebsite("/ib-preview", "IB Mathematics"));
        findViewById(R.id.student_nav_overview).setOnClickListener(v -> openWebsite("/dashboard", "Overview"));
        findViewById(R.id.student_nav_vedic).setOnClickListener(v -> openWebsite("/vedic-preview", "Vedic Mathematics"));
        findViewById(R.id.student_nav_ib).setOnClickListener(v -> openWebsite("/ib-preview", "IB Mathematics"));
        findViewById(R.id.student_nav_progress).setOnClickListener(v -> openWebsite("/dashboard", "Progress"));
        findViewById(R.id.student_nav_reports).setOnClickListener(v -> openWebsite("/dashboard/reports", "Reports"));
        findViewById(R.id.student_nav_payments).setOnClickListener(v -> openWebsite("/profile", "Payments"));
        findViewById(R.id.student_nav_shop).setOnClickListener(v -> openWebsite("/topics", "Shop"));
        findViewById(R.id.student_nav_settings).setOnClickListener(v -> openWebsite("/profile", "Settings"));
    }

    private void sidebar(boolean visible) {
        findViewById(R.id.student_sidebar).setVisibility(visible ? View.VISIBLE : View.GONE);
        findViewById(R.id.student_sidebar_scrim).setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void profile(boolean visible) {
        findViewById(R.id.student_profile_card).setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void openWebsite(String path, String title) {
        Intent intent = new Intent(this, WebPortalActivity.class);
        intent.putExtra(WebPortalActivity.EXTRA_PATH, path);
        intent.putExtra(WebPortalActivity.EXTRA_TITLE, title);
        startActivity(intent);
    }

    private void openTopic(String topic) {
        Intent intent = new Intent(this, LearningTopicActivity.class);
        intent.putExtra(LearningTopicActivity.EXTRA_TOPIC, topic);
        startActivity(intent);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        getSharedPreferences("session", MODE_PRIVATE).edit().clear().apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
