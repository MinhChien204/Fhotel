package learn.fpoly.fhotel.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import learn.fpoly.fhotel.Model.User;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.Retrofit.HttpRequest;
import learn.fpoly.fhotel.activity.Change_password;
import learn.fpoly.fhotel.activity.Login;
import learn.fpoly.fhotel.response.Response;
import retrofit2.Call;
import retrofit2.Callback;


public class TkhoanFragment extends Fragment {
    private HttpRequest httpRequest;
    LinearLayout editProfile, editPassword, payment, booking, privacy, terms, btnLogOut, voucher;
    TextView txtName, txtEmail;
    ImageView imgProfile;
    GoogleSignInClient mGoogleSignInClient;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tkhoan, container, false);


        btnLogOut = view.findViewById(R.id.btnLogOut);
        editProfile = view.findViewById(R.id.editProfile);
        voucher = view.findViewById(R.id.linearlayoutVoucher);
        editPassword = view.findViewById(R.id.editPassword);
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()  // Yêu cầu email khi đăng nhập
                .build());
        txtName = view.findViewById(R.id.profile_name);
        txtEmail = view.findViewById(R.id.profile_email);
        imgProfile = view.findViewById(R.id.profile_image);

        httpRequest = new HttpRequest();

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (userId != null && !userId.isEmpty()) {
            fetchUserById(userId);
        } else {
            Toast.makeText(getContext(), "Invalid User ID", Toast.LENGTH_SHORT).show();
        }
        editPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Change_password.class);
                startActivity(intent);

            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new EditProfileFragment());
            }
        });
        voucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new VoucherFragment());
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutDialog();
            }
        });

        return view;
    }

    private void fetchUserById(String userId) {
        Call<Response<User>> call = httpRequest.callAPI().getuserbyid(userId);
        call.enqueue(new Callback<Response<User>>() {
            @Override
            public void onResponse(Call<Response<User>> call, retrofit2.Response<Response<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body().getData();
                    if (user != null) {
                        txtEmail.setText(user.getEmail());
                        txtName.setText(user.getName());

                        String avatarUrl = user.getAvatar();
                        Log.d("ava", "onResponse: " + avatarUrl);
                        Glide.with(getContext())
                                .load(avatarUrl)  // Sử dụng URL đầy đủ
                                .circleCrop()
                                .into(imgProfile);
                    }
                }
            }

            @Override
            public void onFailure(Call<Response<User>> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to fetch user: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout_u, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Đăng xuất");
        builder.setMessage("Bạn có chắc chắn muốn đăng xuất không?");
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Đăng xuất Google
                GoogleSignIn.getClient(getContext(), new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .build()).signOut()
                        .addOnCompleteListener(getActivity(), task -> {
                            // Xóa thông tin người dùng trong SharedPreferences
                            SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear(); // Xóa toàn bộ dữ liệu đăng nhập
                            editor.apply();
                            // Chuyển hướng về trang đăng nhập
                            Intent intent = new Intent(getActivity(), Login.class);
                            startActivity(intent);
                            getActivity().finish(); // Close the current activity
                        });
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Close the dialog
            }
        });
        builder.show();
    }

}