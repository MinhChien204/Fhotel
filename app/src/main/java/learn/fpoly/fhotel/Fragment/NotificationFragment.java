package learn.fpoly.fhotel.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import learn.fpoly.fhotel.Adapter.BookingAdapter;
import learn.fpoly.fhotel.Adapter.FavouriteAdapter;
import learn.fpoly.fhotel.Adapter.NotificationAdapter;
import learn.fpoly.fhotel.Model.Booking;
import learn.fpoly.fhotel.Model.Notification;
import learn.fpoly.fhotel.Model.UserVoucher;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.Retrofit.HttpRequest;
import learn.fpoly.fhotel.response.Response;
import retrofit2.Call;
import retrofit2.Callback;


public class NotificationFragment extends Fragment {
    private RecyclerView rcv_listNotification;
    private String userId;
    private NotificationAdapter adapter;
    private TextView tvnoNotifications;
    private ImageView ivLoadingnotificationGif;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        tvnoNotifications=view.findViewById(R.id.tvnoNotifications);
        ivLoadingnotificationGif=view.findViewById(R.id.ivLoadingnotificationGif);
        // Ánh xạ RecyclerView
        rcv_listNotification = view.findViewById(R.id.rcv_listNotification);
        rcv_listNotification.setLayoutManager(new LinearLayoutManager(getContext()));

        // Lấy userId từ SharedPreferences (hoặc truyền vào fragment)
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        if (userId != null) {
            // Gọi API để lấy danh sách bookings
            getNotifications(userId);
        }

        return view;
    }

    private void getNotifications(String userId) {
        HttpRequest httpRequest = new HttpRequest();
        Call<Response<List<Notification>>> call = httpRequest.callAPI().getNotifications(userId);

        call.enqueue(new Callback<Response<List<Notification>>>() {
            @Override
            public void onResponse(Call<Response<List<Notification>>> call, retrofit2.Response<Response<List<Notification>>> response) {
                if (response.isSuccessful()) {
                    List<Notification> notifications = response.body().getData();
                    if (notifications != null && !notifications.isEmpty()) {
                        ivLoadingnotificationGif.setVisibility(View.GONE);
                        tvnoNotifications.setVisibility(View.GONE);
                        adapter = new NotificationAdapter(getContext(),notifications);
                        rcv_listNotification.setAdapter(adapter);
                    } else {
                        ivLoadingnotificationGif.setVisibility(View.VISIBLE);
                        tvnoNotifications.setVisibility(View.VISIBLE);
                        Glide.with(getContext())
                                .asGif()  // Đảm bảo tải ảnh động
                                .load(R.drawable.hotel_animation)  // Tải ảnh động từ tài nguyên drawable
                                .into(ivLoadingnotificationGif);
                        adapter = new NotificationAdapter(getContext(), new ArrayList<>());
                        rcv_listNotification.setAdapter(adapter);
                    }
                } else {
                    Toast.makeText(getContext(), "Khong co thong bao.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<List<Notification>>> call, Throwable t) {
                Toast.makeText(getContext(), "Error."+t, Toast.LENGTH_LONG).show();
                Log.d("err", "onFailure: "+t);
            }
        });
    }
}