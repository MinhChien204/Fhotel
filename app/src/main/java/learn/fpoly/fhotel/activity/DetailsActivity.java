package learn.fpoly.fhotel.activity;

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
import java.util.List;

import learn.fpoly.fhotel.Adapter.ServiceAdapter;
import learn.fpoly.fhotel.Fragment.PaymentFragment;
import learn.fpoly.fhotel.Model.Room;
import learn.fpoly.fhotel.Model.Service;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.Retrofit.HttpRequest;
import learn.fpoly.fhotel.response.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class DetailsActivity extends AppCompatActivity implements ServiceAdapter.OnServiceSelectedListener {
    private ImageView ivBack, imgRoomDetails;
    private Button btnBookingHotel;
    private TextView txtDescription, txtPrice, txtNameRoom, txtCapacity;
    private RatingBar ratingBarDetails;
    private RecyclerView rvServices;

    private ServiceAdapter serviceAdapter;
    private HttpRequest httpRequest;
    private Room room;
    private float totalServicePrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        initViews();
        setupRecyclerView();

        String roomId = getIntent().getStringExtra("room_id");
        if (roomId == null || roomId.isEmpty()) {
            showToast("Invalid Room ID");
            return;
        }

        fetchRoomById(roomId);
        fetchServices();

        ivBack.setOnClickListener(v -> onBackPressed());
        btnBookingHotel.setOnClickListener(v -> openPaymentFragment());
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        imgRoomDetails = findViewById(R.id.imgRom_details);
        btnBookingHotel = findViewById(R.id.buttonBooking);
        txtNameRoom = findViewById(R.id.txtNamerom_details);
        ratingBarDetails = findViewById(R.id.ratingBarDetail);
        txtDescription = findViewById(R.id.txtdescription_details);
        txtPrice = findViewById(R.id.txtprice_details);
        txtCapacity = findViewById(R.id.tvcapacity);
        rvServices = findViewById(R.id.rvServices);
        httpRequest = new HttpRequest();
    }

    private void setupRecyclerView() {
        rvServices.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        serviceAdapter = new ServiceAdapter(this, new ArrayList<>(), this);
        rvServices.setAdapter(serviceAdapter);
    }

    private void fetchRoomById(String roomId) {
        httpRequest.callAPI().getRoomById(roomId).enqueue(new Callback<Response<Room>>() {
            @Override
            public void onResponse(Call<Response<Room>> call, retrofit2.Response<Response<Room>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                    room = response.body().getData();
                    updateRoomDetails();
                } else {
                    logAndToastError("Failed to load room details", response.message());
                }
            }

            @Override
            public void onFailure(Call<Response<Room>> call, Throwable t) {
                logAndToastError("Failed to load room details", t.getMessage());
            }
        });
    }

    private void fetchServices() {
        httpRequest.callAPI().getServices().enqueue(new Callback<Response<ArrayList<Service>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<Service>>> call, retrofit2.Response<Response<ArrayList<Service>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Service> services = response.body().getData();
                    if (services != null && !services.isEmpty()) {
                        serviceAdapter.setServices(services);
                        serviceAdapter.notifyDataSetChanged();
                    } else {
                        showToast("No services available");
                    }
                } else {
                    logAndToastError("Failed to load services", response.message());
                }
            }

            @Override
            public void onFailure(Call<Response<ArrayList<Service>>> call, Throwable t) {
                logAndToastError("Failed to load services", t.getMessage());
            }
        });
    }

    private void updateRoomDetails() {
        if (room == null) return;

        txtNameRoom.setText(room.getName());
        ratingBarDetails.setRating(Float.parseFloat(String.valueOf(room.getRating())));
        txtDescription.setText(room.getDescription());
        txtPrice.setText(String.format("$%.2f", room.getPrice()));
        txtCapacity.setText(String.format("%d persons", room.getCapacity()));

        Glide.with(this)
                .load(room.getImage().get(0))
                .into(imgRoomDetails);
    }

    @Override
    public void onServiceSelected(List<Service> selectedServices) {
        totalServicePrice = 0;
        for (Service service : selectedServices) {
            totalServicePrice += service.getPrice();
        }
        // Cập nhật giá phòng + dịch vụ
        float totalPrice = (float) (room.getPrice() + totalServicePrice);
        txtPrice.setText(String.format("$%.2f", totalPrice));
    }

    private void openPaymentFragment() {
        if (room == null) {
            showToast("Room details not loaded yet.");
            return;
        }

        PaymentFragment paymentFragment = new PaymentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("room_name", room.getName());
        bundle.putFloat("room_rating", Float.parseFloat(String.valueOf(room.getRating())));
        bundle.putString("room_description", room.getDescription());
        bundle.putString("room_price", String.format("$%.2f", room.getPrice() + totalServicePrice));
        bundle.putString("room_image", room.getImage().get(0));
        bundle.putString("room_capacity", String.format("%d persons", room.getCapacity()));

        paymentFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, paymentFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void logAndToastError(String message, String errorDetail) {
        Log.e("DetailsActivity", message + " " + errorDetail);
        showToast(message);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

