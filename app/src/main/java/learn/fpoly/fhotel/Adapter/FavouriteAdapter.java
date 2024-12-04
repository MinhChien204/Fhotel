package learn.fpoly.fhotel.Adapter;

import android.content.Context;
import android.content.Intent;
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

        // Gán giá phòng
        holder.tvRoomPrice.setText(String.valueOf(favourite.getRoom().getPrice()));

        // Gán ảnh phòng (sử dụng Glide)
        Glide.with(holder.itemView.getContext())
                .load("http://10.0.2.2:3000/" + favourite.getRoom().getImage())
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
        TextView tvRoomName, tvRoomPrice;
        ImageView ivRoomImage;

        public FavouriteViewHolder(View itemView) {
            super(itemView);
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            tvRoomPrice = itemView.findViewById(R.id.tvRoomPrice);
            ivRoomImage = itemView.findViewById(R.id.ivRoomImage);
        }
    }

    public void sortByCreatedAtNewestFirst() {
        Collections.sort(favoriteRooms, (b1, b2) -> {
            try {
                Date date1 = dateFormat.parse(b1.getCreatedAt());
                Date date2 = dateFormat.parse(b2.getCreatedAt());
                return date2.compareTo(date1);
            } catch (Exception e) {
                return 0;
            }
        });
        notifyDataSetChanged();
    }

}
