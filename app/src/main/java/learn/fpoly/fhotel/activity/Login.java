package learn.fpoly.fhotel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import learn.fpoly.fhotel.Model.LoginRequest;
import learn.fpoly.fhotel.response.LoginResponse;
import learn.fpoly.fhotel.response.Response;
import learn.fpoly.fhotel.Model.User;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.Retrofit.HttpRequest;
import retrofit2.Call;
import retrofit2.Callback;

public class Login extends AppCompatActivity {
    AppCompatButton btn_LOGIN;
    TextView txt_register_now;
    HttpRequest httpRequest;
    EditText edt_Email_login,edt_password_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edt_Email_login=findViewById(R.id.edt_Email_login);
        edt_password_login=findViewById(R.id.edt_password_login);
        btn_LOGIN = findViewById(R.id.btn_LOGIN);
        txt_register_now = findViewById(R.id.txt_register_now);
        httpRequest = new HttpRequest();
        btn_LOGIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = edt_Email_login.getText().toString().trim();
                String password = edt_password_login.getText().toString().trim();


                handleLogin(username, password);
            }
        });
        txt_register_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this , Register.class);
                startActivity(intent);
            }
        });
    }
    public void handleLogin(String username, String password) {
        Call<LoginResponse> call = httpRequest.callAPI().login(new LoginRequest(username, password));
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse userResponse = response.body(); // Lấy đối tượng Response
                    if (userResponse.getStatus() == 200) {
                        int idUser = userResponse.getRole();

                        Log.d("iduser", "onResponse: " + idUser);

                        // Sử dụng switch-case để chuyển màn hình
                        switch (idUser) {
                            case 0:
                                // Chuyển sang TrangChuAdmin nếu idUser = 0
                                Intent adminIntent = new Intent(Login.this, TrangChuAdmin.class);
                                startActivity(adminIntent);
                                break;

                            case 1:
                                // Chuyển sang Home_User nếu idUser = 1
                                Intent userIntent = new Intent(Login.this, Home_User.class);
                                startActivity(userIntent);
                                break;

                            default:
                                Log.d("Login", "User role not recognized");
                                break;
                        }
                        finish();
                    } else {
                        Log.d("Login", "Login failed: " + userResponse.getMessenger());
                    }
                } else {
                    Log.d("Login", "Response unsuccessful or empty");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable throwable) {
                Log.e("Login", "API call failed: " + throwable.getMessage());
            }
        });
    }

}