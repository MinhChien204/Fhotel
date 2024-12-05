package learn.fpoly.fhotel.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import learn.fpoly.fhotel.Adapter.BookingAdapter;
import learn.fpoly.fhotel.Adapter.FavouriteAdapter;
import learn.fpoly.fhotel.Model.Booking;
import learn.fpoly.fhotel.Model.Favourite;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.Retrofit.HttpRequest;
import learn.fpoly.fhotel.response.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class FavouriteFragment extends Fragment {
    private RecyclerView rcvListFavourite;
    private String userId;
    private FavouriteAdapter adapter;
    private TextView tvNoFavourites;
    private ImageView ivLoadingGif;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);
        ivLoadingGif = view.findViewById(R.id.ivLoadingGif);
        tvNoFavourites = view.findViewById(R.id.tvNoFavourites);
        rcvListFavourite = view.findViewById(R.id.rcv_favourite);
        rcvListFavourite.setLayoutManager(new LinearLayoutManager(getContext()));

        // Lấy userId từ SharedPreferences (hoặc truyền vào fragment)
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        if (userId != null) {
            // Gọi API để lấy danh sách bookings
            getFavourites(userId);
        }
        return view;
    }

    private void getFavourites(String userId) {
        HttpRequest httpRequest = new HttpRequest();
        Call<Response<List<Favourite>>> call = httpRequest.callAPI().getUserFavourites(userId);

        call.enqueue(new Callback<Response<List<Favourite>>>() {
            @Override
            public void onResponse(Call<Response<List<Favourite>>> call, retrofit2.Response<Response<List<Favourite>>> response) {
                if (response.isSuccessful()) {
                    List<Favourite> favourites = response.body().getData();
                    if (favourites != null && !favourites.isEmpty()) {
                        ivLoadingGif.setVisibility(View.GONE);
                        tvNoFavourites.setVisibility(View.GONE); // Ẩn thông báo
                        adapter = new FavouriteAdapter(getContext(),favourites);
                        rcvListFavourite.setAdapter(adapter);
                    } else {
                        ivLoadingGif.setVisibility(View.VISIBLE);
                        tvNoFavourites.setVisibility(View.VISIBLE);
                        Glide.with(getContext())
                                .asGif()  // Đảm bảo tải ảnh động
                                .load(R.drawable.heart_animation)  // Tải ảnh động từ tài nguyên drawable
                                .into(ivLoadingGif);
                        adapter = new FavouriteAdapter(getContext(), new ArrayList<>());
                        rcvListFavourite.setAdapter(adapter);
                    }
                } else {
                    Toast.makeText(getContext(), "Error fetching favourites.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<List<Favourite>>> call, Throwable t) {
                Toast.makeText(getContext(), "Error."+t, Toast.LENGTH_LONG).show();
                Log.d("err", "onFailure: "+t);
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        if (userId != null) {
            getFavourites(userId);
        }
    }
}

