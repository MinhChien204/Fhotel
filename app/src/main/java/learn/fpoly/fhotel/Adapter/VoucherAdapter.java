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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import learn.fpoly.fhotel.Model.Favourite;
import learn.fpoly.fhotel.Model.Room;
import learn.fpoly.fhotel.Model.UserVoucher;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.activity.DetailsActivity;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder> {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private Context context;
    private List<UserVoucher> vouchers;

    public VoucherAdapter(Context context, List<UserVoucher> vouchers) {
        this.context = context;
        this.vouchers = vouchers;
    }

    @Override
    public VoucherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_voucher_user, parent, false);
        return new VoucherViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(VoucherViewHolder holder, int position) {
        UserVoucher voucher = vouchers.get(position);

        // Gán tên phòng
        holder.tvcode.setText("Mã giảm giá: "+voucher.getVoucher().getCode());

        // Gán giá phòng
        holder.tvdiscount.setText("Giảm giá: "+voucher.getVoucher().getDiscount()+"%");

        holder.tvexpirationDate.setText("Hạn sử dụng: "+formatDate(voucher.getVoucher().getExpirationDate()));


    }

    @Override
    public int getItemCount() {
        return vouchers.size();
    }

    public class VoucherViewHolder extends RecyclerView.ViewHolder {
        TextView tvcode, tvdiscount,tvexpirationDate,tvisActive;

        public VoucherViewHolder(View itemView) {
            super(itemView);
            tvcode = itemView.findViewById(R.id.tvcode);
            tvdiscount = itemView.findViewById(R.id.tvdiscount);
            tvexpirationDate = itemView.findViewById(R.id.tvexpirationDate);
            tvisActive = itemView.findViewById(R.id.tvisActive);
        }
    }
    public void sortByCreatedAtNewestFirst() {
        Collections.sort(vouchers, (b1, b2) -> {
            try {
                Date date1 = dateFormat.parse(b1.getCreatedAt());
                Date date2 = dateFormat.parse(b2.getCreatedAt());
                return date2.compareTo(date1);
            } catch (Exception e) {
                return 0;
            }
        });
        notifyDataSetChanged();
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
