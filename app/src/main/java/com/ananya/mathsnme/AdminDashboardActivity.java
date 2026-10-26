package com.ananya.mathsnme;

import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminDashboardActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_admin_dashboard);
        WindowCompat.getInsetsController(getWindow(), findViewById(R.id.admin_dashboard_root)).setAppearanceLightStatusBars(true);
        WindowCompat.getInsetsController(getWindow(), findViewById(R.id.admin_dashboard_root)).setAppearanceLightNavigationBars(true);
        View signOut = findViewById(R.id.admin_sign_out);
        signOut.setOnClickListener(view -> signOut());
        findViewById(R.id.admin_sidebar_sign_out).setOnClickListener(view -> signOut());
        findViewById(R.id.admin_menu).setOnClickListener(view -> setSidebarVisible(true));
        findViewById(R.id.admin_sidebar_close).setOnClickListener(view -> setSidebarVisible(false));
        findViewById(R.id.admin_sidebar_scrim).setOnClickListener(view -> setSidebarVisible(false));
        FirebaseFirestore.getInstance().collection("users").addSnapshotListener((snapshot, error) -> {
            if (error != null || snapshot == null) {
                ((TextView) findViewById(R.id.admin_registered_count)).setText("—");
                ((TextView) findViewById(R.id.admin_grade_seven_count)).setText("—");
                return;
            }
            ((TextView) findViewById(R.id.admin_registered_count)).setText(String.valueOf(snapshot.size()));
            int gradeSeven = 0;
            for (com.google.firebase.firestore.DocumentSnapshot document : snapshot.getDocuments()) {
                Long grade = document.getLong("grade");
                if (grade != null && grade == 7) gradeSeven++;
            }
            ((TextView) findViewById(R.id.admin_grade_seven_count)).setText(gradeSeven == 0 ? "—" : String.valueOf(gradeSeven));
        });
    }

    private void signOut() {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, AdminLoginActivity.class));
            finish();
    }

    private void setSidebarVisible(boolean visible) {
        findViewById(R.id.admin_sidebar).setVisibility(visible ? View.VISIBLE : View.GONE);
        findViewById(R.id.admin_sidebar_scrim).setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
