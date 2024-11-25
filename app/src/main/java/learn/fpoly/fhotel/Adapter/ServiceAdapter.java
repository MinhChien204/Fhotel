package learn.fpoly.fhotel.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import learn.fpoly.fhotel.Model.Service;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.activity.DetailsActivity;
import learn.fpoly.fhotel.activity.ServiceDetail;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {
    private Context context;
    private List<Service> services;
    private List<Service> selectedServices = new ArrayList<>();
    private OnServiceSelectedListener onServiceSelectedListener;

    public ServiceAdapter(Context context, List<Service> services, OnServiceSelectedListener listener) {
        this.context = context;
        this.services = services;
        this.onServiceSelectedListener = listener;
    }

    public void setServices(List<Service> services) {
        this.services = services;
        notifyDataSetChanged();  // Notify data change after updating the service list
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service roomService = services.get(position);

        // Tải ảnh vào ImageView bằng Glide
        Glide.with(context)
                .load(roomService.getImage().get(0)) // Hiển thị ảnh đầu tiên
                .into(holder.serviceImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ServiceDetail.class);
                // Truyền dữ liệu qua Intent
                i.putExtra("ser_id", roomService.get_id());

                // Bắt đầu Activity DetailsActivity
                context.startActivity(i);
            }
        });


        // Nếu dịch vụ đã được chọn, đánh dấu checkbox là checked
        holder.checkBox.setChecked(selectedServices.contains(roomService));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedServices.add(roomService);  // Thêm dịch vụ vào danh sách đã chọn
            } else {
                selectedServices.remove(roomService);  // Xóa dịch vụ khỏi danh sách đã chọn
            }
            // Gọi lại listener để tính lại tổng giá
            onServiceSelectedListener.onServiceSelected(selectedServices);
        });
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public interface OnServiceSelectedListener {
        void onServiceSelected(List<Service> selectedServices);
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        ImageView serviceImage;
        CheckBox checkBox;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceImage = itemView.findViewById(R.id.ivServiceImage);
            checkBox = itemView.findViewById(R.id.checkBoxService);
        }
    }
}
