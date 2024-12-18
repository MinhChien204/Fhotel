package learn.fpoly.fhotel.Fragment;

import static android.content.ContentValues.TAG;

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

import learn.fpoly.fhotel.Model.Bill;
import learn.fpoly.fhotel.Model.Booking;
import learn.fpoly.fhotel.Model.Notification;
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
//        rbPayVNPay = findViewById(R.id.rb_pay_vnpay);
        rbPayCash = findViewById(R.id.rb_pay_cash);
        handleIncomingIntent(getIntent());


        Intent intent = getIntent();

        totalPrice.setText("0đ");
        tvPriceDetails.setText("Giá phòng: 0đ\nSố ngày: 0\nGiá: 0đ");
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
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
                    Toast.makeText(this, "Ngày bắt đầu và kết thúc không được trùng nhau", Toast.LENGTH_SHORT).show();
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

            // Lấy giá trị tổng tiền bằng cách loại bỏ "đ" nhưng giữ nguyên định dạng số tiền
            String totalText = totalPrice.getText().toString().replace("đ", "").trim(); // Loại bỏ ký tự "đ"
            float total;
            try {
                total = Float.parseFloat(totalText.replace(".", "").trim());  // Loại bỏ dấu "." để chuyển thành float
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Lỗi định dạng tổng tiền!", Toast.LENGTH_SHORT).show();
                Log.e("PaymentActivity", "Error parsing total price: " + e.getMessage());
                return;
            }

            if (total <= 0) {
                Toast.makeText(this, "Invalid booking details", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra phòng đã được đặt chưa
            checkRoomAvailable(roomId, startDate, endDate, isAvailable -> {
                Log.d("kkk", "onCreate: " + isAvailable);
                if (isAvailable) {
                    // Nếu phòng chưa được đặt, tiến hành thanh toán với ZaloPay
                    int selectedPaymentMethod = paymentMethodsGroup.getCheckedRadioButtonId();
                    if (selectedPaymentMethod == R.id.rb_pay_zalopay) {
                        // Thực hiện thanh toán qua ZaloPay
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
                                        intent1.putExtra("result", "Đặt phòng thành công!");

                                        // Cập nhật booking thành công
                                        Booking booking = new Booking(userId, roomId, startDate, endDate, total);
                                        Bill bill = new Bill(userId, roomId, startDate, endDate, total);
                                        createBooking(booking);

//                                        createBill(bill);
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
                    } else if (selectedPaymentMethod == R.id.rb_pay_cash) {
                        // Xử lý thanh toán tiền mặt
                        Booking booking = new Booking(userId, roomId, startDate, endDate, total);
                        createBookingCash(booking);
                    } else {
                        Toast.makeText(this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PaymentActivity.this, "Phòng đã được đặt trong khoảng thời gian này.", Toast.LENGTH_SHORT).show();
                }
            });
        });


        btnback.setOnClickListener(view1 -> onBackPressed());


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
                Log.e("PaymentActivity", "Ngày bắt đầu phải trước ngày kết thúc");
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
            String priceText = tvpriceKS.getText().toString().trim();
            if (!priceText.isEmpty()) {
                // Tính toán tổng giá
                if (numberOfNights > 0) {
                    String cleanPriceText = priceText.replace("đ", "").trim();
                    float roomPrice = Float.parseFloat(cleanPriceText.replace(".", ""));
                    float totalPriceValue = roomPrice * numberOfNights;
                    totalPrice.setText(String.format("%,.0f đ", totalPriceValue).replace(',', '.'));
                    tvPriceDetails.setText(String.format("Giá phòng: %,.0f đ\nSố ngày: %d\nGiá: %,.0f đ", roomPrice, numberOfNights, totalPriceValue).replace(',', '.'));
                } else {
                    totalPrice.setText("0 đ");
                    tvPriceDetails.setText("Số ngày: 0\nTổng giá: 0 đ");
                }
            } else {
                Log.e("PaymentActivity", "Room price is empty or invalid");
                totalPrice.setText("0 đ");
                tvPriceDetails.setText("Lỗi tính tổng giá");
            }
        } catch (NumberFormatException e) {
            Log.e("PaymentActivity", "Error parsing room price: " + e.getMessage());
            totalPrice.setText("0 đ");
            tvPriceDetails.setText("Lỗi tính tổng giá");
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
                    Booking booking = response.body().getData();
                    if (response.body() != null && response.body().getData() != null) {
                        String roomName = booking.getRoom().getName();
                        String startDate = booking.getStartDate();
                        String endDate = booking.getEndDate();
                        String message = "Bạn đã đặt phòng " + roomName + " từ ngày " + startDate + " đến ngày " + endDate;
                        sendNotification(booking.getUserId(), message, "booking_confirmed");
//                        Toast.makeText(PaymentActivity.this, "Booking successful!", Toast.LENGTH_SHORT).show();
                        String bookingId = response.body().getData().getId();
                        updateBookingStatus(bookingId, "confirmed", "paid"); // Trạng thái đã xác nhận và đã thanh toán
                        Intent intent = new Intent(PaymentActivity.this, PaymentNotification.class);
                        intent.putExtra("result", "Thanh toán thành công");
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


    //bill
    private void createBill(Bill bill) {
        // Khởi tạo Retrofit
        HttpRequest httpRequest = new HttpRequest();
        Call<Response<Bill>> call = httpRequest.callAPI().createBill(bill);

        // Gửi yêu cầu đến server
        call.enqueue(new Callback<Response<Bill>>() {
            @Override
            public void onResponse(Call<Response<Bill>> call, retrofit2.Response<Response<Bill>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getData() != null) {
//                        Toast.makeText(PaymentActivity.this, "Bill successful!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("Booking", "Bill failed with no data in response.");
                        Toast.makeText(PaymentActivity.this, "Error: No data in Bill response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("Booking", "Booking failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Response<Bill>> call, Throwable t) {
                Log.e("Bill", "Error creating Bill: " + t.getMessage());
                Toast.makeText(PaymentActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //
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
        setIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
        handleIncomingIntent(intent);
    }

    private void handleIncomingIntent(Intent intent) {
        if (intent != null && intent.hasExtra("room_id")) {
            roomId = intent.getStringExtra("room_id");
            String roomName = intent.getStringExtra("room_name");
            float roomRating = intent.getFloatExtra("room_rating", 0);
            String roomPrice = intent.getStringExtra("room_price");
            String roomImageURL = intent.getStringExtra("room_image");
            String roomCapacity = intent.getStringExtra("room_capacity");

            // Cập nhật giao diện
            tvnameKS.setText(roomName);
            tvpriceKS.setText(roomPrice);
            ratingBar.setRating(roomRating);
            tvcapacityKS.setText(roomCapacity);

            // Hiển thị ảnh với Glide
            Glide.with(this).load(roomImageURL).into(roomImage);

            // Làm mới tổng giá và các chi tiết liên quan
            numberOfNights = 0;
            tvdate.setText("");
            tvPerson.setText("");
            totalPrice.setText("0đ");
            tvPriceDetails.setText("Giá phòng: 0đ\nSố ngày: 0\nGiá: 0đ");
        }
    }

    private void updateBookingStatus(String bookingId, String newStatus, String paymentStatus) {
        HttpRequest httpRequest = new HttpRequest();
        Call<Response<Booking>> call = httpRequest.callAPI().updateBookingStatus(bookingId, new Booking(newStatus, paymentStatus));

        call.enqueue(new Callback<Response<Booking>>() {
            @Override
            public void onResponse(Call<Response<Booking>> call, retrofit2.Response<Response<Booking>> response) {
                if (response.isSuccessful()) {
                    // Kiểm tra và cập nhật thông tin của booking chỉ cần thiết
//                    Toast.makeText(PaymentActivity.this, "Trạng thái đã được cập nhật!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PaymentActivity.this, "Không thể cập nhật trạng thái", Toast.LENGTH_SHORT).show();
                    Log.d("BookingStatus", "Response: " + response.body());
                }
            }

            @Override
            public void onFailure(Call<Response<Booking>> call, Throwable t) {
                Log.d("stbk", "onFailure: " + t);
                Toast.makeText(PaymentActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createBookingCash(Booking booking) {
        // Khởi tạo Retrofit
        HttpRequest httpRequest = new HttpRequest();
        Call<Response<Booking>> call = httpRequest.callAPI().createBooking(booking);

        // Gửi yêu cầu đến server
        call.enqueue(new Callback<Response<Booking>>() {
            @Override
            public void onResponse(Call<Response<Booking>> call, retrofit2.Response<Response<Booking>> response) {
                if (response.isSuccessful()) {
                    Booking booking = response.body().getData();
                    if (response.body() != null && response.body().getData() != null) {
                        String roomName = booking.getRoom().getName();
                        String startDate = booking.getStartDate();
                        String endDate = booking.getEndDate();
                        String message = "Bạn đã đặt phòng " + roomName + " từ ngày " + startDate + " đến ngày " + endDate;
                        sendNotification(booking.getUserId(), message, "booking_confirmed");
//                        Toast.makeText(PaymentActivity.this, "Booking successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PaymentActivity.this, PaymentNotification.class);
                        intent.putExtra("result", "Gửi yêu cầu đặt phòng thành công");
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

    private void checkRoomAvailable(String roomId, String startDate, String endDate, CheckAvailabilityCallback callback) {
        // Gọi API hoặc truy vấn cơ sở dữ liệu để kiểm tra xem phòng đã được đặt chưa
        HttpRequest httpRequest = new HttpRequest();
        Call<Response<Boolean>> call = httpRequest.callAPI().checkRoomAvailability(roomId, startDate, endDate);

        call.enqueue(new Callback<Response<Boolean>>() {
            @Override
            public void onResponse(Call<Response<Boolean>> call, retrofit2.Response<Response<Boolean>> response) {
                if (response.isSuccessful()) {
                    callback.onResult(response.body().getData());
                } else {
                    callback.onResult(false);
                }
            }

            @Override
            public void onFailure(Call<Response<Boolean>> call, Throwable t) {
                callback.onResult(false);
            }
        });
    }

    interface CheckAvailabilityCallback {
        void onResult(boolean isAvailable);
    }

    private void sendNotification(String userId, String message, String type) {
        // Chuẩn bị thông tin thông báo
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setType(type);

        // Gọi API thêm thông báo
        HttpRequest httpRequest = new HttpRequest();
        Call<Response<Notification>> notificationCall = httpRequest.callAPI().createNotification(notification);

        notificationCall.enqueue(new Callback<Response<Notification>>() {
            @Override
            public void onResponse(Call<Response<Notification>> call, retrofit2.Response<Response<Notification>> response) {
                if (response.isSuccessful()) {
                    Log.d("Notification", "Notification created successfully");
                } else {
                    Log.e("Notification", "Failed to create notification");
                }
            }

            @Override
            public void onFailure(Call<Response<Notification>> call, Throwable t) {
                Log.e("Notification", "Error: " + t.getMessage());
            }
        });
    }
}
