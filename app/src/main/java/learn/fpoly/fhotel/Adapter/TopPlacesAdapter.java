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

import learn.fpoly.fhotel.Model.Room;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.activity.DetailsActivity;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class TopPlacesAdapter extends RecyclerView.Adapter<TopPlacesAdapter.TopPlacesViewHolder> {

    Context context;
    List<Room> topPlacesDataList;

    public TopPlacesAdapter(Context context, List<Room> topPlacesDataList) {
        this.context = context;
        this.topPlacesDataList = topPlacesDataList;
    }

    @NonNull
    @Override
    public TopPlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.top_places_row_item, parent, false);

        // here we create a recyclerview row item layout file
        return new TopPlacesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopPlacesViewHolder holder, int position) {

        holder.room_name.setText(topPlacesDataList.get(position).getName());
        holder.room_code.setText(topPlacesDataList.get(position).getRoom_code());
        holder.price_room.setText(formatCurrency(topPlacesDataList.get(position).getPrice()) + "/night");
        holder.ratingBar.setRating(topPlacesDataList.get(position).getRating());
        Glide.with(context)
                .load(topPlacesDataList.get(position).getImage()) // URL của hình ảnh
                .into(holder.room_image); // ImageView để hiển thị ảnh
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, DetailsActivity.class);
                // Truyền dữ liệu qua Intent
                i.putExtra("room_id", topPlacesDataList.get(position).getId());

                // Bắt đầu Activity DetailsActivity
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return topPlacesDataList.size();
    }

    public static final class TopPlacesViewHolder extends RecyclerView.ViewHolder {

        ImageView room_image;
        TextView room_name, room_code, price_room;
        RatingBar ratingBar;

        public TopPlacesViewHolder(@NonNull View itemView) {
            super(itemView);

            room_image = itemView.findViewById(R.id.room_image);
            room_name = itemView.findViewById(R.id.room_name);
            room_code = itemView.findViewById(R.id.room_code);
            price_room = itemView.findViewById(R.id.price_room);
            ratingBar = itemView.findViewById(R.id.ratingBarHome);
        }
    }
    private String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(amount) + "đ";
    }
}

