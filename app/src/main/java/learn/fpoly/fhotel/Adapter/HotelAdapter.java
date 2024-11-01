package learn.fpoly.fhotel.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import learn.fpoly.fhotel.Model.Hotel;
import learn.fpoly.fhotel.R;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.HotelViewHolder> {
    private Context context;
    private List<Hotel> list;

    public HotelAdapter(Context context, List<Hotel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hotel, parent, false);
        return new HotelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        Hotel hotel = list.get(position);
         holder.imghotel.setImageResource(hotel.getImgHotel());
        holder.txtName.setText(hotel.getNameHotel());
        holder.txtLocation.setText(hotel.getLocationHotel());
        holder.txtPrice.setText(String.valueOf(hotel.getPriceHotel()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class HotelViewHolder extends RecyclerView.ViewHolder {
            ImageView imghotel;
            TextView txtName, txtLocation, txtPrice, txtRate;
        public HotelViewHolder(@NonNull View itemView) {
            super(itemView);
            imghotel = itemView.findViewById(R.id.hotelImage);
            txtName = itemView.findViewById(R.id.hotelName);
            txtLocation = itemView.findViewById(R.id.hotelLocation);
            txtPrice = itemView.findViewById(R.id.hotelPrice);
        }
    }
}
