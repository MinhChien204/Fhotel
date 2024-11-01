package learn.fpoly.fhotel.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import learn.fpoly.fhotel.Model.Room;
import learn.fpoly.fhotel.R;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {

    private Context context;
    private List<Room> roomList;

    public RoomAdapter(Context context, List<Room> roomList) {
        this.context = context;
        this.roomList = roomList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Room room = roomList.get(position);
        holder.nameTextView.setText(room.getName());
        holder.priceTextView.setText(String.valueOf(room.getPrice()));
        holder.ratingTextView.setText(String.valueOf(room.getRating()));
        holder.descriptionTextView.setText(room.getDescription());
        // Thêm mã để tải hình ảnh từ URL nếu cần
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, priceTextView, ratingTextView, descriptionTextView;
        ImageView roomImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.room_name);
            priceTextView = itemView.findViewById(R.id.room_price);
            ratingTextView = itemView.findViewById(R.id.room_rating);
            descriptionTextView = itemView.findViewById(R.id.room_description);
            roomImageView = itemView.findViewById(R.id.room_image); // Nếu bạn có ImageView
        }
    }
}
