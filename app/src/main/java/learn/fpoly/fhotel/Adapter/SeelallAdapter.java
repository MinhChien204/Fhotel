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

import java.util.List;

import learn.fpoly.fhotel.Model.Room;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.activity.DetailsActivity;

public class SeelallAdapter  extends RecyclerView.Adapter<SeelallAdapter.SeeAllViewHolder>{
    Context context;
    List<Room> SeelallDataList;
    public SeelallAdapter(Context context, List<Room> SeelallDataList) {
        this.context = context;
        this.SeelallDataList = SeelallDataList;
    }
    @NonNull
    @Override
    public SeelallAdapter.SeeAllViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.seeall_row_item, parent, false);

        // here we create a recyclerview row item layout file
        return new SeeAllViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeelallAdapter.SeeAllViewHolder holder, int position) {
        Room room = SeelallDataList.get(position);
        holder.room_name.setText(room.getName());
        holder.room_code.setText(room.getRoom_code());
        holder.price_room.setText(String.valueOf(room.getPrice() + "vnd/night"));
        holder.ratingBar.setRating(room.getRating());
        Glide.with(context)
                .load(room.getImage()) // URL của hình ảnh
                .into(holder.room_image); // ImageView để hiển thị ảnh

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, DetailsActivity.class);
                // Truyền dữ liệu qua Intent
                i.putExtra("room_id", room.getId());

                // Bắt đầu Activity DetailsActivity
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return SeelallDataList.size();
    }

    public static final class SeeAllViewHolder extends RecyclerView.ViewHolder {

        ImageView room_image;
        TextView room_name, room_code, price_room;
        RatingBar ratingBar;

        public SeeAllViewHolder(@NonNull View itemView) {
            super(itemView);

            room_image = itemView.findViewById(R.id.room_image);
            room_name = itemView.findViewById(R.id.room_name);
            room_code = itemView.findViewById(R.id.room_code);
            price_room = itemView.findViewById(R.id.price_room);
            ratingBar = itemView.findViewById(R.id.ratingBarHome);
        }
    }
}
