package learn.fpoly.fhotel.Adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import learn.fpoly.fhotel.Model.Booking;
import learn.fpoly.fhotel.R;

public class UpcomingBookingAdapter extends RecyclerView.Adapter<UpcomingBookingAdapter.BookingViewHolder> {

    private List<Booking> bookings;

    public UpcomingBookingAdapter(List<Booking> bookings) {
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

        // Gán trạng thái đặt phòng
        holder.tvBookingStatus.setText("Status: " + booking.getStatus());

        // Gán các thông tin khác
        holder.tvStartDate.setText("Start: " + booking.getStartDate());
        holder.tvEndDate.setText("End: " + booking.getEndDate());
        holder.tvTotalPrice.setText("$" + booking.getTotalPrice());
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    public class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomName, tvStartDate, tvEndDate, tvTotalPrice, tvBookingStatus;
        ImageView ivRoomImage;

        public BookingViewHolder(View itemView) {
            super(itemView);
            ivRoomImage = itemView.findViewById(R.id.ivRoomImage);
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            tvBookingStatus = itemView.findViewById(R.id.tvBookingStatus);
            tvStartDate = itemView.findViewById(R.id.tvStartDate);
            tvEndDate = itemView.findViewById(R.id.tvEndDate);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
        }
    }
}

