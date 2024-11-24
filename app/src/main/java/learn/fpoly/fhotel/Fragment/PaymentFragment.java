package learn.fpoly.fhotel.Fragment;

import static android.content.Intent.getIntent;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import learn.fpoly.fhotel.Model.Room;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.Retrofit.HttpRequest;
import learn.fpoly.fhotel.activity.DetailsActivity;
import learn.fpoly.fhotel.dialog.SelectDateBottomSheet;
import learn.fpoly.fhotel.dialog.SelectGuestBottomSheet;
import learn.fpoly.fhotel.response.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class PaymentFragment extends Fragment {
    TextView tvdate, tvPerson, tvnameKS, tvpriceKS, tvcapacityKS;
    ImageView btnback, roomImage;
    HttpRequest httpRequest;
    RatingBar ratingBar;

    private TextView priceDetailsTextView;
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
        roomImage = view.findViewById(R.id.roomImage);
        ratingBar = view.findViewById(R.id.ratingBarPayment);

        httpRequest = new HttpRequest(); // Khởi tạo HttpRequest


        // Xử lý sự kiện chọn ngày
        tvdate.setOnClickListener(v -> {
            SelectDateBottomSheet bottomSheet = new SelectDateBottomSheet();
            bottomSheet.setOnDateSelectedListener(dateRange -> {
                tvdate.setText(dateRange);

                // Tính số đêm từ khoảng ngày
                numberOfNights = calculateNights(dateRange);

                // Kiểm tra và cập nhật tổng giá
                if (numberOfNights > 0) {
                    updateTotalPrice(); // Cập nhật tổng giá
                } else {
                    Toast.makeText(getContext(), "Invalid date range selected", Toast.LENGTH_SHORT).show();
                }
            });
            bottomSheet.show(getParentFragmentManager(), "SelectDateBottomSheet");
        });




        // Xử lý sự kiện chọn khách
        tvPerson.setOnClickListener(view1 -> showSelectGuestBottomSheet());

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
            String roomPrice = arguments.getString("room_price"); // Giá dưới dạng chuỗi
            String roomImageURL = arguments.getString("room_image");
            String roomCapacity = arguments.getString("room_capacity");

            // Gán giá trị vào các view
            tvnameKS.setText(roomName);
            tvpriceKS.setText(roomPrice);
            ratingBar.setRating(roomRating);
            tvcapacityKS.setText(roomCapacity);

            // Hiển thị ảnh với Glide
            Glide.with(this)
                    .load(roomImageURL)
                    .into(roomImage);

            // Chuyển đổi giá phòng sang số để tính toán
            try {
                roomPricePerNight = Float.parseFloat(roomPrice.replace("$", "").trim());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                roomPricePerNight = 0; // Giá mặc định nếu không chuyển đổi được
            }
        }
        return view;
    }

    // Hiển thị BottomSheet để chọn khách
    private void showSelectGuestBottomSheet() {
        SelectGuestBottomSheet bottomSheet = new SelectGuestBottomSheet();
        bottomSheet.setOnGuestSelectedListener((adults, children, infants) -> {
            String summary = "Adults: " + adults + " | Children: " + children + " | Infants: " + infants;
            tvPerson.setText(summary);
        });
        bottomSheet.show(getChildFragmentManager(), "SelectGuestBottomSheet");
    }
    private int calculateNights(String dateRange) {
        try {
            // Kiểm tra dateRange có đúng định dạng không
            if (dateRange == null || !dateRange.contains(" - ")) {
                return 0; // Trả về 0 nếu định dạng không hợp lệ
            }

            // Giả sử dateRange có định dạng: "From: dd/MM/yyyy     To: dd/MM/yyyy"
            String[] dates = dateRange.replace("From: ", "")
                    .replace("To: ", "")
                    .split("\\s+-\\s+");
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            Date startDate = format.parse(dates[0]);
            Date endDate = format.parse(dates[1]);

            // Tính số ngày giữa hai ngày
            long diffInMillis = endDate.getTime() - startDate.getTime();
            return (int) TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // Trả về 0 nếu xảy ra lỗi
        }
    }

    private float discountAmount = 50; // Giảm giá mặc định

    private void updateTotalPrice() {
        TextView priceDetailsView = getView().findViewById(R.id.price_details);
        TextView discountView = getView().findViewById(R.id.discount);
        TextView totalPriceView = getView().findViewById(R.id.totalPrice);

        if (numberOfNights > 0 && roomPricePerNight > 0) {
            float totalPriceBeforeDiscount = roomPricePerNight * numberOfNights; // Giá trước giảm giá
            float finalPrice = totalPriceBeforeDiscount - discountAmount; // Giá sau giảm giá

            priceDetailsView.setText(String.format(Locale.getDefault(),
                    "$%.2f x %d nights = $%.2f", roomPricePerNight, numberOfNights, totalPriceBeforeDiscount));
            discountView.setText(String.format(Locale.getDefault(), "Discount: $%.2f", discountAmount));
            totalPriceView.setText(String.format(Locale.getDefault(), "$%.2f", finalPrice));
        } else {
            priceDetailsView.setText("$0.00 x 0 nights = $0.00");
            discountView.setText("Discount: $0.00");
            totalPriceView.setText("$0.00");
        }
    }





}
