package learn.fpoly.fhotel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import learn.fpoly.fhotel.Fragment.BookingFragment;
import learn.fpoly.fhotel.R;

public class PaymentNotification extends AppCompatActivity {
    TextView txtNotification;
    Button btnbackhome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_notification);

        txtNotification = findViewById(R.id.textViewNotify);
        btnbackhome = findViewById(R.id.btn_payment_backhome);

        Intent intent = getIntent();
        if (intent != null) {
            String notificationMessage = intent.getStringExtra("result");
            if (notificationMessage != null) {
                txtNotification.setText(notificationMessage);
            }
        }
        btnbackhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentNotification.this, Home_User.class);
                intent.putExtra("fragment_to_load", R.id.navBooking_u);
                startActivity(intent);
                finish();
            }
        });
    }
//    private void replaceFragment(Fragment fragment) {
//        // Thực hiện thay thế Fragment trong Activity
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.navBooking_u, fragment); // R.id.fragmentContainer là container nơi bạn muốn hiển thị Fragment
//        transaction.addToBackStack(null); // Thêm vào back stack nếu muốn cho phép quay lại
//        transaction.commit();
//    }
}