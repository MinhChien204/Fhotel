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

import learn.fpoly.fhotel.Adapter.BillAdapter;
import learn.fpoly.fhotel.Adapter.BookingAdapter;
import learn.fpoly.fhotel.Adapter.FavouriteAdapter;
import learn.fpoly.fhotel.Adapter.VoucherAdapter;
import learn.fpoly.fhotel.Model.Bill;
import learn.fpoly.fhotel.Model.Booking;
import learn.fpoly.fhotel.Model.Favourite;
import learn.fpoly.fhotel.Model.UserVoucher;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.Retrofit.HttpRequest;
import learn.fpoly.fhotel.response.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class BillFragment extends Fragment {
    private RecyclerView rcv_listbill;
    private String userId;
    private BillAdapter adapter;
    private TextView tvNoBill;
    private ImageView ivLoadingGifVoucher;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_bill, container, false);
        ivLoadingGifVoucher = view.findViewById(R.id.ivLoadingGifVoucher);
        tvNoBill = view.findViewById(R.id.tvNoBill);
        rcv_listbill = view.findViewById(R.id.rcv_listbill);
        rcv_listbill.setLayoutManager(new LinearLayoutManager(getContext()));

        // Lấy userId từ SharedPreferences (hoặc truyền vào fragment)
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        if (userId != null) {
            // Gọi API để lấy danh sách bookings
            getUserBills(userId);
        }
        return view;
    }

    private void getUserBills(String userId) {
        HttpRequest httpRequest = new HttpRequest();
        Call<Response<List<Bill>>> call = httpRequest.callAPI().getUserBills(userId);

        call.enqueue(new Callback<Response<List<Bill>>>() {
            @Override
            public void onResponse(Call<Response<List<Bill>>> call, retrofit2.Response<Response<List<Bill>>> response) {
                if (response.isSuccessful()) {
                    List<Bill> bills = response.body().getData();
                    if (bills != null && !bills.isEmpty()) {
                        ivLoadingGifVoucher.setVisibility(View.GONE);
                        tvNoBill.setVisibility(View.GONE); // Ẩn thông báo
                        adapter = new BillAdapter(getContext(),bills);
                        rcv_listbill.setAdapter(adapter);
                    } else {
                        ivLoadingGifVoucher.setVisibility(View.VISIBLE);
                        tvNoBill.setVisibility(View.VISIBLE);
                        Glide.with(getContext())
                                .asGif()
                                .load(R.drawable.bill_gif)
                                .into(ivLoadingGifVoucher);
                        adapter = new BillAdapter(getContext(), new ArrayList<>());
                        rcv_listbill.setAdapter(adapter);
                    }
                } else {
                    Toast.makeText(getContext(), "Error fetching bills.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<List<Bill>>> call, Throwable t) {
                Toast.makeText(getContext(), "Error."+t, Toast.LENGTH_LONG).show();
                Log.d("err", "onFailure: "+t);
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        if (userId != null) {
            getUserBills(userId);
        }
    }
}

