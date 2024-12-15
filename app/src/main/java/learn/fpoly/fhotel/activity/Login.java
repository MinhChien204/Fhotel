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
import learn.fpoly.fhotel.response.UpdateFcmTokenRequest;
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
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;

public class Login extends AppCompatActivity {
    AppCompatButton btn_LOGIN;
    TextView txt_register_now, txt_forgot_Password;
    HttpRequest httpRequest;
    EditText edt_Username_login, edt_password_login;
    // Thêm biến global
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 100;
    Button btngoogle,btnfacebook;
    private CallbackManager callbackManager;

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

        btnfacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Bắt đầu quá trình đăng nhập với Facebook
                LoginManager.getInstance().logInWithReadPermissions(Login.this, Arrays.asList("email"));
            }
        });
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Khi đăng nhập thành công, lấy token
                String accessToken = loginResult.getAccessToken().getToken();
                Log.d("FacebookLogin", "Login successful with token: " + accessToken);

                // Xử lý đăng nhập với token
                handleFacebookSignIn(accessToken);

                // Đăng xuất sau khi xử lý
                LoginManager.getInstance().logOut();
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
                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.putInt("userRole", userResponse.getRole());
                        editor.putString("userId", userResponse.getId());
                        editor.apply();

                        // Lấy FCM token
                        FirebaseMessaging.getInstance().getToken()
                                .addOnCompleteListener(task -> {
                                    if (!task.isSuccessful()) {
                                        Log.w("FCM", "Fetching FCM token failed", task.getException());
                                        return;
                                    }
                                    String fcmToken = task.getResult();
                                    Log.d("FCM", "FCM Token: " + fcmToken);

                                    // Gửi FCM token lên server
                                    sendFcmTokenToServer(userResponse.getId(), fcmToken);
                                });

                        // Điều hướng theo vai trò người dùng
                        if (userResponse.getRole() == 0) {
                            startActivity(new Intent(Login.this, TrangChuAdmin.class));
                        } else if (userResponse.getRole() == 1) {
                            startActivity(new Intent(Login.this, Home_User.class));
                        }
                        finish();
                    } else {
                        edt_Username_login.setError("Tài khoản hoặc mật khẩu không đúng");
                        edt_Username_login.requestFocus();
                    }
                } else {
                    edt_password_login.setError("Tài khoản hoặc mật khẩu không chính xác");
                    edt_Username_login.requestFocus();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable throwable) {
                Log.e("Login", "API call failed: " + throwable.getMessage());
            }
        });
    }

    public void sendFcmTokenToServer(String userId, String fcmToken) {
        UpdateFcmTokenRequest request = new UpdateFcmTokenRequest(userId, fcmToken);
        Call<Response<Void>> call = httpRequest.callAPI().updateFcmToken(request);
        call.enqueue(new Callback<Response<Void>>() {
            @Override
            public void onResponse(Call<Response<Void>> call, retrofit2.Response<Response<Void>> response) {
                if (response.isSuccessful()) {
                    Log.d("FCM", "FCM Token updated successfully on server");
                } else {
                    Log.e("FCM", "Failed to update FCM token: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Response<Void>> call, Throwable t) {
                Log.e("FCM", "Error updating FCM token: " + t.getMessage());
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
            Call<Response<User>> call = httpRequest.callAPI().loginWithGoogle(new TokenRequest(idToken));
            call.enqueue(new Callback<Response<User>>() {
                @Override
                public void onResponse(Call<Response<User>> call, retrofit2.Response<Response<User>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        User user = response.body().getData();
                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.putInt("userRole", user.getRole());
                        editor.putString("userId", user.get_id());
                        editor.apply();

                        // Lấy FCM token
                        FirebaseMessaging.getInstance().getToken()
                                .addOnCompleteListener(task -> {
                                    if (!task.isSuccessful()) {
                                        Log.w("FCM", "Fetching FCM token failed", task.getException());
                                        return;
                                    }
                                    String fcmToken = task.getResult();
                                    Log.d("FCM", "FCM Token: " + fcmToken);

                                    // Gửi FCM token lên server
                                    sendFcmTokenToServer(user.get_id(), fcmToken);
                                });

                        startActivity(new Intent(Login.this, Home_User.class));
                        finish();
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
        Call<Response<User>> call = httpRequest.callAPI().loginWithFacebook(new TokenFacebookRequest(accessToken));
        call.enqueue(new Callback<Response<User>>() {
            @Override
            public void onResponse(Call<Response<User>> call, retrofit2.Response<Response<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body().getData();
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putInt("userRole", user.getRole());
                    editor.putString("userId", user.get_id());
                    editor.apply();

                    // Lấy FCM token
                    FirebaseMessaging.getInstance().getToken()
                            .addOnCompleteListener(task -> {
                                if (!task.isSuccessful()) {
                                    Log.w("FCM", "Fetching FCM token failed", task.getException());
                                    return;
                                }
                                String fcmToken = task.getResult();
                                Log.d("FCM", "FCM Token: " + fcmToken);

                                // Gửi FCM token lên server
                                sendFcmTokenToServer(user.get_id(), fcmToken);
                            });

                    startActivity(new Intent(Login.this, Home_User.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Response<User>> call, Throwable t) {
                Log.e("FacebookSignIn", "API call failed", t);
            }
        });
    }



}

