package learn.fpoly.fhotel.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import learn.fpoly.fhotel.Model.Favourite;
import learn.fpoly.fhotel.Model.Room;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.activity.DetailsActivity;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder> {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private Context context;
    private List<Favourite> favoriteRooms;

    public FavouriteAdapter(Context context, List<Favourite> favoriteRooms) {
        this.context = context;
        this.favoriteRooms = favoriteRooms;
    }

    @Override
    public FavouriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favourite_room, parent, false);
        return new FavouriteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FavouriteViewHolder holder, int position) {
        Favourite favourite = favoriteRooms.get(position);

        // Gán tên phòng
        holder.tvRoomName.setText(favourite.getRoom().getName());
        double price = favourite.getRoom().getPrice();
        String formattedPrice = formatCurrency(price);
        holder.tvRoomPrice.setText(formattedPrice + "đ");
        holder.tvroom_code.setText(String.valueOf(favourite.getRoom().getRoom_code()));
        holder.tvratingBarHome.setRating(favourite.getRoom().getRating());

        // Gán ảnh phòng (sử dụng Glide)
        Glide.with(holder.itemView.getContext())
                .load(favourite.getRoom().getImage())
                .placeholder(R.drawable.ic_launcher_background) // Ảnh placeholder
                .into(holder.ivRoomImage);

        // Sự kiện khi nhấn vào item
        holder.itemView.setOnClickListener(v -> {
            String roomId = favourite.getRoom().getId();
            if (roomId != null && !roomId.isEmpty()) {
                // Chuyển sang màn hình chi tiết phòng
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("room_id", roomId);
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Thông tin phòng không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoriteRooms.size();
    }

    public class FavouriteViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomName, tvRoomPrice,tvroom_code;
        ImageView ivRoomImage;
        RatingBar tvratingBarHome;
        public FavouriteViewHolder(View itemView) {
            super(itemView);
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            tvRoomPrice = itemView.findViewById(R.id.tvRoomPrice);
            tvroom_code = itemView.findViewById(R.id.tvroom_code);
            tvratingBarHome = itemView.findViewById(R.id.tvratingBarHome);
            ivRoomImage = itemView.findViewById(R.id.ivRoomImage);
        }
    }

    // Hàm định dạng giá phòng
    private String formatCurrency(double amount) {
        // Định dạng số thành dạng 300.000
        DecimalFormat formatter = new DecimalFormat("#,###");
        String formatted = formatter.format(amount);
        return formatted.replace(',', '.');
    }

}
