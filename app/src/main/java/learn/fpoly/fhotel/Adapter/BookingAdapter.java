package learn.fpoly.fhotel.Adapter;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import learn.fpoly.fhotel.Model.Booking;
import learn.fpoly.fhotel.Model.Notification;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.Retrofit.HttpRequest;
import learn.fpoly.fhotel.activity.DetailsActivity;
import learn.fpoly.fhotel.response.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    private SimpleDateFormat displayFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    private List<Booking> bookings;

    private Context context;

    public BookingAdapter(Context context, List<Booking> bookings) {
        this.context = context;
        this.bookings = bookings;
    }

    @Override
    public BookingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_upcoming_booking, parent, false);
        return new BookingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BookingViewHolder holder, int position) {
        Booking booking = bookings.get(position);

        // Gán tên phòng
        if (booking.getRoom() != null) {
            holder.tvRoomName.setText(booking.getRoom().getName());

            // Gán ảnh phòng (dùng thư viện Picasso hoặc Glide để tải ảnh)
            Glide.with(holder.itemView.getContext())
                    .load(booking.getRoom().getImage())
                    .placeholder(R.drawable.ic_launcher_background)  // Ảnh mặc định nếu tải thất bại
                    .into(holder.ivRoomImage);
        }

        try {
            // Định nghĩa định dạng ISO 8601
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Đảm bảo khớp với múi giờ ISO

            // Phân tích chuỗi ngày ISO 8601
            Date createdAtDate = isoFormat.parse(booking.getCreatedAt());

            // Chuyển đổi sang định dạng dd-MM-yyyy
            String formattedDate = displayFormat.format(createdAtDate);

            holder.tvCreatedAt.setText("Ngày đặt: " + formattedDate);
        } catch (Exception e) {
            e.printStackTrace();
            holder.tvCreatedAt.setText("Ngày đặt: Không xác định");
        }


        String status = booking.getStatus().trim();
        holder.tvBookingStatus.setText("Trạng thái: " + status);

        // Đổi màu dựa trên trạng thái
        switch (status.toLowerCase(Locale.ROOT)) {
            case "pending":
                holder.tvBookingStatus.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark)); // Màu vàng
                break;
            case "confirmed":
                holder.tvBookingStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark)); // Màu xanh lá
                break;
            case "cancelled":
                holder.tvBookingStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark)); // Màu đỏ
                break;
            default:
                holder.tvBookingStatus.setTextColor(context.getResources().getColor(android.R.color.darker_gray)); // Màu xám
                break;
        }

        // Gán các thông tin khác
        holder.tvStartDate.setText("Ngày bắt đầu: " + booking.getStartDate());
        holder.tvEndDate.setText("Ngày kết thúc: " + booking.getEndDate());
        holder.tvTotalPrice.setText(booking.getTotalPrice()+"đ");

        // Sự kiện khi nhấn vào item (chuyển đến màn chi tiết phòng)
        holder.itemView.setOnClickListener(v -> {
            // Lấy roomId từ booking
            String roomId = booking.getRoom().getId(); // Giả sử Booking có thông tin về phòng

            if (roomId != null && !roomId.isEmpty()) {
                // Chuyển sang màn hình chi tiết phòng
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("room_id", roomId);  // Truyền room_id qua Intent
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Thông tin phòng không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        // Sự kiện khi nhấn dài (có thể là để hủy booking)
        holder.itemView.setOnLongClickListener(v -> {
            String bookingId = booking.getId();

            if (bookingId == null || bookingId.isEmpty()) {
                Toast.makeText(v.getContext(), "ID booking không hợp lệ", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (!status.equals("pending") && !status.equals("confirmed") && !status.equals("cancelled")) {
                Toast.makeText(v.getContext(), "Trạng thái không hợp lệ", Toast.LENGTH_SHORT).show();
                return false;
            }

            // Hiển thị Dialog để xác nhận
            if (status.equals("pending")) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Xác nhận hủy đặt phòng")
                        .setMessage("Bạn có chắc chắn muốn hủy đặt phòng?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            String cancel = "cancelled";
                            updateBookingStatus(bookingId, cancel);  // Gọi hàm cập nhật trạng thái
                        })
                        .setNegativeButton("No", null)
                        .show();
            } else {
                Toast.makeText(v.getContext(), "Chỉ có thể hủy booking đang xử lý (pending)", Toast.LENGTH_SHORT).show();
            }

            return true;  // Trả về true để không gọi thêm hành động nhấn bình thường
        });
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    public class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomName, tvStartDate, tvEndDate, tvTotalPrice, tvBookingStatus, tvCreatedAt;
        ImageView ivRoomImage;

        public BookingViewHolder(View itemView) {
            super(itemView);
            ivRoomImage = itemView.findViewById(R.id.ivRoomImage);
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            tvBookingStatus = itemView.findViewById(R.id.tvBookingStatus);
            tvStartDate = itemView.findViewById(R.id.tvStartDate);
            tvEndDate = itemView.findViewById(R.id.tvEndDate);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
        }
    }

    public void updateList(List<Booking> newBookings) {
        this.bookings = newBookings;
        notifyDataSetChanged();
    }

    public List<Booking> getBookings() {
        return bookings;
    }



    private void updateBookingStatus(String bookingId, String newStatus) {
        HttpRequest httpRequest = new HttpRequest();
        Call<Response<Booking>> call = httpRequest.callAPI().updateBookingStatus(bookingId, new Booking(newStatus));

        call.enqueue(new Callback<Response<Booking>>() {
            @Override
            public void onResponse(Call<Response<Booking>> call, retrofit2.Response<Response<Booking>> response) {
                if (response.isSuccessful()) {
                    // Kiểm tra và cập nhật thông tin của booking chỉ cần thiết
                    for (int i = 0; i < bookings.size(); i++) {
                        Booking currentBooking = bookings.get(i);
                        if (currentBooking.getId().equals(response.body().getData().getId())) {
                            // Cập nhật chỉ trạng thái của booking, giữ nguyên các dữ liệu khác
                            currentBooking.setStatus(response.body().getData().getStatus());
                            // Nếu cần, có thể cập nhật thêm các thông tin khác như ngày bắt đầu, ngày kết thúc...
                            notifyItemChanged(i);  // Cập nhật riêng phần tử này trong RecyclerView
                            break;
                        }
                    }
                    createCancelNotification(bookingId);
                    Toast.makeText(context, "Trạng thái đã được cập nhật!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Không thể cập nhật trạng thái", Toast.LENGTH_SHORT).show();
                    Log.d("BookingStatus", "Response: " + response.body());
                }
            }

            @Override
            public void onFailure(Call<Response<Booking>> call, Throwable t) {
                Log.d("stbk", "onFailure: " + t);
                Toast.makeText(context, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void createCancelNotification(String bookingId) {
        // Gọi API lấy chi tiết booking
        HttpRequest httpRequest = new HttpRequest();
        Call<Response<Booking>> bookingCall = httpRequest.callAPI().getBookingById(bookingId); // API giả định

        bookingCall.enqueue(new Callback<Response<Booking>>() {
            @Override
            public void onResponse(Call<Response<Booking>> call, retrofit2.Response<Response<Booking>> response) {
                if (response.isSuccessful()) {
                    Booking booking = response.body().getData();
                    if (booking != null && booking.getRoom() != null) {
                        String roomName = booking.getRoom().getName(); // Lấy tên phòng từ booking
                        Log.d("rname", "onResponse: "+roomName);
                        // Sau khi có thông tin phòng, tạo thông báo
                        String message = "Your booking for room " + roomName;
                        sendNotification(booking.getUserId(), message, "booking_cancelled");
                    } else {
                        Log.e("Notification", "Room information not found for booking ID: " + bookingId);
                    }
                } else {
                    Log.e("Notification", "Failed to fetch booking details for ID: " + bookingId);
                }
            }

            @Override
            public void onFailure(Call<Response<Booking>> call, Throwable t) {
                Log.e("Notification", "Error: " + t.getMessage());
            }
        });
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

