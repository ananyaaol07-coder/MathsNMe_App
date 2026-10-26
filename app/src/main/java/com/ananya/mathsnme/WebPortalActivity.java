package com.ananya.mathsnme;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceError;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class WebPortalActivity extends AppCompatActivity {
    public static final String EXTRA_PATH = "path";
    public static final String EXTRA_TITLE = "title";
    private static final String LOCAL_SITE = "http://10.0.2.2:3000";
    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_web_portal);
        View root = findViewById(R.id.web_portal_root);
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
        ViewCompat.requestApplyInsets(root);
        WindowCompat.getInsetsController(getWindow(), root).setAppearanceLightStatusBars(true);
        WindowCompat.getInsetsController(getWindow(), root).setAppearanceLightNavigationBars(true);
        webView = findViewById(R.id.web_portal_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override public void onPageStarted(WebView view, String url, Bitmap icon) {
                findViewById(R.id.web_portal_progress).setVisibility(View.VISIBLE);
            }
            @Override public void onPageFinished(WebView view, String url) {
                findViewById(R.id.web_portal_progress).setVisibility(View.GONE);
            }
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
            @Override public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (request.isForMainFrame()) showLocalServerError("Start the MathsNMe website on your Mac with npm run dev, then open this section again.");
            }
        });
        String path = getIntent().getStringExtra(EXTRA_PATH);
        openWebsite(path == null ? "/" : path);
    }

    private void openWebsite(String path) {
        webView.loadUrl(LOCAL_SITE + path);
    }

    private void showLocalServerError(String message) {
        findViewById(R.id.web_portal_progress).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.web_portal_error_text)).setText(message);
        findViewById(R.id.web_portal_error).setVisibility(View.VISIBLE);
    }

    private void goBack() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else finish();
    }

    @Override public void onBackPressed() { goBack(); }
}
