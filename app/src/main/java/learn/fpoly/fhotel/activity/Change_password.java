package learn.fpoly.fhotel.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import learn.fpoly.fhotel.Model.Notification;
import learn.fpoly.fhotel.Model.PasswordUpdateRequest;
import learn.fpoly.fhotel.Model.User;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.Retrofit.HttpRequest;
import learn.fpoly.fhotel.response.Response;
import retrofit2.Call;
import retrofit2.Callback;


public class Change_password extends AppCompatActivity {
    EditText edt_new_password_change, edt_password_change;
    private HttpRequest httpRequest;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        edt_new_password_change = findViewById(R.id.edt_new_password_change);
        edt_password_change = findViewById(R.id.edt_password_change);
        Button btnChangePassword = findViewById(R.id.btn_Save);

        // Khởi tạo HttpRequest
        httpRequest = new HttpRequest();

        // Lấy userId từ SharedPreferences (giả sử bạn đã lưu userId khi đăng nhập)
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        // Kiểm tra nếu không có userId
        if (userId == null) {
            Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật mật khẩu khi nhấn nút
        btnChangePassword.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String oldPassword = edt_password_change.getText().toString().trim();
        String newPassword = edt_new_password_change.getText().toString().trim();

        if (oldPassword.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra mật khẩu cũ có đúng không
        validateCurrentPassword(oldPassword, newPassword);
    }

    private void validateCurrentPassword(String oldPassword, String newPassword) {
        // Gửi API để lấy thông tin người dùng và kiểm tra mật khẩu hiện tại
        Call<Response<User>> call = httpRequest.callAPI().getuserbyid(userId);
        call.enqueue(new Callback<Response<User>>() {
            @Override
            public void onResponse(Call<Response<User>> call, retrofit2.Response<Response<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body().getData();
                    if (user != null) {
                        // Kiểm tra mật khẩu cũ
                        if (user.getPassword().equals(oldPassword)) {
                            // Nếu đúng, gọi hàm để cập nhật mật khẩu mới
                            updatePassword(newPassword);
                        } else {
                            // Mật khẩu cũ sai
                            Toast.makeText(Change_password.this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(Change_password.this, "User not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<User>> call, Throwable t) {
                Toast.makeText(Change_password.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePassword(String newPassword) {
        String oldPassword = edt_password_change.getText().toString().trim();

        // Tạo đối tượng PasswordUpdateRequest
        PasswordUpdateRequest passwordUpdateRequest = new PasswordUpdateRequest(oldPassword, newPassword);

        // Gửi API để cập nhật mật khẩu mới cho người dùng
        Call<Response<User>> call = httpRequest.callAPI().updatePassword(userId, passwordUpdateRequest);
        call.enqueue(new Callback<Response<User>>() {
            @Override
            public void onResponse(Call<Response<User>> call, retrofit2.Response<Response<User>> response) {
                if (response.isSuccessful()) {
                    createPasswordChangeNotification();
                    Toast.makeText(Change_password.this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                    navigateToLogin();
                } else {
                    Toast.makeText(Change_password.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<User>> call, Throwable t) {
                Toast.makeText(Change_password.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void navigateToLogin() {
        // Khởi tạo Intent để chuyển hướng đến màn hình login
        Intent intent = new Intent(Change_password.this, Login.class);

        // Xoá tất cả các Activity trước đó trong back stack để khi nhấn nút back sẽ không quay lại màn hình change password
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Thực hiện chuyển hướng đến màn hình login
        startActivity(intent);

        // Kết thúc màn hình hiện tại
        finish();
    }

    private void createPasswordChangeNotification() {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage("Bạn đã thay đổi mật khẩu.");
        notification.setType("password_change");

        Call<Response<Notification>> notificationCall = httpRequest.callAPI().createNotification(notification);
        notificationCall.enqueue(new Callback<Response<Notification>>() {
            @Override
            public void onResponse(Call<Response<Notification>> call, retrofit2.Response<Response<Notification>> response) {
                if (response.isSuccessful()) {
                    Log.d("Notification", "Notification created successfully");
                } else {
                    Log.e("Notification", "Failed to create notification");
                }
            }

            @Override
            public void onFailure(Call<Response<Notification>> call, Throwable t) {
                Log.e("Notification", "Error: " + t.getMessage());
            }
        });
    }
}
