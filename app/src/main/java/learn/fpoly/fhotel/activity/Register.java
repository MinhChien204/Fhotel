package learn.fpoly.fhotel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import learn.fpoly.fhotel.R;

public class Register extends AppCompatActivity {
    AppCompatButton btn_REGISTER;
    TextView txt_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        btn_REGISTER = findViewById(R.id.btn_REGISTER);
        txt_login = findViewById(R.id.txt_login);
        btn_REGISTER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this , Login.class);
                startActivity(intent);
            }
        });
        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this , Login.class);
                startActivity(intent);
            }
        });
    }
}