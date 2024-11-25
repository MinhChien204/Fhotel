package learn.fpoly.fhotel.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;


import learn.fpoly.fhotel.Model.Service;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.Retrofit.HttpRequest;
import learn.fpoly.fhotel.response.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class ServiceDetail extends AppCompatActivity {
    private ImageView ivBack, imgSerDetails;
    private TextView txtDescription, txtPrice, txtName;
    private Service service;
    private HttpRequest httpRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

        initViews();

        String serId = getIntent().getStringExtra("ser_id");
        if (serId == null || serId.isEmpty()) {
            showToast("Invalid Service ID");
            return;
        }

        fetchServiceById(serId);

        ivBack.setOnClickListener(v -> onBackPressed());
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        imgSerDetails = findViewById(R.id.imgSer_details);
        txtName = findViewById(R.id.txtNameSer_details);
        txtDescription = findViewById(R.id.tvDes);
        txtPrice = findViewById(R.id.tvPrice);
        httpRequest = new HttpRequest();
    }

    private void fetchServiceById(String serId) {
        httpRequest.callAPI().getServiceById(serId).enqueue(new Callback<Response<Service>>() {
            @Override
            public void onResponse(Call<Response<Service>> call, retrofit2.Response<Response<Service>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                    service = response.body().getData();
                    updateRoomDetails();
                } else {
                    logAndToastError("Failed to load service details", response.message());
                }
            }

            @Override
            public void onFailure(Call<Response<Service>> call, Throwable t) {
                logAndToastError("Failed to load service details", t.getMessage());
            }
        });
    }

    private void updateRoomDetails() {
        if (service == null) return;

        txtName.setText("Dịch vụ: " + service.getName());
        txtDescription.setText(service.getDescription());
        txtPrice.setText(String.format("$%.2f", service.getPrice()));

        Glide.with(this)
                .load(service.getImage().get(0))
                .into(imgSerDetails);
    }


    private void logAndToastError(String message, String errorDetail) {
        Log.e("DetailsActivity", message + " " + errorDetail);
        showToast(message);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

