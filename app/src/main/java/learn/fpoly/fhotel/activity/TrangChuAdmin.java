package learn.fpoly.fhotel.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import learn.fpoly.fhotel.Fragment.HoaDonAdminFragment;
import learn.fpoly.fhotel.Fragment.NotificationAdminFragment;
import learn.fpoly.fhotel.Fragment.QuanLiUserFragment;
import learn.fpoly.fhotel.Fragment.ThongKeAdminFragment;
import learn.fpoly.fhotel.R;

public class TrangChuAdmin extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;
    Button btnPayDone;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trang_chu_admin);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        frameLayout = findViewById(R.id.framelayout);
        btnPayDone = findViewById(R.id.btnpaydone);
        btnPayDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemID = item.getItemId();
                if (itemID == R.id.navHome) {
                    loadFragment(new QuanLiUserFragment(), false);
                } else if (itemID == R.id.navHoaDon) {
                    loadFragment(new HoaDonAdminFragment(), false);
                } else if (itemID == R.id.navNotification) {
                    loadFragment(new NotificationAdminFragment(), false);
                } else {
                    loadFragment(new ThongKeAdminFragment(), false);
                }
                return true;
            }
        });

// Set fragment mặc định khi mở Activity
        loadFragment(new QuanLiUserFragment(), true);

    }

    private void showDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetpaymentdone);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    private void loadFragment(Fragment fragment, boolean isAppInitialized) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (isAppInitialized) {
            // Thêm fragment mặc định chỉ khi Activity khởi tạo lần đầu
            fragmentTransaction.add(R.id.framelayout, fragment);
        } else {
            // Thay thế fragment khi có mục khác được chọn
            fragmentTransaction.replace(R.id.framelayout, fragment);
        }

        fragmentTransaction.commit();
    }
}