package com.ananya.mathsnme;

import android.graphics.Rect;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;

import java.util.Locale;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int GOLD = 0xFFD4AF37;
    private static final int DARK_GOLD = 0xFFB8860B;
    private static final long DEMO_STEP_MS = 90L;
    private final List<ObjectAnimator> heroAnimators = new ArrayList<>();
    private final Runnable visibilityCheck = this::checkDemoVisibility;
    private boolean started;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final String[] subtractionSteps = {"5148", "4368", "3588", "2808", "2028", "1248", "468", "312", "156", "156"};
    private NestedScrollView scroll;
    private boolean demoVisible;
    private Runnable demoTick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
        androidx.core.view.WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(true);
        androidx.core.view.WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightNavigationBars(true);
        colourPhrase(R.id.hero_heading, "Vedic Maths", GOLD);
        colourPhrase(R.id.approach_heading, "A fraction of the time.", GOLD);
        scroll = findViewById(R.id.landing_scroll);
        findViewById(R.id.explore_paths).setOnClickListener(v -> scrollTo(R.id.learning_paths));
        findViewById(R.id.see_roadmap).setOnClickListener(v -> scrollTo(R.id.roadmap));
        findViewById(R.id.talk_to_us).setOnClickListener(v -> scrollTo(R.id.footer));
        findViewById(R.id.landing_logo).setOnClickListener(v -> scroll.smoothScrollTo(0, 0));
        findViewById(R.id.landing_login).setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));
        findViewById(R.id.landing_register).setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
        for (int id : new int[]{R.id.start_learning, R.id.cta_start}) {
            findViewById(id).setOnClickListener(v -> showNextScreenNotice(false));
        }
        findViewById(R.id.run_demo).setOnClickListener(v -> runDemo());
        scroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, x, y, oldX, oldY) -> checkDemoVisibility());

    }

    @Override
    protected void onStart() {
        super.onStart();
        started = true;
        startHeroFloating();
        // Wait for the restored scroll position and layout before checking visibility.
        scroll.post(visibilityCheck);
    }

    private void startHeroFloating() {
        if (!heroAnimators.isEmpty()) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                && !ValueAnimator.areAnimatorsEnabled()) return;
        int[] cards = {R.id.hero_card_worksheet, R.id.hero_card_ai,
                R.id.hero_card_progress, R.id.hero_card_level};
        float distance = 4f * getResources().getDisplayMetrics().density;
        for (int i = 0; i < cards.length; i++) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(
                    findViewById(cards[i]), View.TRANSLATION_Y, 0f, -distance);
            animator.setDuration(1800L);
            animator.setStartDelay(i * 450L);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.REVERSE);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            heroAnimators.add(animator);
            animator.start();
        }
    }

    private void stopHeroFloating() {
        for (ObjectAnimator animator : heroAnimators) {
            animator.cancel();
            View card = (View) animator.getTarget();
            if (card != null) card.setTranslationY(0f);
        }
        heroAnimators.clear();
    }

    private void colourPhrase(int id, String phrase, int colour) {
        TextView view = findViewById(id);
        SpannableString text = new SpannableString(view.getText());
        int start = text.toString().indexOf(phrase);
        if (start >= 0) text.setSpan(new ForegroundColorSpan(colour), start, start + phrase.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setText(text);
    }

    private void scrollTo(int id) {
        View target = findViewById(id);
        Rect bounds = new Rect();
        target.getDrawingRect(bounds);
        scroll.offsetDescendantRectToMyCoords(target, bounds);
        scroll.smoothScrollTo(0, bounds.top);
    }

    private void showNextScreenNotice(boolean login) {
        // Replace this notice with navigation when authentication screens are available.
        new AlertDialog.Builder(this)
                .setTitle(login ? R.string.landing_login : R.string.landing_register)
                .setMessage(R.string.landing_next_screen)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void checkDemoVisibility() {
        if (!started) return;
        View card = findViewById(R.id.shortcut_demo);
        Rect visible = new Rect();
        boolean shown = card.getHeight() > 0 && card.getLocalVisibleRect(visible)
                && visible.height() >= card.getHeight() / 4;
        if (shown && !demoVisible) runDemo();
        demoVisible = shown;
    }

    private void runDemo() {
        if (!started) return;
        if (demoTick != null) handler.removeCallbacks(demoTick);
        TextView equation = findViewById(R.id.demo_equation);
        TextView time = findViewById(R.id.demo_time);
        TextView description = findViewById(R.id.demo_description);
        View replay = findViewById(R.id.run_demo);
        replay.setEnabled(false);
        time.setText(String.format(Locale.getDefault(), "%.2fs", 0.0));
        equation.setText(R.string.demo_equation);
        description.setText(R.string.demo_description);
        final long start = android.os.SystemClock.uptimeMillis();
        demoTick = new Runnable() {
            @Override public void run() {
                long elapsed = android.os.SystemClock.uptimeMillis() - start;
                int count = Math.min((int) (elapsed / DEMO_STEP_MS), subtractionSteps.length);
                time.setText(String.format(Locale.getDefault(), "%.2fs", elapsed / 1000.0));
                if (count > 0) {
                    equation.setText(getString(R.string.demo_equation_value, subtractionSteps[count - 1]));
                    colourPhrase(R.id.demo_equation, subtractionSteps[count - 1], DARK_GOLD);
                }
                if (count == subtractionSteps.length) {
                    description.setText(R.string.demo_complete);
                    replay.setEnabled(true);
                    demoTick = null;
                } else handler.postDelayed(this, 40);
            }
        };
        handler.post(demoTick);
    }

    @Override
    protected void onStop() {
        started = false;
        scroll.removeCallbacks(visibilityCheck);
        stopHeroFloating();
        if (demoTick != null) {
            handler.removeCallbacks(demoTick);
            demoTick = null;
        }
        findViewById(R.id.run_demo).setEnabled(true);
        demoVisible = false;
        super.onStop();
    }

    @Override protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
