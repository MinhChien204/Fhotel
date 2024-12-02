package learn.fpoly.fhotel.activity;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import learn.fpoly.fhotel.Adapter.ServiceAdapter;
import learn.fpoly.fhotel.Fragment.PaymentFragment;
import learn.fpoly.fhotel.Model.Booking;
import learn.fpoly.fhotel.Model.Favourite;
import learn.fpoly.fhotel.Model.Room;
import learn.fpoly.fhotel.Model.RoomService;
import learn.fpoly.fhotel.Model.User;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.Retrofit.HttpRequest;
import retrofit2.Call;
import retrofit2.Callback;
import learn.fpoly.fhotel.response.Response;

public class DetailsActivity extends AppCompatActivity {
    private ImageView ivBack, ivFavorite, imgRom_details,imageView8;
    private Button btnBookingHotel;
    private TextView txtdescription_details, txtprice_details, txtNamerom_details,txt_capacity,txtstatusRoom;
    private RatingBar txtRating_details;
    private HttpRequest httpRequest;
    private  int favouritestatus;
    private String roomId ;
private RecyclerView rvServices;
private ServiceAdapter serviceAdapter;
private Favourite favourite;
    private Room room;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        // Khởi tạo HttpRequest
        httpRequest = new HttpRequest();
        rvServices = findViewById(R.id.rvServices);
        rvServices.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        // Ánh xạ các view từ layout
        txtNamerom_details = findViewById(R.id.txtNamerom_details);
        txtRating_details = findViewById(R.id.ratingBarDetail);
        txtdescription_details = findViewById(R.id.txtdescription_details);
        txtprice_details = findViewById(R.id.txtprice_details);
        txt_capacity =findViewById(R.id.tvcapacity);
        txtstatusRoom =findViewById(R.id.textView10);
        ivBack = findViewById(R.id.ivBack);
        ivFavorite = findViewById(R.id.ivFavorite);
        btnBookingHotel = findViewById(R.id.buttonBooking);
        imgRom_details = findViewById(R.id.imgRom_details);
        serviceAdapter = new ServiceAdapter(this, new ArrayList<>());
        rvServices.setAdapter(serviceAdapter);

        // Nhận dữ liệu room_id từ Intent
        SharedPreferences sharedPreferences = DetailsActivity.this.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);
        roomId = getIntent().getStringExtra("room_id");
        // Xử lý sự kiện quay lại
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();  // Quay lại màn hình trước
            }
        });
        ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    favourite = new Favourite(userId,roomId);
                    createfavourite(favourite);
                    ivFavorite.setBackgroundResource(R.drawable.baseline_favorite_24);



            }
        });
        // Xử lý sự kiện khi nhấn nút đặt phòng
        btnBookingHotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PaymentFragment paymentFragment = new PaymentFragment();

                Bundle bundle = new Bundle();
                bundle.putString("room_id", roomId);
                bundle.putString("room_name", txtNamerom_details.getText().toString());
                bundle.putFloat("room_rating", txtRating_details.getRating());
                bundle.putString("room_description", txtdescription_details.getText().toString());
                bundle.putString("room_price", txtprice_details.getText().toString());
                bundle.putString("room_image", room.getImage()); // Lấy URL ảnh từ đối tượng room
                bundle.putString("room_capacity", txt_capacity.getText().toString());
                // Gán Bundle cho Fragment
                paymentFragment.setArguments(bundle);

                // Use FragmentTransaction to replace the fragment container with PaymentFragment
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                // Thay thế Fragment hiện tại bằng PaymentFragment
                transaction.replace(R.id.fragment_container, paymentFragment);
                transaction.addToBackStack(null);  // Add to backstack to allow back navigation
                transaction.commit();
            }
        });
    }
