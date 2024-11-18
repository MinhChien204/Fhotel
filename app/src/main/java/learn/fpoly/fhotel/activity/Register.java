package learn.fpoly.fhotel.activity;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import learn.fpoly.fhotel.Model.User;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.Retrofit.ApiService;
import learn.fpoly.fhotel.Retrofit.HttpRequest;
import learn.fpoly.fhotel.response.Response;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Register extends AppCompatActivity {
    EditText edt_username_register, edt_Email_register, edt_password_register, edt_phonenumber_register, edt_name_register;
    AppCompatButton btn_REGISTER;
    ApiService apiService;
    TextView txt_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edt_Email_register = findViewById(R.id.edt_Email_register);
        edt_username_register = findViewById(R.id.edt_username_register);
        edt_password_register = findViewById(R.id.edt_password_register);
        edt_phonenumber_register = findViewById(R.id.edt_phonenumber_register);
        btn_REGISTER = findViewById(R.id.btn_REGISTER);
        txt_login = findViewById(R.id.txt_login);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        btn_REGISTER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edt_username_register.getText().toString().trim();
                String password = edt_password_register.getText().toString().trim();
                String email = edt_Email_register.getText().toString().trim();
                String phone = edt_phonenumber_register.getText().toString().trim();

                // Validate inputs
                if (username.isEmpty()) {
                    edt_username_register.setError("Vui lòng nhập username");
                    edt_username_register.requestFocus();
                    return;
                }
                if (email.isEmpty()) {
                    edt_Email_register.setError("Vui lòng nhập Email");
                    edt_Email_register.requestFocus();
                    return;
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    edt_Email_register.setError("Vui lòng nhập Email đúng dịnh dạng");
                    edt_Email_register.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    edt_password_register.setError("Vui lòng nhập password");
                    edt_password_register.requestFocus();
                    return;
                }

                if (password.length() < 6) {
                    edt_password_register.setError("Mật khẩu phải đủ 6 kí tự");
                    edt_password_register.requestFocus();
                    return;
                }

                // Phone number validation using regex
                String phoneRegex = "^[+]?[0-9]{10,13}$";  // Regex for valid phone numbers (supports + and 10-13 digits)
                if (phone.isEmpty()) {
                    edt_phonenumber_register.setError("Vui lòng nhập số điện thoại");
                    edt_phonenumber_register.requestFocus();
                    return;
                }
                if (!phone.matches(phoneRegex)) {
                    edt_phonenumber_register.setError("Số điện thoại không hợp lệ");
                    edt_phonenumber_register.requestFocus();
                    return;
                }
                    int role = 1;
                String gender ="female";
                String name="";
                String address = "";
                // Make API call to register
                Call<Response<User>> call = apiService.register(username, password, email, phone,name,gender,address);
                call.enqueue(new Callback<Response<User>>() {
                    @Override
                    public void onResponse(Call<Response<User>> call, retrofit2.Response<Response<User>> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "onResponse: Successful registration");
                            Toast.makeText(Register.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Register.this, Login.class));
                        } else {
                            Toast.makeText(Register.this, "Đăng ký không thành công", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Response<User>> call, Throwable throwable) {
                        Toast.makeText(Register.this, "Lỗi kết nối tới máy chủ", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onFailure: " + throwable.getMessage());
                    }
                });
            }
        });

        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });
    }
}