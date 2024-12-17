package learn.fpoly.fhotel.Fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import learn.fpoly.fhotel.Adapter.BookingAdapter;
import learn.fpoly.fhotel.Adapter.FavouriteAdapter;
import learn.fpoly.fhotel.Model.Booking;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.Retrofit.HttpRequest;
import learn.fpoly.fhotel.response.Response;
import retrofit2.Call;
import retrofit2.Callback;


public class BookingFragment extends Fragment {
    private RecyclerView rcvListUpcomingBooking;
    private String userId;
    private BookingAdapter adapter;
    private TextView tvNoBookings;
    private ImageView ivLoadingbookingGif, ivFilterBookings;
    private List<Booking> originalBookings = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_booking, container, false);
        tvNoBookings = view.findViewById(R.id.tvNoBookings);
        ivLoadingbookingGif = view.findViewById(R.id.ivLoadingbookingGif);
        ivFilterBookings = view.findViewById(R.id.ivFilterBookings);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        // Xử lý làm mới danh sách khi vuốt xuống
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (userId != null) {
                getBookings(userId); // Gọi lại API để lấy danh sách mới
            }
        });

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
        ivFilterBookings.setOnClickListener(v -> showFilterDialog());
        return view;
    }

    private void getBookings(String userId) {
        HttpRequest httpRequest = new HttpRequest();
        Call<Response<List<Booking>>> call = httpRequest.callAPI().getUserBookings(userId);

        call.enqueue(new Callback<Response<List<Booking>>>() {
            @Override
            public void onResponse(Call<Response<List<Booking>>> call, retrofit2.Response<Response<List<Booking>>> response) {
                swipeRefreshLayout.setRefreshing(false); // Tắt trạng thái làm mới
                if (response.isSuccessful()) {
                    List<Booking> bookings = response.body().getData();
                    if (bookings != null && !bookings.isEmpty()) {
                        originalBookings.clear(); // Làm sạch danh sách gốc
                        originalBookings.addAll(bookings); // Lưu lại danh sách gốc

                        ivLoadingbookingGif.setVisibility(View.GONE);
                        tvNoBookings.setVisibility(View.GONE);
                        adapter = new BookingAdapter(getContext(), bookings);
                        rcvListUpcomingBooking.setAdapter(adapter);
                    } else {
                        ivLoadingbookingGif.setVisibility(View.VISIBLE);
                        tvNoBookings.setVisibility(View.VISIBLE);
                        Glide.with(getContext())
                                .asGif()
                                .load(R.drawable.hotel_animation)
                                .into(ivLoadingbookingGif);
                        adapter = new BookingAdapter(getContext(), new ArrayList<>());
                        rcvListUpcomingBooking.setAdapter(adapter);
                    }
                } else {
                    Toast.makeText(getContext(), "No room was found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<List<Booking>>> call, Throwable t) {
                Toast.makeText(getContext(), "Error." + t, Toast.LENGTH_LONG).show();
                Log.d("err", "onFailure: " + t);
            }
        });
    }


    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Lọc Bookings");

        // Tùy chọn lọc
        String[] filterOptions = {"Lọc theo trạng thái", "Lọc theo ngày đặt"};
        builder.setItems(filterOptions, (dialog, which) -> {
            if (which == 0) {
                showStatusFilterDialog();
            } else if (which == 1) {
                showDateRangePicker();
            }
        });

        builder.create().show();
    }

    private void showStatusFilterDialog() {
        String[] statuses = {"pending", "confirmed", "cancelled"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Lọc theo trạng thái");

        builder.setItems(statuses, (dialog, which) -> {
            String selectedStatus = statuses[which];
            filterBookingsByStatus(selectedStatus);
        });

        builder.create().show();
    }

    // Hiển thị dialog lọc theo ngày
    private void showDateRangePicker() {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select Date Range");
        MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            // Lấy giá trị ngày bắt đầu và kết thúc
            long startDate = selection.first;
            long endDate = selection.second;

            // Kiểm tra nếu ngày bắt đầu và kết thúc trùng nhau
            if (startDate == endDate) {
                Toast.makeText(getContext(), "Ngày bắt đầu và kết thúc không được trùng nhau.", Toast.LENGTH_SHORT).show();
                return;  // Dừng không lọc nếu ngày trùng nhau
            }

            // Format ngày
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String start = sdf.format(new Date(startDate));
            String end = sdf.format(new Date(endDate));

            // Lọc dữ liệu
            filterBookingsByDate(start, end);
        });


        datePicker.show(getChildFragmentManager(), "DATE_PICKER");
    }


    // Lọc danh sách theo trạng thái
    private void filterBookingsByStatus(String status) {
        if (originalBookings != null && !originalBookings.isEmpty()) {
            List<Booking> filteredList = new ArrayList<>();
            for (Booking booking : originalBookings) {
                if (booking.getStatus().equalsIgnoreCase(status)) {
                    filteredList.add(booking);
                }
            }
            updateBookingList(filteredList);
        }

    }


    // Lọc danh sách theo ngày
    private void filterBookingsByDate(String startDate, String endDate) {
        if (originalBookings == null || originalBookings.isEmpty()) return;

        // Ghi log các giá trị startDate và endDate
        Log.d("FilterByDate", "Start Date: " + startDate + ", End Date: " + endDate);

        List<Booking> filteredList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        try {
            // Parse startDate và endDate
            Date start = dateFormat.parse(startDate);
            Date end = dateFormat.parse(endDate);

            for (Booking booking : originalBookings) { // Sử dụng originalBookings thay vì adapter.getBookings()
                try {
                    // Lấy ngày gốc từ booking
                    String rawDate = booking.getCreatedAt();
                    Log.d("FilterByDate", "Raw Booking Date: " + rawDate);

                    // Parse ISO 8601 thành LocalDateTime
                    Instant instant = Instant.parse(rawDate);
                    Date bookingDate = Date.from(instant);

                    // Ghi log ngày đã parse
                    Log.d("FilterByDate", "Parsed Booking Date: " + dateFormat.format(bookingDate));

                    // Kiểm tra khoảng thời gian
                    if (!bookingDate.before(start) && !bookingDate.after(end)) {
                        filteredList.add(booking);
                    }
                } catch (Exception e) {
                    Log.e("FilterByDate", "Error parsing booking date: " + booking.getCreatedAt(), e);
                }
                updateBookingList(filteredList);
            }
        } catch (Exception e) {
            Log.e("FilterByDate", "Error parsing filter dates", e);
        }

        // Hiển thị kết quả
        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "No bookings found for the selected date range.", Toast.LENGTH_SHORT).show();
        }
        adapter.updateList(filteredList); // Update adapter data
    }

    private void updateBookingList(List<Booking> filteredList) {
        if (filteredList.isEmpty()) {
            ivLoadingbookingGif.setVisibility(View.VISIBLE);
            tvNoBookings.setVisibility(View.VISIBLE);
            Glide.with(getContext())
                    .asGif()
                    .load(R.drawable.hotel_animation)  // Đảm bảo GIF đúng
                    .into(ivLoadingbookingGif);
            rcvListUpcomingBooking.setVisibility(View.GONE);  // Ẩn RecyclerView
        } else {
            ivLoadingbookingGif.setVisibility(View.GONE);
            tvNoBookings.setVisibility(View.GONE);
            rcvListUpcomingBooking.setVisibility(View.VISIBLE);  // Hiển thị RecyclerView
            adapter.updateList(filteredList);  // Cập nhật danh sách cho adapter
        }
    }



}