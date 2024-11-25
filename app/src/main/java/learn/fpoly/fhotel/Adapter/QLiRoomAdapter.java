package learn.fpoly.fhotel.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import learn.fpoly.fhotel.Model.Room;
import learn.fpoly.fhotel.R;

import java.util.List;

public class QLiRoomAdapter extends RecyclerView.Adapter<QLiRoomAdapter.QLiRoomViewHolder> {

    Context context;
    List<Room> list;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    public QLiRoomAdapter(Context context, List<Room> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClickListener(QLiRoomAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public QLiRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_qlroom, parent, false);

        // here we create a recyclerview row item layout file
        return new QLiRoomViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull QLiRoomViewHolder holder, int position) {
        Room room = list.get(position);
        holder.room_name.setText("Name: " + room.getName());
        holder.room_code.setText("Room code: " + room.getRoom_code());
        holder.des_room.setText("Description: " + room.getDescription());

        holder.price_room.setText("Price: " + String.valueOf(room.getPrice()));
        holder.cap_room.setText("Capacity: " + String.valueOf(room.getCapacity()));
        holder.rate_room.setText("Rate: " + String.valueOf(room.getRating()));
        Glide.with(context)
                .load(list.get(position).getImage().get(0))
                .into(holder.room_image); // ImageView để hiển thị ảnh


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static final class QLiRoomViewHolder extends RecyclerView.ViewHolder {

        ImageView room_image, imgDeleteRoom;
        TextView room_name, room_code, price_room, cap_room, des_room, rate_room;

        public QLiRoomViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            room_image = itemView.findViewById(R.id.room_image);
            room_name = itemView.findViewById(R.id.txtNameRoom);
            room_code = itemView.findViewById(R.id.txtRoomcode);
            price_room = itemView.findViewById(R.id.txtPriceRoom);
            cap_room = itemView.findViewById(R.id.txtCapacityRoom);
            des_room = itemView.findViewById(R.id.txtDescriptionRoom);
            rate_room = itemView.findViewById(R.id.txtRatingRoom);
            imgDeleteRoom = itemView.findViewById(R.id.imgDeleteRoom);
            imgDeleteRoom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position); // Gọi phương thức onDeleteClick của listener
                        }
                    }
                }
            });
        }
    }
}
