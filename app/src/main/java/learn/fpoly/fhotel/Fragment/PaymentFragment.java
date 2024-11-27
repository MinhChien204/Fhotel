package learn.fpoly.fhotel.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
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
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.Retrofit.HttpRequest;
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
    private String userId;
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

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

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

        // Xử lý sự kiện chọn khách
        tvPerson.setOnClickListener(view1 -> showSelectGuestBottomSheet());

        //booking
        btnpay.setOnClickListener(v -> {
            // Lấy thông tin cần thiết
            String startDate = tvdate.getText().toString().split("To:")[0].replace("From:", "").trim();
            String endDate = tvdate.getText().toString().split("To:")[1].trim();
            float total = Float.parseFloat(totalPrice.getText().toString().replace("$", "").trim());
            String roomId = getArguments().getString("room_id");  // Lấy roomId
            // Kiểm tra thông tin hợp lệ
            if (startDate.isEmpty() || endDate.isEmpty() || total <= 0) {
                Toast.makeText(getContext(), "Invalid booking details", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo đối tượng booking
            Booking booking = new Booking(userId, roomId, startDate, endDate, total);

            // Gọi API để tạo booking
            createBooking(booking);
        });


        // Xử lý sự kiện nút Back
        btnback.setOnClickListener(view1 -> {
            if (getFragmentManager() != null && getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            }
        });

        Bundle arguments = getArguments();
        if (arguments != null) {
            String roomName = arguments.getString("room_name");
            float roomRating = arguments.getFloat("room_rating", 0);
            String roomPrice = arguments.getString("room_price");
            String roomImageURL = arguments.getString("room_image");
            String roomCapacity = arguments.getString("room_capacity");

            // Gán dữ liệu vào các View
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
                    // Nếu booking thành công, hiển thị thông báo
                    Toast.makeText(getContext(), "Booking successful!", Toast.LENGTH_SHORT).show();
                    // Có thể chuyển hướng đến màn hình khác
                } else {
                    // Nếu có lỗi, hiển thị thông báo
                    Toast.makeText(getContext(), "Booking failed: Không được đặt cùng số ngày ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<Booking>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




}
