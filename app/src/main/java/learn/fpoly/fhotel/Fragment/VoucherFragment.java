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
import learn.fpoly.fhotel.Adapter.VoucherAdapter;
import learn.fpoly.fhotel.Model.Booking;
import learn.fpoly.fhotel.Model.Favourite;
import learn.fpoly.fhotel.Model.UserVoucher;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.Retrofit.HttpRequest;
import learn.fpoly.fhotel.response.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class VoucherFragment extends Fragment {
    private RecyclerView rcv_voucher;
    private String userId;
    private VoucherAdapter adapter;
    private TextView tvNoVoucher;
    private ImageView ivLoadingGifVoucher;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_voucher, container, false);
        ivLoadingGifVoucher = view.findViewById(R.id.ivLoadingGifVoucher);
        tvNoVoucher = view.findViewById(R.id.tvNoVoucher);
        rcv_voucher = view.findViewById(R.id.rcv_voucher);
        rcv_voucher.setLayoutManager(new LinearLayoutManager(getContext()));

        // Lấy userId từ SharedPreferences (hoặc truyền vào fragment)
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        if (userId != null) {
            // Gọi API để lấy danh sách bookings
            getUserVouchers(userId);
        }
        return view;
    }

    private void getUserVouchers(String userId) {
        HttpRequest httpRequest = new HttpRequest();
        Call<Response<List<UserVoucher>>> call = httpRequest.callAPI().getUserVouchers(userId);

        call.enqueue(new Callback<Response<List<UserVoucher>>>() {
            @Override
            public void onResponse(Call<Response<List<UserVoucher>>> call, retrofit2.Response<Response<List<UserVoucher>>> response) {
                if (response.isSuccessful()) {
                    List<UserVoucher> vouchers = response.body().getData();
                    if (vouchers != null && !vouchers.isEmpty()) {
                        ivLoadingGifVoucher.setVisibility(View.GONE);
                        tvNoVoucher.setVisibility(View.GONE); // Ẩn thông báo
                        adapter = new VoucherAdapter(getContext(),vouchers);
                        adapter.sortByCreatedAtNewestFirst();
                        rcv_voucher.setAdapter(adapter);
                    } else {
                        ivLoadingGifVoucher.setVisibility(View.VISIBLE);
                        tvNoVoucher.setVisibility(View.VISIBLE);
                        Glide.with(getContext())
                                .asGif()  // Đảm bảo tải ảnh động
                                .load(R.drawable.sale)  // Tải ảnh động từ tài nguyên drawable
                                .into(ivLoadingGifVoucher);
                        adapter = new VoucherAdapter(getContext(), new ArrayList<>());
                        rcv_voucher.setAdapter(adapter);
                    }
                } else {
                    Toast.makeText(getContext(), "Error fetching favourites.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<List<UserVoucher>>> call, Throwable t) {
                Toast.makeText(getContext(), "Error."+t, Toast.LENGTH_LONG).show();
                Log.d("err", "onFailure: "+t);
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        if (userId != null) {
            getUserVouchers(userId);
        }
    }
}

