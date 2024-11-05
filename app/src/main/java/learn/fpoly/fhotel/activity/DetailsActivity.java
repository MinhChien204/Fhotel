package learn.fpoly.fhotel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import learn.fpoly.fhotel.Fragment.PaymentFragment;
import learn.fpoly.fhotel.Model.Room;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.Retrofit.HttpRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity {
    private ImageView ivBack, ivFavorite, imgRom_details;
    private Button btnBookingHotel;
    private TextView txtdescription_details, txtprice_details, txtRating_details, txtNamerom_details;
    private HttpRequest httpRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Khởi tạo HttpRequest
        httpRequest = new HttpRequest();

        // Ánh xạ các view từ layout
        txtNamerom_details = findViewById(R.id.txtNamerom_details);
        txtRating_details = findViewById(R.id.txtRating_details);
        txtdescription_details = findViewById(R.id.txtdescription_details);
        txtprice_details = findViewById(R.id.txtprice_details);
        ivBack = findViewById(R.id.ivBack);
        ivFavorite = findViewById(R.id.ivFavorite);
        btnBookingHotel = findViewById(R.id.buttonBooking);
        imgRom_details = findViewById(R.id.imgRom_details);

        // Nhận dữ liệu room_id từ Intent
        String roomId = getIntent().getStringExtra("room_id");
        Log.d("dcm", "onCreate: " + roomId);


         // Kiểm tra nếu ID hợp lệ và gọi API
        if (roomId != null && !roomId.isEmpty()) {
            fetchRoomById(roomId);
        } else {
            Toast.makeText(this, "Invalid Room ID", Toast.LENGTH_SHORT).show();
        }

        // Xử lý sự kiện quay lại
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();  // Quay lại màn hình trước
            }
        });

        // Xử lý sự kiện khi nhấn nút đặt phòng
        btnBookingHotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Khởi tạo PaymentFragment và chuyển đến Fragment này
                PaymentFragment paymentFragment = new PaymentFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, paymentFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    // Hàm lấy chi tiết phòng theo ID và gán dữ liệu vào các View
    public void fetchRoomById(String roomId) {
        Call<Room> call = httpRequest.callAPI().getRoomById(roomId);
        call.enqueue(new Callback<Room>() {
            @Override
            public void onResponse(Call<Room> call, Response<Room> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Room room = response.body();
                    Log.d("room", "onResponse: " + room);
                    // Gán dữ liệu phòng vào các View
                    txtNamerom_details.setText(room.getName());
                    txtRating_details.setText(String.valueOf(room.getRating()));
                    txtdescription_details.setText(room.getDescription());
                    txtprice_details.setText(String.valueOf(room.getPrice()));
                    // Nếu có hình ảnh, hãy gán nó vào imgRom_details
                    Glide.with(DetailsActivity.this)
                            .load(room.getImage()) // Thay `getImageUrl()` bằng phương thức lấy URL ảnh
                            .into(imgRom_details);// nếu bạn có phương thức này
                } else {
                    Toast.makeText(DetailsActivity.this, "Room not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Room> call, Throwable t) {
                Toast.makeText(DetailsActivity.this, "Failed to load room details: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
