package learn.fpoly.fhotel.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import learn.fpoly.fhotel.Model.Booking;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.Retrofit.HttpRequest;
import learn.fpoly.fhotel.activity.Home_User;
import learn.fpoly.fhotel.activity.PaymentNotification;
import learn.fpoly.fhotel.activity.VnPayment;
import learn.fpoly.fhotel.dialog.SelectDateBottomSheet;
import learn.fpoly.fhotel.dialog.SelectGuestBottomSheet;
import learn.fpoly.fhotel.utils.VNPayUtils;
import learn.fpoly.fhotel.zalopay.Api.CreateOrder;
import retrofit2.Call;
import retrofit2.Callback;
import learn.fpoly.fhotel.response.Response;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class PaymentActivity extends AppCompatActivity {

    TextView tvdate, tvPerson, tvnameKS, tvpriceKS, tvcapacityKS, totalPrice, tvPriceDetails;
    ImageView btnback, roomImage;
    Button btnpay;
    RatingBar ratingBar;
    private RadioGroup paymentMethodsGroup;
    private RadioButton rbPayZalopay, rbPayVNPay, rbPayCash;

    private String userId, roomId;
    private int numberOfNights = 0; // Số đêm mặc định
    private float roomPricePerNight = 120; // Giá phòng mỗi đêm mặc định

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_payment); // Use your new activity layout

        // Ánh xạ các view
        tvdate = findViewById(R.id.dates);
        tvPerson = findViewById(R.id.guests);
        btnback = findViewById(R.id.ivBackPayment);
        tvnameKS = findViewById(R.id.titleText);
        tvpriceKS = findViewById(R.id.priceText);
        tvcapacityKS = findViewById(R.id.capacityText);
        totalPrice = findViewById(R.id.totalPrice);
        tvPriceDetails = findViewById(R.id.price_details);
        roomImage = findViewById(R.id.roomImage);
        ratingBar = findViewById(R.id.ratingBarPayment);
        btnpay = findViewById(R.id.pay_now_button);
        paymentMethodsGroup = findViewById(R.id.payment_methods_group);

        rbPayZalopay = findViewById(R.id.rb_pay_zalopay);
        rbPayVNPay = findViewById(R.id.rb_pay_vnpay);
        rbPayCash = findViewById(R.id.rb_pay_cash);



        Intent intent = getIntent();

        totalPrice.setText("$0.00");
        tvPriceDetails.setText("Room Price: $0.00\nNumber of nights: 0\nTotal Price: $0.00");

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        // Extract the roomId from the intent extras
        if (intent != null) {
            roomId = intent.getStringExtra("room_id");
            Log.d("roomid nek", "onCreate: " + roomId);
            String roomName = intent.getStringExtra("room_name");
            float roomRating = intent.getFloatExtra("room_rating", 0);
            String roomPrice = intent.getStringExtra("room_price");
            String roomImageURL = intent.getStringExtra("room_image");
            String roomCapacity = intent.getStringExtra("room_capacity");

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
                        Toast.makeText(this, "Invalid date range selected", Toast.LENGTH_SHORT).show();
                    }
                });
                bottomSheet.show(getSupportFragmentManager(), "SelectDateBottomSheet");
            });

            tvPerson.setOnClickListener(view1 -> showSelectGuestBottomSheet());

            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            // ZaloPay SDK Init
            ZaloPaySDK.init(2553, Environment.SANDBOX);


            btnpay.setOnClickListener(view -> {
                String dateRange = tvdate.getText().toString().trim();
                if (dateRange.isEmpty() || !dateRange.contains("To:")) {
                    Toast.makeText(this, "Vui lòng chọn ngày đặt phòng!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String startDate = dateRange.split("To:")[0].replace("From:", "").trim();
                String endDate = dateRange.split("To:")[1].trim();

                // Lấy giá trị tổng tiền bằng cách loại bỏ "VND " và chuyển đổi thành float
                String totalText = totalPrice.getText().toString().replace("đ", "").trim();
                float total;
                try {
                    total = Float.parseFloat(totalText);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Lỗi định dạng tổng tiền!", Toast.LENGTH_SHORT).show();
                    Log.e("PaymentActivity", "Error parsing total price: " + e.getMessage());
                    return;
                }

                if (total <= 0) {
                    Toast.makeText(this, "Invalid booking details", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra phương thức thanh toán
                int selectedPaymentMethod = paymentMethodsGroup.getCheckedRadioButtonId();
                if(selectedPaymentMethod == R.id.rb_pay_zalopay){
                    CreateOrder orderApi = new CreateOrder();
                    try {
                        JSONObject data = orderApi.createOrder(String.valueOf((long) total));
                        String code = data.getString("return_code");
                        if (code.equals("1")) {
                            String token = data.getString("zp_trans_token");
                            ZaloPaySDK.getInstance().payOrder(PaymentActivity.this, token, "demozpdk://app", new PayOrderListener() {
                                @Override
                                public void onPaymentSucceeded(String s, String s1, String s2) {
                                    Intent intent1 = new Intent(PaymentActivity.this, PaymentNotification.class);
                                    intent1.putExtra("result", "Thanh toán thành công");

                                    // Cập nhật booking thành công
                                    Booking booking = new Booking(userId, roomId, startDate, endDate, total);
                                    booking.setStatus("confirmed");  // Đã xác nhận
                                    booking.setPaymentStatus("paid");  // Đã thanh toán
                                    createBooking(booking);

                                    // Điều hướng sang màn hình thông báo thanh toán thành công
                                    startActivity(intent1);
                                }
                                @Override
                                public void onPaymentCanceled(String s, String s1) {
                                    Intent intent1 = new Intent(PaymentActivity.this, PaymentNotification.class);
                                    intent1.putExtra("result", "Hủy thanh toán");
                                    startActivity(intent1);
                                }
                                @Override
                                public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                                    Intent intent1 = new Intent(PaymentActivity.this, PaymentNotification.class);
                                    intent1.putExtra("result", "Lỗi thanh toán");
                                    startActivity(intent1);
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error creating ZaloPay order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else if (selectedPaymentMethod == R.id.rb_pay_vnpay) {
                    processVNPayPayment(startDate, endDate, total);
                } else if (selectedPaymentMethod == R.id.rb_pay_cash) {
                    Booking booking = new Booking(userId, roomId, startDate, endDate, total);
                    booking.setStatus("pending");  // Trạng thái chờ xác nhận
                    booking.setPaymentStatus("unpaid");  // Chưa thanh toán
                    createBooking(booking);
                } else {
                    Toast.makeText(this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
                }
            });


            btnback.setOnClickListener(view1 -> onBackPressed());

            tvnameKS.setText(roomName);
            tvpriceKS.setText("đ " + roomPrice);
            ratingBar.setRating(roomRating);
            tvcapacityKS.setText(roomCapacity);

            // Hiển thị ảnh với Glide
            Glide.with(this)
                    .load(roomImageURL)
                    .into(roomImage);
        }
    }

    private void showSelectGuestBottomSheet() {
        SelectGuestBottomSheet bottomSheet = new SelectGuestBottomSheet();
        bottomSheet.setOnGuestSelectedListener((adults, children, infants) -> {
            String summary = "Adults: " + adults + " | Children: " + children + " | Infants: " + infants;
            tvPerson.setText(summary);
        });
        bottomSheet.show(getSupportFragmentManager(), "SelectGuestBottomSheet");
    }

    private int calculateNights(String dateRange) {
        if (dateRange == null || !dateRange.contains("To:")) {
            Log.e("PaymentActivity", "Date range format is invalid: " + dateRange);
            return 0;
        }
        try {
            String cleanedDateRange = dateRange.replace("From:", "").replace("To:", "").trim();
            String[] dates = cleanedDateRange.split("\\s+");

            if (dates.length < 2) {
                Log.e("PaymentActivity", "Not enough dates in range: " + cleanedDateRange);
                return 0;
            }
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

            Date startDate = format.parse(dates[0].trim());
            Date endDate = format.parse(dates[1].trim());

            if (startDate != null && endDate != null && endDate.after(startDate)) {
                long diffInMillis = endDate.getTime() - startDate.getTime();
                return (int) TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
            } else {
                Log.e("PaymentActivity", "Start date must be before end date");
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PaymentActivity", "Error parsing date range: " + e.getMessage());
            return 0;
        }
    }

    // Trong phương thức updatePriceDetails:
    private void updatePriceDetails() {
        try {
            // Lấy giá trị từ TextView tvpriceKS và chuyển đổi sang float
            String priceText = tvpriceKS.getText().toString().replace("đ ", "").trim();
            float roomPrice = Float.parseFloat(priceText); // Chuyển đổi chuỗi thành số thực

            // Tính toán tổng giá
            if (numberOfNights > 0 && roomPrice > 0) {
                float totalPriceValue = roomPrice * numberOfNights;

                // Hiển thị tổng giá và chi tiết, thay "VND " phía trước thành " VND" phía sau
                totalPrice.setText(String.format("%.0f đ", totalPriceValue)); // Thêm "VND" vào sau
                tvPriceDetails.setText(String.format("Room Price: %.0f đ\nNumber of nights: %d\nTotal Price: %.0f đ", roomPrice, numberOfNights, totalPriceValue));
            } else {
                totalPrice.setText("0 đ");
                tvPriceDetails.setText("Number of nights: 0\nTotal Price: 0 đ");
            }
        } catch (NumberFormatException e) {
            Log.e("PaymentActivity", "Error parsing room price: " + e.getMessage());
            totalPrice.setText("0 đ");
            tvPriceDetails.setText("Error calculating total price");
        }
    }



// Trong phần xử lý giá phòng:


    private void createBooking(Booking booking) {
        // Khởi tạo Retrofit
        HttpRequest httpRequest = new HttpRequest();
        Call<Response<Booking>> call = httpRequest.callAPI().createBooking(booking);

        // Gửi yêu cầu đến server
        call.enqueue(new Callback<Response<Booking>>() {
            @Override
            public void onResponse(Call<Response<Booking>> call, retrofit2.Response<Response<Booking>> response) {
                if (response.isSuccessful()) {
                    // Kiểm tra kết quả từ API
                    if (response.body() != null && response.body().getData() != null) {
                        Log.d("Booking", "Booking successful: " + response.body().getData().toString());
                        Toast.makeText(PaymentActivity.this, "Booking successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PaymentActivity.this, PaymentNotification.class);
                        intent.putExtra("result", "Thanh toán thành cong");
                        startActivity(intent);
                    } else {
                        Log.e("Booking", "Booking failed with no data in response.");
                        Toast.makeText(PaymentActivity.this, "Error: No data in booking response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("Booking", "Booking failed: " + response.message());
                    Toast.makeText(PaymentActivity.this, "Đã có người đặt cùng ngày phòng này", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<Booking>> call, Throwable t) {
                Log.e("Booking", "Error creating booking: " + t.getMessage());
                Toast.makeText(PaymentActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void processVNPayPayment(String startDate, String endDate, float totalAmount) {
        try {
            String paymentUrl = VNPayUtils.generateVNPayUrl(roomId, (long) totalAmount);

            // Chuyển hướng đến PaymentActivity để mở WebView
            Intent intent = new Intent(PaymentActivity.this, VnPayment.class);
            intent.putExtra("paymentUrl", paymentUrl);
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(PaymentActivity.this, "Error generating VNPay URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}
