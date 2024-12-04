package learn.fpoly.fhotel.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import learn.fpoly.fhotel.R;

public class VnPayment extends AppCompatActivity {
    private WebView webView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vn_payment);

        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);

//        webView.setWebViewClient(new WebViewClient());

        // Lấy URL từ Intent
        String paymentUrl = getIntent().getStringExtra("paymentUrl");

        if (paymentUrl != null) {
            webView.loadUrl(paymentUrl);
        } else {
            Toast.makeText(this, "Payment URL is invalid", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}