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
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import learn.fpoly.fhotel.Adapter.BookingAdapter;
import learn.fpoly.fhotel.Model.Booking;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.Retrofit.HttpRequest;
import learn.fpoly.fhotel.response.Response;
import retrofit2.Call;
import retrofit2.Callback;


public class BookingFragment extends Fragment {
    private RecyclerView rcvListUpcomingBooking;
    private String userId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        // Ánh xạ RecyclerView
        rcvListUpcomingBooking = view.findViewById(R.id.rcv_listUpcomingBooking);
        rcvListUpcomingBooking.setLayoutManager(new LinearLayoutManager(getContext()));

        // Lấy userId từ SharedPreferences (hoặc truyền vào fragment)
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        if (userId != null) {
            // Gọi API để lấy danh sách bookings
            getBookings(userId);
        }

        return view;
    }

    private void getBookings(String userId) {
        HttpRequest httpRequest = new HttpRequest();
        Call<Response<List<Booking>>> call = httpRequest.callAPI().getUserBookings(userId);

        call.enqueue(new Callback<Response<List<Booking>>>() {
            @Override
            public void onResponse(Call<Response<List<Booking>>> call, retrofit2.Response<Response<List<Booking>>> response) {
                if (response.isSuccessful() && response.body().getStatus() != 404) {
                    List<Booking> bookings = response.body().getData();
                    if (bookings != null && !bookings.isEmpty()) {
                        BookingAdapter adapter = new BookingAdapter(getContext(),bookings);
                        adapter.sortByCreatedAtNewestFirst(); // Sắp xếp trước khi hiển thị
                        rcvListUpcomingBooking.setAdapter(adapter);
                    } else {
                        Toast.makeText(getContext(), "No upcoming bookings.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "No room was found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<List<Booking>>> call, Throwable t) {
                Toast.makeText(getContext(), "Error."+t, Toast.LENGTH_LONG).show();
                Log.d("err", "onFailure: "+t);
            }
        });
    }
}