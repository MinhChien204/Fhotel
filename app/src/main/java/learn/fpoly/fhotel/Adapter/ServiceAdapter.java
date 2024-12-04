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

import java.util.List;

import learn.fpoly.fhotel.Model.Service;
import learn.fpoly.fhotel.R;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<Service> serviceList;
    private Context context;

    public ServiceAdapter(Context context, List<Service> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
    }

    public void updateData(List<Service> newServices) {
        this.serviceList = newServices;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);

        holder.tvName.setText(service.getName());
        // Load the image using Glide
        Glide.with(context)
                .load(service.getImage()) // Replace with the method to get image URL
                .into(holder.serviceImage);
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public class ServiceViewHolder extends RecyclerView.ViewHolder {
        ImageView serviceImage;
        TextView tvName;

        public ServiceViewHolder(View itemView) {
            super(itemView);
            // Bind the ImageView from the item_service layout
            serviceImage = itemView.findViewById(R.id.ivServiceImage);
            tvName = itemView.findViewById(R.id.tvNameSer);
        }
    }
}
