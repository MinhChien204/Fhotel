package learn.fpoly.fhotel.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import learn.fpoly.fhotel.activity.DetailsActivity;
import learn.fpoly.fhotel.Model.Room;
import learn.fpoly.fhotel.R;

import java.util.List;

public class RoomSearchAdapter extends RecyclerView.Adapter<RoomSearchAdapter.RoomSearchViewHolder> {
    private List<Room> rooms;
    private Context context;

    public RoomSearchAdapter(Context context, List<Room> rooms) {
        this.context = context;
        this.rooms = rooms;
    }

    @NonNull
    @Override
    public RoomSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.room_item, parent, false);
        return new RoomSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomSearchViewHolder holder, int position) {
        Room room = rooms.get(position);
        holder.roomName.setText(room.getName());
        Glide.with(context).load(room.getImage()).into(holder.roomImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, DetailsActivity.class);
                i.putExtra("room_id", room.getId());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public void updateRooms(List<Room> newRooms) {
        this.rooms = newRooms;
        notifyDataSetChanged();
    }

    public static class RoomSearchViewHolder extends RecyclerView.ViewHolder {
        TextView roomName;
        ImageView roomImage;

        public RoomSearchViewHolder(@NonNull View itemView) {
            super(itemView);
            roomName = itemView.findViewById(R.id.roomName);
            roomImage = itemView.findViewById(R.id.roomImage);
        }
    }
}
