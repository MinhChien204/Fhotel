package learn.fpoly.fhotel.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import learn.fpoly.fhotel.Model.LoginRequest;
import learn.fpoly.fhotel.Model.TokenFacebookRequest;
import learn.fpoly.fhotel.Model.TokenRequest;
import learn.fpoly.fhotel.response.LoginResponse;
import learn.fpoly.fhotel.response.Response;
import learn.fpoly.fhotel.Model.User;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.Retrofit.HttpRequest;
import retrofit2.Call;
import retrofit2.Callback;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
public class Login extends AppCompatActivity {
    AppCompatButton btn_LOGIN;
    TextView txt_register_now, txt_forgot_Password;
    HttpRequest httpRequest;
    EditText edt_Username_login, edt_password_login;
    // Thêm biến global
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 100;
    Button btngoogle;
    private CallbackManager callbackManager;
    LoginButton btnfacebook;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edt_Username_login = findViewById(R.id.edt_Email_login);
        edt_password_login = findViewById(R.id.edt_password_login);
        btn_LOGIN = findViewById(R.id.btn_LOGIN);
        txt_register_now = findViewById(R.id.txt_register_now);
        txt_forgot_Password = findViewById(R.id.txt_forgot_Password);
        btngoogle = findViewById(R.id.btngoogle);
        btnfacebook = findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();
        httpRequest = new HttpRequest();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Thêm Client ID từ Google Console
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btngoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        btnfacebook.setPermissions("email"); // request permission to access email
        btnfacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Login success
                String accessToken = loginResult.getAccessToken().getToken();
                if (accessToken != null) {
                    Log.d("FacebookLogin", "Login successful with token: " + accessToken);
                    handleFacebookSignIn(accessToken);
                    LoginManager.getInstance().logOut();
                }else{
                    Toast.makeText(Login.this, "AccessToken null", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancel() {
                Log.d("FacebookLogin", "User cancelled login.");
                Toast.makeText(Login.this, "Đăng nhập bị hủy", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("FacebookLogin", "Login error: " + error.getMessage());
                Toast.makeText(Login.this, "Có lỗi xảy ra khi đăng nhập", Toast.LENGTH_SHORT).show();
            }
        });

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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                handleGoogleSignIn(account); // Xử lý đăng nhập thành công
            } catch (ApiException e) {
                Log.e("GoogleSignIn", "Sign-in failed", e);
            }
        }
    }


    private void handleGoogleSignIn(GoogleSignInAccount account) {
        if (account != null) {

            String idToken = account.getIdToken();
            String email = account.getEmail();
            String name = account.getDisplayName();
            Log.d("GoogleSignIn", "Name: " + name + ", Email: " + email + ", Token: " + idToken);
            Log.d("GoogleSignIn", "ID Token: " + idToken);

            Call<Response<User>> call = httpRequest.callAPI().loginWithGoogle(new TokenRequest(idToken));
            call.enqueue(new Callback<Response<User>>() {
                @Override
                public void onResponse(Call<Response<User>> call, retrofit2.Response<Response<User>> response) {
                    if (response.isSuccessful()) {
                        User user = response.body().getData();
                        Log.d("GoogleSignIn", "User saved: " + user.getEmail());
                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedIn", true); // Lưu trạng thái đăng nhập
                        editor.putInt("userRole", user.getRole()); // Lưu vai trò người dùng
                        editor.putString("userId", user.get_id()); // Lưu ID người dùng
                        editor.apply();
                        Intent intent = new Intent(Login.this, Home_User.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e("GoogleSignIn", "Failed to save user: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<Response<User>> call, Throwable t) {
                    Log.e("GoogleSignIn", "API call failed", t);


                }
            });
        }
    }

    //facebook
    private void handleFacebookSignIn(String accessToken) {
        // Call the API to authenticate the user using the Facebook token
        Call<Response<User>> call = httpRequest.callAPI().loginWithFacebook(new TokenFacebookRequest(accessToken));
        call.enqueue(new Callback<Response<User>>() {
            @Override
            public void onResponse(Call<Response<User>> call, retrofit2.Response<Response<User>> response) {
                if (response.isSuccessful()) {
                    User user = response.body().getData();
                    Log.d("FacebookSignIn", "User saved: " + user.getEmail());

                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putInt("userRole", user.getRole());
                    editor.putString("userId", user.get_id());
                    editor.apply();

                    Intent intent = new Intent(Login.this, Home_User.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("FacebookSignIn", "Failed to save user: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Response<User>> call, Throwable t) {
                Log.e("FacebookSignIn", "API call failed", t);
            }
        });
    }


}

