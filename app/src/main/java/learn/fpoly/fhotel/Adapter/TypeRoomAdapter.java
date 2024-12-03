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

import java.util.ArrayList;
import java.util.List;

import learn.fpoly.fhotel.Model.TypeRoom;
import learn.fpoly.fhotel.R;

public class TypeRoomAdapter extends RecyclerView.Adapter<TypeRoomAdapter.HotelViewHolder> {
    private Context context;
    private List<TypeRoom> list;

    public TypeRoomAdapter(Context context, List<TypeRoom> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public TypeRoomAdapter.HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_typeroom,parent,false);
        return new HotelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TypeRoomAdapter.HotelViewHolder holder, int position) {
        TypeRoom typeRoom = list.get(position);
        Glide.with(context)
                .load(typeRoom.getImageRoom()) // Truyền URL vào đây
                .into(holder.imgRoom); // ImageView
        holder.name.setText(typeRoom.getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class HotelViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRoom;
        TextView name;
        public HotelViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRoom = itemView.findViewById(R.id.item_image);
            name = itemView.findViewById(R.id.item_title);
        }
    }
}
