package learn.fpoly.fhotel.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import learn.fpoly.fhotel.Model.Notification;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.activity.DetailsActivity;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private Context context;
    private List<Notification> notifications;

    public NotificationAdapter(Context context, List<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.tvmessage.setText(notification.getMessage());
        holder.tvCreationDate.setText(formatDate(notification.getCreatedAt()));
        // Customize based on type
        switch (notification.getType()) {
            case "password_change":
                holder.iconImageView.setImageResource(R.drawable.ic_password_change);
                break;
            case "booking_confirmed":
                holder.iconImageView.setImageResource(R.drawable.ic_booking_confirmed);
                break;
            case "booking_cancelled":
                holder.iconImageView.setImageResource(R.drawable.ic_booking_cancelled);
                break;
            case "voucher_received":
                holder.iconImageView.setImageResource(R.drawable.sale);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView tvmessage,tvCreationDate;
        ImageView iconImageView;

        public NotificationViewHolder(View itemView) {
            super(itemView);
            tvmessage = itemView.findViewById(R.id.tvmessage);
            tvCreationDate = itemView.findViewById(R.id.tvCreationDate);
            iconImageView = itemView.findViewById(R.id.iconImageView);
        }
    }
    private String formatDate(String isoDate) {
        try {

            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));


            SimpleDateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());


            Date date = isoFormat.parse(isoDate);
            return targetFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "Invalid date";
        }
    }
}
