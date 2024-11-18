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

        // Gọi API để lấy dữ liệu
//        String roomId = getIntent().getStringExtra("room_id");
//        Log.d("dcm", "onCreate: " + roomId);

        // Kiểm tra nếu ID hợp lệ và gọi API
//        if (roomId != null && !roomId.isEmpty()) {
//            fetchRoomById(roomId);
//        } else {
//            Toast.makeText(getContext(), "Invalid Room ID", Toast.LENGTH_SHORT).show();
//        }

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

    // Gọi API để lấy thông tin phòng
    public void fetchRoomById(String roomId) {
        Call<Response<Room>> call = httpRequest.callAPI().getRoomById(roomId);
        call.enqueue(new Callback<Response<Room>>() {
            @Override
            public void onResponse(Call<Response<Room>> call, retrofit2.Response<Response<Room>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Response<Room> roomResponse = response.body();
                    if (roomResponse.getStatus() == 200) {
                        Room room = roomResponse.getData();

                        // Gán dữ liệu vào các View
                        tvnameKS.setText(room.getName());
                        tvpriceKS.setText("$" + room.getPrice());
                        tvcapacityKS.setText(room.getCapacity() + "m2");

//                        ratingBar.setRating(room.getRating());

                        // Tải ảnh với Glide
                        Glide.with(PaymentFragment.this)
                                .load(room.getImage())
                                .into(roomImage);
                    } else {
                        Toast.makeText(getContext(), "Room not found: " + roomResponse.getMessenger(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Room not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<Room>> call, Throwable t) {
                Toast.makeText(getContext(), "Room Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