//createfavourite
    public  void createfavourite (Favourite favourite) {
        // Khởi tạo Retrofit
        HttpRequest httpRequest = new HttpRequest();
        Call<Response<Favourite>> call = httpRequest.callAPI().createfavourite(favourite);

        // Gửi yêu cầu đến server
        call.enqueue(new Callback<Response<Favourite>>() {
            @Override
            public void onResponse(Call<Response<Favourite>> call, retrofit2.Response<Response<Favourite>> response) {
                if (response.isSuccessful()) {
                    updatefavouritestatus(roomId,1);
                    Toast.makeText(DetailsActivity.this, "create successful!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(DetailsActivity.this, "false", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<Favourite>> call, Throwable t) {
                Toast.makeText(DetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
///updatefavouritestatus
private void updatefavouritestatus(String roomId, int newfavouritestatus) {
    HttpRequest httpRequest = new HttpRequest();
    Call<Response<Room>> call = httpRequest.callAPI().updatefavouritestatus(roomId, new Room(newfavouritestatus));

    call.enqueue(new Callback<Response<Room>>() {
        @Override
        public void onResponse(Call<Response<Room>> call, retrofit2.Response<Response<Room>> response) {
            if (response.isSuccessful()) {
                // Kiểm tra và cập nhật thông tin của booking chỉ cần thiết

                Toast.makeText(DetailsActivity.this, "Trạng thái đã được cập nhật!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(DetailsActivity.this, "Không thể cập nhật trạng thái", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<Response<Room>> call, Throwable t) {
            Log.d("stbk", "onFailure: "+t);
            Toast.makeText(DetailsActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });
}
    public void fetchRoomById(String roomId) {
        Call<Response<Room>> call = httpRequest.callAPI().getRoomById(roomId);
        call.enqueue(new Callback<Response<Room>>() {
            @Override
            public void onResponse(Call<Response<Room>> call, retrofit2.Response<Response<Room>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Response<Room> roomResponse = response.body(); // Lấy đối tượng Response
                    if (roomResponse.getStatus() == 200) { // Kiểm tra status
                        room = roomResponse.getData(); // Lấy dữ liệu Room
                        Log.d("room", "onResponse: " + room);
                        // Gán dữ liệu phòng vào các View
                        txtNamerom_details.setText(room.getName());
                        txtRating_details.setRating(Float.parseFloat(String.valueOf(room.getRating())));
                        txtdescription_details.setText(room.getDescription());
                        txtstatusRoom.setText(room.getStatus());
                        txtprice_details.setText(String.valueOf(room.getPrice()));
                        txt_capacity.setText(String.valueOf(room.getCapacity()) + " person");
                        favouritestatus =  room.getFavouritestatus();

                        Glide.with(DetailsActivity.this)
                                .load(room.getImage())
                                .into(imgRom_details);

                        // Kiểm tra trạng thái phòng
                        if (room.getStatus().equals("unavailable")) {
                            btnBookingHotel.setText("Room Unavailable");
                            btnBookingHotel.setBackgroundColor(getResources().getColor(R.color.black));
                            btnBookingHotel.setBackgroundResource(R.drawable.book_button_bg);
                            btnBookingHotel.setEnabled(false);
                        } else {
                            // Trạng thái phòng có sẵn, giữ nút Booking
                            btnBookingHotel.setText("Start Booking Your Trip");
                            btnBookingHotel.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                            btnBookingHotel.setBackgroundResource(R.drawable.book_button_bg);
                            btnBookingHotel.setEnabled(true);
                        }
                    } else {
                        Toast.makeText(DetailsActivity.this, "Room not found: " + roomResponse.getMessenger(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DetailsActivity.this, "Room not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<Room>> call, Throwable t) {
                Toast.makeText(DetailsActivity.this, "Failed to load room details: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void fetchServiceByRoomId(String roomId) {
        Call<Response<ArrayList<RoomService>>> call = httpRequest.callAPI().getServiceByIdRoom(roomId);
        call.enqueue(new Callback<Response<ArrayList<RoomService>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<RoomService>>> call, retrofit2.Response<Response<ArrayList<RoomService>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Response<ArrayList<RoomService>> roomResponse = response.body();
                    if (roomResponse.getStatus() == 200) {
                        ArrayList<RoomService> services = roomResponse.getData();

                        // Update RecyclerView with fetched services
                        serviceAdapter.updateData(services); // Assuming you have an updateData method in ServiceAdapter

                        Toast.makeText(DetailsActivity.this, "Services loaded successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DetailsActivity.this, "Services not found: " + roomResponse.getMessenger(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DetailsActivity.this, "Failed to load services", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<ArrayList<RoomService>>> call, Throwable t) {
                Toast.makeText(DetailsActivity.this, "Failed to load services: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //de lam moi du lieu moi khi focus vao man nay
    @Override
    protected void onResume() {
        super.onResume();
        String roomId = getIntent().getStringExtra("room_id");
        if (roomId != null && !roomId.isEmpty()) {
            fetchRoomById(roomId);
            fetchServiceByRoomId(roomId);
        } else {
            Toast.makeText(this, "Invalid Room ID", Toast.LENGTH_SHORT).show();
        }
    }
}
