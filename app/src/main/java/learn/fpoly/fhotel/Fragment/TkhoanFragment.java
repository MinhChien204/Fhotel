package learn.fpoly.fhotel.Fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;

import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.activity.Change_password;
import learn.fpoly.fhotel.activity.Home_User;
import learn.fpoly.fhotel.activity.Login;
import learn.fpoly.fhotel.activity.MainActivity;
import learn.fpoly.fhotel.chatbot.ChatBotActivity;


public class TkhoanFragment extends Fragment {

    LinearLayout editProfile, editPassword, payment, booking, privacy, terms,btnLogOut,voucher;
    TextView txtUserName, txtUserEmail;
    ImageView imgUserProfile;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tkhoan, container, false);


        btnLogOut = view.findViewById(R.id.btnLogOut);
        editProfile = view.findViewById(R.id.editProfile);
        voucher = view.findViewById(R.id.linearlayoutVoucher);
        editPassword = view.findViewById(R.id.editPassword);

        txtUserName = view.findViewById(R.id.profile_name);
        txtUserEmail = view.findViewById(R.id.profile_email);
        imgUserProfile = view.findViewById(R.id.profile_image);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", getContext().MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "Người dùng");
        String userEmail = sharedPreferences.getString("userEmail", "Email không xác định");
        String userImage = sharedPreferences.getString("userImage", null);

        txtUserName.setText(userName);
        txtUserEmail.setText(userEmail);
        if (userImage != null) {
            Glide.with(this).load(userImage).into(imgUserProfile); // Tải ảnh từ URL
        } else {
            imgUserProfile.setImageResource(R.drawable.ic_launcher_foreground); // Hiển thị ảnh mặc định nếu không có URL
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
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
                getActivity().finish(); // Close the current activity
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