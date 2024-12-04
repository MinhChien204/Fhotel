package learn.fpoly.fhotel.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    TextView txt_register_now, txt_forgot_Password;
    HttpRequest httpRequest;
    EditText edt_Username_login, edt_password_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edt_Username_login = findViewById(R.id.edt_Email_login);
        edt_password_login = findViewById(R.id.edt_password_login);
        btn_LOGIN = findViewById(R.id.btn_LOGIN);
        txt_register_now = findViewById(R.id.txt_register_now);
        txt_forgot_Password = findViewById(R.id.txt_forgot_Password);
        httpRequest = new HttpRequest();


        txt_forgot_Password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Forgot_password.class);
                startActivity(intent);
            }
        });

        btn_LOGIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = edt_Username_login.getText().toString().trim();
                String password = edt_password_login.getText().toString().trim();

                if (username.isEmpty()) {
                    edt_Username_login.setError("Vui lòng nhập username");
                    edt_Username_login.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    edt_password_login.setError("Vui lòng nhập mật khẩu");
                    edt_password_login.requestFocus();
                    return;
                }
                handleLogin(username, password);
            }
        });
        txt_register_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
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
                    LoginResponse userResponse = response.body();
                    if (userResponse.getStatus() == 200) {

                        // Tiến hành lưu thông tin người dùng và điều hướng
                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedIn", true); // Lưu trạng thái đăng nhập
                        editor.putInt("userRole", userResponse.getRole()); // Lưu vai trò người dùng
                        editor.putString("userId", userResponse.getId()); // Lưu ID người dùng
                        editor.apply();

                        // Chuyển hướng dựa trên vai trò người dùng
                        if (userResponse.getRole() == 0) {
                            // Admin
                            Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this, TrangChuAdmin.class));
                        } else if (userResponse.getRole() == 1) {
                            // User
                            Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this, Home_User.class));
                        } else {
                            Log.d("Login", "Unrecognized role");
                        }

                        finish(); // Hoàn thành hoạt động login
                    } else {
                        // Thông báo lỗi từ server
                        Log.d("Login", "Login failed: " + userResponse.getMessenger());
                        edt_Username_login.setError("Tài khoản hoặc mật khẩu không đúng");
                        edt_Username_login.requestFocus();
                    }
                } else {
                    // Khi response không thành công hoặc không có dữ liệu
                    Log.d("Login", "Response unsuccessful or empty");
                    edt_password_login.setError("Tài khoản hoặc mật khẩu không chính xác");
                    edt_Username_login.setError("Tài khoản hoặc mật khẩu không chính xác");
                    edt_password_login.requestFocus();
                }
            }


            @Override
            public void onFailure(Call<LoginResponse> call, Throwable throwable) {
                Log.e("Login", "API call failed: " + throwable.getMessage());
            }
        });
    }
}