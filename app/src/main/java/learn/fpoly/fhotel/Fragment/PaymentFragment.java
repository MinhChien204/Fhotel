package learn.fpoly.fhotel.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import learn.fpoly.fhotel.Model.Booking;
import learn.fpoly.fhotel.Model.Room;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.Retrofit.HttpRequest;
import learn.fpoly.fhotel.activity.Home_User;
import learn.fpoly.fhotel.dialog.SelectDateBottomSheet;
import learn.fpoly.fhotel.dialog.SelectGuestBottomSheet;
import retrofit2.Call;
import retrofit2.Callback;
import learn.fpoly.fhotel.response.Response;

public class PaymentFragment extends Fragment {
    TextView tvdate, tvPerson, tvnameKS, tvpriceKS, tvcapacityKS, totalPrice, tvPriceDetails;
    ImageView btnback, roomImage;
    Button btnpay;
    RatingBar ratingBar;
    private String userId, roomId;
    private int numberOfNights = 0; // Số đêm mặc định
    private float roomPricePerNight = 120; // Giá phòng mỗi đêm mặc định
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        // Ánh xạ các view
        tvdate = view.findViewById(R.id.dates);
        tvPerson = view.findViewById(R.id.guests);
        btnback = view.findViewById(R.id.ivBackPayment);
        tvnameKS = view.findViewById(R.id.titleText);
        tvpriceKS = view.findViewById(R.id.priceText);
        tvcapacityKS = view.findViewById(R.id.capacityText);
        totalPrice = view.findViewById(R.id.totalPrice);
        tvPriceDetails = view.findViewById(R.id.price_details);
        roomImage = view.findViewById(R.id.roomImage);
        ratingBar = view.findViewById(R.id.ratingBarPayment);
        btnpay = view.findViewById(R.id.pay_now_button);

        totalPrice.setText("$0.00");
        tvPriceDetails.setText("Room Price: $0.00\nNumber of nights: 0\nTotal Price: $0.00");

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        // Extract the roomId from the bundle


