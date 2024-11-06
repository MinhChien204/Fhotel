package learn.fpoly.fhotel.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import learn.fpoly.fhotel.Model.RoomService;
import learn.fpoly.fhotel.R;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<RoomService> serviceList;
    private Context context;

    // Constructor to initialize the context and service list
    public ServiceAdapter(Context context, List<RoomService> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item_service layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        // Get the RoomService object at the given position
        RoomService service = serviceList.get(position);

        // Load the image using Glide
        Glide.with(context)
                .load(service.getImage()) // Replace with the method to get image URL
                .into(holder.serviceImage);
    }

    @Override
    public int getItemCount() {
        // Return the size of the service list
        return serviceList.size();
    }

    // Method to update the list of services
    public void updateData(List<RoomService> newServices) {
        this.serviceList = newServices;
        notifyDataSetChanged();  // Notify RecyclerView that the data has changed
    }

    // ViewHolder class to hold the references to the image view for each item
    public class ServiceViewHolder extends RecyclerView.ViewHolder {
        ImageView serviceImage;

        public ServiceViewHolder(View itemView) {
            super(itemView);
            // Bind the ImageView from the item_service layout
            serviceImage = itemView.findViewById(R.id.ivServiceImage);
        }
    }
}
