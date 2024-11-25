package learn.fpoly.fhotel.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import learn.fpoly.fhotel.Model.Service;
import learn.fpoly.fhotel.R;

public class QLiServiceAdapter extends RecyclerView.Adapter<QLiServiceAdapter.QLiServiceViewHolder> {
    private Context context;
    private List<Service> serviceList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    public QLiServiceAdapter(Context context, List<Service> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
    }

    // Set listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<Service> newServices) {
        this.serviceList = newServices;
        notifyDataSetChanged(); // Cập nhật RecyclerView sau khi thay đổi dữ liệu
    }

    @NonNull
    @Override
    public QLiServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_qliservice, parent, false);
        return new QLiServiceViewHolder(view, listener); // Truyền listener vào ViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull QLiServiceViewHolder holder, int position) {
        Service roomService = serviceList.get(position);

        // Tải ảnh vào ImageView bằng Glide
        Glide.with(context)
                .load(roomService.getImage().get(0)) // Hiển thị ảnh đầu tiên
                .into(holder.serviceImage);

        // Thiết lập dữ liệu
        holder.txtNameSer.setText(roomService.getName());
        holder.txtPriceSer.setText(String.valueOf(roomService.getPrice()));
        holder.txtDescriptionSer.setText(roomService.getDescription());

    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public static class QLiServiceViewHolder extends RecyclerView.ViewHolder {
        ImageView serviceImage, imgDelete;
        TextView txtNameSer, txtPriceSer, txtDescriptionSer;

        public QLiServiceViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            serviceImage = itemView.findViewById(R.id.service_image);
            txtNameSer = itemView.findViewById(R.id.txtNameSer);
            txtPriceSer = itemView.findViewById(R.id.txtPriceSer);
            txtDescriptionSer = itemView.findViewById(R.id.txtDescriptionSer);
            imgDelete = itemView.findViewById(R.id.imgDeleteSer);

            imgDelete.setOnClickListener(new View.OnClickListener() {
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

