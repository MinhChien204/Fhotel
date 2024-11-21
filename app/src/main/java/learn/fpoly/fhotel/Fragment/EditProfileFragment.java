package learn.fpoly.fhotel.Fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.Calendar;

import learn.fpoly.fhotel.Model.User;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.Retrofit.HttpRequest;
import learn.fpoly.fhotel.activity.Home_User;
import learn.fpoly.fhotel.response.Response;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class EditProfileFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;

    private HttpRequest httpRequest;
    private EditText mail_pro, txten_prf, date_pof, Address_prf, Phone_prf;
    private ImageView image_prf, edit_icon;
    private RadioButton Male_prf, Famal_prf;
    private Button btnUpdateUser;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // Khởi tạo các view
        mail_pro = view.findViewById(R.id.mail_pro);
        txten_prf = view.findViewById(R.id.txten_prf);
        date_pof = view.findViewById(R.id.date_pof);
        Address_prf = view.findViewById(R.id.Address_prf);
        Phone_prf = view.findViewById(R.id.Phone_prf);
        image_prf = view.findViewById(R.id.image_prf);
        edit_icon = view.findViewById(R.id.edit_icon);
        Male_prf = view.findViewById(R.id.Male_prf);
        Famal_prf = view.findViewById(R.id.Famal_prf);
        btnUpdateUser = view.findViewById(R.id.btnUpdateUser);

        httpRequest = new HttpRequest();

        // Lấy userId từ SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        if (userId != null && !userId.isEmpty()) {
            fetchUserById(userId);
        } else {
            Toast.makeText(getContext(), "Invalid User ID", Toast.LENGTH_SHORT).show();
        }

        // Khi nhấn vào biểu tượng chỉnh sửa ảnh
        edit_icon.setOnClickListener(v -> openImagePicker());

        // Khi nhấn vào trường ngày sinh
        date_pof.setOnClickListener(v -> showDatePickerDialog()); // Khi nhấn nút Update
        btnUpdateUser.setOnClickListener(v -> updateUser());

        return view;
    }

    // Hiển thị DatePickerDialog
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year1, month1, dayOfMonth) -> {
                    String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                    date_pof.setText(selectedDate);
                }, year, month, day);

        datePickerDialog.show();
    }

    // Mở trình chọn ảnh
    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    // Xử lý kết quả khi người dùng chọn ảnh
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                // Hiển thị ảnh trong ImageView
                Glide.with(getContext())
                        .load(imageUri)
                        .circleCrop()
                        .into(image_prf);

                // Tải ảnh lên server
                uploadImageToServer(imageUri);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Lấy thông tin người dùng
    private void fetchUserById(String userId) {
        Call<Response<User>> call = httpRequest.callAPI().getuserbyid(userId);
        call.enqueue(new Callback<Response<User>>() {
            @Override
            public void onResponse(Call<Response<User>> call, retrofit2.Response<Response<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body().getData();
                    if (user != null) {
                        mail_pro.setText(user.getEmail());
                        txten_prf.setText(user.getName());
                        Address_prf.setText(user.getAddress());
                        Phone_prf.setText(user.getPhonenumber());
                        date_pof.setText(user.getBirthday());
                        if (user.getGender().equalsIgnoreCase("female")) {
                            Famal_prf.setChecked(true);
                        } else {
                        Male_prf.setChecked(true);
                        }
                        String avatarUrl = "http://10.0.2.2:3000/" + user.getAvatar();  // Thêm URL gốc vào đường dẫn ảnh
                        Log.d("ava", "onResponse: "+avatarUrl);
                        Glide.with(getContext())
                                .load(avatarUrl)  // Sử dụng URL đầy đủ
                                .circleCrop()
                                .into(image_prf);
                    }
                }
            }

            @Override
            public void onFailure(Call<Response<User>> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to fetch user: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Cập nhật thông tin người dùng
    private void updateUser() {
        String email = mail_pro.getText().toString().trim();
        String name = txten_prf.getText().toString().trim();
        String birthday = date_pof.getText().toString().trim();
        String address = Address_prf.getText().toString().trim();
        String phonenumber = Phone_prf.getText().toString().trim();
        String gender = Male_prf.isChecked() ? "male" : "female";

        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setBirthday(birthday);
        user.setAddress(address);
        user.setPhonenumber(phonenumber);
        user.setGender(gender);

        Call<Response<User>> call = httpRequest.callAPI().updateUser(userId, user);
        call.enqueue(new Callback<Response<User>>() {
            @Override
            public void onResponse(Call<Response<User>> call, retrofit2.Response<Response<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "User updated successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), Home_User.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Failed to update user!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<User>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Tải ảnh lên server
    private void uploadImageToServer(Uri imageUri) {
        try {
            File file = new File(getPathFromUri(imageUri));
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);

            Call<Response<User>> call = httpRequest.callAPI().uploadUserImage(userId, body);
            call.enqueue(new Callback<Response<User>>() {
                @Override
                public void onResponse(Call<Response<User>> call, retrofit2.Response<Response<User>> response) {
                    if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                }
                }

                @Override
                public void onFailure(Call<Response<User>> call, Throwable t) {
                    Toast.makeText(getContext(), "Error uploading image: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e("dcmmm", "uploadImageToServer: ", e);
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
        }
    }

    private String getPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
        return uri.getPath();
    }
}