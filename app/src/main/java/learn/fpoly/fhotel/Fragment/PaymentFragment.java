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
            // Tạo và hiển thị bottom sheet chọn ngày
            SelectDateBottomSheet bottomSheet = new SelectDateBottomSheet();
            bottomSheet.setOnDateSelectedListener(dateRange -> {
                // Cập nhật TextView với ngày đã chọn
                tvdate.setText(dateRange);
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

    // Hiển thị BottomSheet để chọn khách
    private void showSelectGuestBottomSheet() {
        SelectGuestBottomSheet bottomSheet = new SelectGuestBottomSheet();
        bottomSheet.setOnGuestSelectedListener((adults, children, infants) -> {
            String summary = "Adults: " + adults + " | Children: " + children + " | Infants: " + infants;
            tvPerson.setText(summary);
        });
        bottomSheet.show(getChildFragmentManager(), "SelectGuestBottomSheet");
    }

}