        Bundle arguments = getArguments();
        if (arguments != null) {
            roomId = arguments.getString("room_id");
            Log.d("roomid nek", "onCreateView: "+roomId);
            String roomName = arguments.getString("room_name");
            float roomRating = arguments.getFloat("room_rating", 0);
            String roomPrice = arguments.getString("room_price");
            String roomImageURL = arguments.getString("room_image");
            String roomCapacity = arguments.getString("room_capacity");
            // Xử lý sự kiện chọn ngày
            tvdate.setOnClickListener(v -> {
                // Tạo và hiển thị bottom sheet chọn ngày
                SelectDateBottomSheet bottomSheet = new SelectDateBottomSheet();
                bottomSheet.setOnDateSelectedListener(dateRange -> {
                    // Cập nhật TextView với ngày đã chọn
                    tvdate.setText(dateRange);

                    numberOfNights = calculateNights(dateRange);

                    if (numberOfNights > 0) {
                        // Nếu có số đêm hợp lệ, cập nhật tổng giá
                        updatePriceDetails();
                    } else {
                        Toast.makeText(getContext(), "Invalid date range selected", Toast.LENGTH_SHORT).show();
                    }
                });
                bottomSheet.show(getParentFragmentManager(), "SelectDateBottomSheet");
            });

            tvPerson.setOnClickListener(view1 -> showSelectGuestBottomSheet());

            btnpay.setOnClickListener(v -> {
                String dateRange = tvdate.getText().toString().trim();
                if (dateRange.isEmpty() || !dateRange.contains("To:")) {
                    Toast.makeText(getContext(), "Vui lòng chọn ngày đặt phòng!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String startDate = dateRange.split("To:")[0].replace("From:", "").trim();
                String endDate = dateRange.split("To:")[1].trim();
                float total = Float.parseFloat(totalPrice.getText().toString().replace("$", "").trim());
                if (total <= 0) {
                    Toast.makeText(getContext(), "Invalid booking details", Toast.LENGTH_SHORT).show();
                    return;
                }

                Booking booking = new Booking(userId, roomId, startDate, endDate, total);

                createBooking(booking);
            });


            btnback.setOnClickListener(view1 -> {
                if (getFragmentManager() != null && getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();
                }
            });


            tvnameKS.setText(roomName);
            tvpriceKS.setText(roomPrice);
            ratingBar.setRating(roomRating);
            tvcapacityKS.setText(roomCapacity);

            // Hiển thị ảnh với Glide
            Glide.with(this)
                    .load(roomImageURL)
                    .into(roomImage);
        }
        return view;
    }

    private void showSelectGuestBottomSheet() {
        SelectGuestBottomSheet bottomSheet = new SelectGuestBottomSheet();
        bottomSheet.setOnGuestSelectedListener((adults, children, infants) -> {
            String summary = "Adults: " + adults + " | Children: " + children + " | Infants: " + infants;
            tvPerson.setText(summary);
        });
        bottomSheet.show(getChildFragmentManager(), "SelectGuestBottomSheet");
    }
    private int calculateNights(String dateRange) {
        if (dateRange == null || !dateRange.contains("To:")) {
            Log.e("PaymentFragment", "Date range format is invalid: " + dateRange);
            return 0;
        }
        try {
            String cleanedDateRange = dateRange.replace("From:", "").replace("To:", "").trim();
            String[] dates = cleanedDateRange.split("\\s+");

            if (dates.length < 2) {
                Log.e("PaymentFragment", "Not enough dates in range: " + cleanedDateRange);
                return 0;
            }
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

            Date startDate = format.parse(dates[0].trim());
            Date endDate = format.parse(dates[1].trim());

            if (startDate != null && endDate != null && endDate.after(startDate)) {
                long diffInMillis = endDate.getTime() - startDate.getTime();
                return (int) TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
            } else {
                Log.e("PaymentFragment", "Start date must be before end date");
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PaymentFragment", "Error parsing date range: " + e.getMessage());
            return 0;
        }
    }

    private void updatePriceDetails() {
        try {
            // Lấy giá trị từ TextView tvpriceKS và chuyển đổi sang float
            String priceText = tvpriceKS.getText().toString().replace("$", "").trim();
            float roomPrice = Float.parseFloat(priceText); // Chuyển đổi chuỗi thành số thực

            // Tính toán tổng giá
            if (numberOfNights > 0 && roomPrice > 0) {
                float totalPriceValue = roomPrice * numberOfNights;

                // Hiển thị tổng giá và chi tiết
                totalPrice.setText(String.format("$%.2f", totalPriceValue));
                tvPriceDetails.setText(String.format("Room Price: $%.2f\nNumber of nights: %d\nTotal Price: $%.2f",roomPrice, numberOfNights, totalPriceValue));
            } else {
                totalPrice.setText("$0.00");
                tvPriceDetails.setText("Number of nights: 0\nTotal Price: $0.00");
            }
        } catch (NumberFormatException e) {
            Log.e("PaymentFragment", "Error parsing room price: " + e.getMessage());
            totalPrice.setText("$0.00");
            tvPriceDetails.setText("Error calculating total price");
        }
    }

    private void createBooking(Booking booking) {
        // Khởi tạo Retrofit
        HttpRequest httpRequest = new HttpRequest();
        Call<Response<Booking>> call = httpRequest.callAPI().createBooking(booking);

        // Gửi yêu cầu đến server
        call.enqueue(new Callback<Response<Booking>>() {
            @Override
            public void onResponse(Call<Response<Booking>> call, retrofit2.Response<Response<Booking>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Booking successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), Home_User.class);
                    intent.putExtra("fragment_to_load", R.id.navBooking_u);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    Toast.makeText(getContext(), "Đã có người đặt cùng ngày phòng này", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<Booking>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
