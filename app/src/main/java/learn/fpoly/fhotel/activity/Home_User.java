package learn.fpoly.fhotel.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import learn.fpoly.fhotel.Fragment.Fragment_TrangChu;
import learn.fpoly.fhotel.Fragment.NotificationAdminFragment;
import learn.fpoly.fhotel.Fragment.TkhoanFragment;
import learn.fpoly.fhotel.Fragment.UudaiFragment;
import learn.fpoly.fhotel.R;

public class Home_User extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_user);

        bottomNavigationView = findViewById(R.id.bottomNavigationView_u);
        frameLayout = findViewById(R.id.framelayout_u);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment;
                int itemID = item.getItemId();

                // Xác định fragment được chọn
                if (itemID == R.id.navHome_u) {
                    selectedFragment = new Fragment_TrangChu();
                } else if (itemID == R.id.navNotification_u) {
                    selectedFragment = new NotificationAdminFragment();
                } else if (itemID == R.id.navUuDai_u) {
                    selectedFragment = new UudaiFragment();
                } else {
                    selectedFragment = new TkhoanFragment();
                }

                // Load fragment được chọn
                loadFragment(selectedFragment);
                return true;
            }
        });

        // Set fragment mặc định khi mở Activity lần đầu
        if (savedInstanceState == null) {
            loadFragment(new Fragment_TrangChu());
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Luôn thay thế fragment để tránh chồng lấn
        fragmentTransaction.replace(R.id.framelayout_u, fragment);
        fragmentTransaction.commit();
    }
}
