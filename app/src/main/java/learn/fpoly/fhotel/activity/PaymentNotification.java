package learn.fpoly.fhotel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import learn.fpoly.fhotel.R;

public class PaymentNotification extends AppCompatActivity {
    TextView txtNotification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_notification);

        txtNotification = findViewById(R.id.textViewNotify);

        Intent intent = getIntent();
        if (intent != null) {
            String notificationMessage = intent.getStringExtra("paynotification");
            if (notificationMessage != null) {
                txtNotification.setText(notificationMessage);
            }
        }
    }
}