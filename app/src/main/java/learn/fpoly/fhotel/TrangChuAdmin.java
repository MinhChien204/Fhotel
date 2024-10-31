package learn.fpoly.fhotel;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import learn.fpoly.fhotel.Fragment.HoaDonAdminFragment;
import learn.fpoly.fhotel.Fragment.HomeAdminFragment;
import learn.fpoly.fhotel.Fragment.NotificationAdminFragment;
import learn.fpoly.fhotel.Fragment.ThongKeAdminFragment;

public class TrangChuAdmin extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trang_chu_admin);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        frameLayout = findViewById(R.id.framelayout);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemID = item.getItemId();
                if (itemID == R.id.navHome) {
                    loadFragment(new HomeAdminFragment(), false);
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
        loadFragment(new HomeAdminFragment(), true);

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