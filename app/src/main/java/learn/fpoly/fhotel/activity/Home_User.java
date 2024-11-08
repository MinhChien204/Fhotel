package learn.fpoly.fhotel.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import learn.fpoly.fhotel.Fragment.BookingFragment;
import learn.fpoly.fhotel.Fragment.Fragment_TrangChu;
import learn.fpoly.fhotel.Fragment.NotificationAdminFragment;
import learn.fpoly.fhotel.Fragment.TkhoanFragment;
import learn.fpoly.fhotel.Fragment.UudaiFragment;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.chatbot.ChatBotActivity;

public class Home_User extends AppCompatActivity {
    public BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;
    private FloatingActionButton floatingActionButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_user);

        bottomNavigationView = findViewById(R.id.bottomNavigationView_u);
        frameLayout = findViewById(R.id.framelayout_u);
        floatingActionButton =findViewById(R.id.fab_chatbot);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home_User.this, ChatBotActivity.class);
                startActivity(intent);
            }
        });

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
                } else if (itemID == R.id.navBooking_u) {
                    selectedFragment = new BookingFragment();
                }
                else if (itemID == R.id.navUudai_u) {
                    selectedFragment = new UudaiFragment();
                }else {
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
