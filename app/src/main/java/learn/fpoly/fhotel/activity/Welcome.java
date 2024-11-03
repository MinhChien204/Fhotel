package learn.fpoly.fhotel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import learn.fpoly.fhotel.R;

public class Welcome extends AppCompatActivity {
    private static final int DATN = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(Welcome.this, navigation.class );
                startActivity(intent);
                finish();
        }, DATN );
    }
}