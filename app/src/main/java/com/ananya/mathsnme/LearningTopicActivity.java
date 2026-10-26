package com.ananya.mathsnme;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.graphics.drawable.GradientDrawable;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class LearningTopicActivity extends AppCompatActivity {
    public static final String EXTRA_TOPIC = "topic";

    private void addGradeCards() {
        LinearLayout holder = findViewById(R.id.grade_cards);
        for (int grade = 3; grade <= 12; grade++) {
            boolean active = grade == 6 || grade == 7 || grade == 9;
            LinearLayout card = new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL); card.setPadding(dp(32), dp(28), dp(32), dp(28));
            GradientDrawable bg = new GradientDrawable(); bg.setColor(Color.WHITE); bg.setCornerRadius(dp(30)); bg.setStroke(dp(1), Color.rgb(232, 210, 153)); card.setBackground(bg);
            LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(-1, -2); cp.setMargins(dp(28), dp(12), dp(28), dp(12)); holder.addView(card, cp);
            TextView title = item("Grade " + grade, 31, Color.rgb(10,20,47), true); card.addView(title);
            String desc = grade == 7 ? "Current grade · exercises required" : grade == 6 ? "Revision grade · chapter tests available" : grade == 9 ? "Admin-approved access" : grade == 3 ? "Request declined · contact admin" : "Explore with admin approval";
            TextView description = item(desc, 19, Color.rgb(82,105,143), false); LinearLayout.LayoutParams dp = new LinearLayout.LayoutParams(-1,-2); dp.topMargin=dp(12); card.addView(description, dp);
            TextView action = item(active ? "Explore grade                                      →" : (grade == 3 ? "Contact admin" : "Request access"), 18, Color.rgb(148,104,0), true); action.setGravity(active ? android.view.Gravity.CENTER_VERTICAL : android.view.Gravity.CENTER); action.setPadding(dp(20),0,dp(20),0); GradientDrawable button = new GradientDrawable(); button.setCornerRadius(dp(16)); button.setColor(active ? Color.rgb(255,241,191) : Color.WHITE); button.setStroke(dp(1),Color.rgb(224,190,91)); action.setBackground(button); LinearLayout.LayoutParams ap = new LinearLayout.LayoutParams(-1,dp(62)); ap.topMargin=dp(24); card.addView(action,ap);
        }
    }
    private TextView item(String s, int size, int color, boolean bold) { TextView t=new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color); if(bold)t.setTypeface(android.graphics.Typeface.DEFAULT_BOLD); return t; }
    private int dp(int n) { return (int)(n*getResources().getDisplayMetrics().density+.5f); }

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            return;
        }
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_learning_topic);
        View root = findViewById(R.id.learning_topic_root);
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
        ViewCompat.requestApplyInsets(root);
        WindowCompat.getInsetsController(getWindow(), root).setAppearanceLightStatusBars(true);
        WindowCompat.getInsetsController(getWindow(), root).setAppearanceLightNavigationBars(true);
        boolean vedic = "vedic".equals(getIntent().getStringExtra(EXTRA_TOPIC));
        ((TextView) findViewById(R.id.learning_topic_title)).setText(vedic ? "Vedic Mathematics" : "IB Mathematics");
        if (!vedic) addGradeCards();
        ((TextView) findViewById(R.id.learning_topic_eyebrow)).setText(vedic ? "VEDIC MATHEMATICS" : "IB MATHEMATICS");
        ((TextView) findViewById(R.id.learning_topic_heading)).setText(vedic ? "Build speed and confidence." : "Build conceptual confidence.");
        ((TextView) findViewById(R.id.learning_topic_description)).setText(vedic
                ? "Practise calculation techniques through guided levels and short worksheets."
                : "Explore structured lessons and problem-solving practice at your grade level.");
        ((TextView) findViewById(R.id.learning_topic_button)).setText(vedic ? "Start Vedic practice  →" : "Explore IB lessons  →");
        findViewById(R.id.learning_topic_back).setOnClickListener(v -> finish());
        findViewById(R.id.learning_topic_button).setOnClickListener(v -> { });
    }
}
