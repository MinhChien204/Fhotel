package learn.fpoly.fhotel.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import learn.fpoly.fhotel.Model.Bill;
import learn.fpoly.fhotel.Model.Favourite;
import learn.fpoly.fhotel.Model.Room;
import learn.fpoly.fhotel.Model.UserVoucher;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.activity.DetailsActivity;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.BillViewHolder> {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private Context context;
    private List<Bill> bills;

    public BillAdapter(Context context, List<Bill> bills) {
        this.context = context;
        this.bills = bills;
    }

    @Override
    public BillViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_bill, parent, false);
        return new BillViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BillViewHolder holder, int position) {
        Bill bill = bills.get(position);
        String userPhone = bill.getUser().getPhonenumber();
        String userAddress = bill.getUser().getAddress();
        String userEmail = bill.getUser().getEmail();
        // Gán tên phòng
        holder.tvUserName.setText("Tên người dùng: " + bill.getUser().getName());
        holder.tvUserPhone.setText("Số điện thoại: " + (userPhone != null && !userPhone.isEmpty() ? userPhone : "Chưa có số điện thoại"));
        holder.tvUserAddress.setText("Địa chỉ: " + (userAddress != null && !userAddress.isEmpty() ? userAddress : "Chưa có địa chỉ"));
        holder.tvUserEmail.setText("Email: " + (userEmail != null && !userEmail.isEmpty() ? userEmail : "Chưa có email"));

        holder.tvRoomName.setText("Tên phòng: " + bill.getRoom().getName());
        holder.tvRoomPrice.setText("Giá phòng: " + formatCurrency(bill.getRoom().getPrice()));
        holder.tvRoomCode.setText("Mã phòng: " + bill.getRoom().getRoom_code());
        holder.tvStartDate.setText("Ngày bắt đầu: " + bill.getStartDate());
        holder.tvEndDate.setText("Ngày kết thúc: " + bill.getEndDate());
        holder.tvTotalPrice.setText("Tổng giá: " + formatCurrency(bill.getTotalPrice()));
        if ("paid".equalsIgnoreCase(bill.getPaymentStatus())) {
            holder.tvPaymentStatus.setText("Trạng thái: Đã thanh toán");
            holder.tvPaymentStatus.setTextColor(Color.GREEN);
        } else if ("unpaid".equalsIgnoreCase(bill.getPaymentStatus())) {
            holder.tvPaymentStatus.setText("Trạng thái: Chưa thanh toán");
            holder.tvPaymentStatus.setTextColor(Color.RED);
        } else {
            holder.tvPaymentStatus.setText("Trạng thái thanh toán: Không xác định");
            holder.tvPaymentStatus.setTextColor(Color.GRAY);
        }
    }

    @Override
    public int getItemCount() {
        return bills.size();
    }

    public class BillViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvUserPhone, tvUserAddress, tvUserEmail, tvRoomName, tvRoomPrice, tvRoomCode, tvStartDate, tvEndDate, tvTotalPrice, tvPaymentStatus;

        public BillViewHolder(View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserPhone = itemView.findViewById(R.id.tvUserPhone);
            tvUserAddress = itemView.findViewById(R.id.tvUserAddress);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            tvRoomPrice = itemView.findViewById(R.id.tvRoomPrice);
            tvRoomCode = itemView.findViewById(R.id.tvRoomCode);
            tvStartDate = itemView.findViewById(R.id.tvStartDate);
            tvEndDate = itemView.findViewById(R.id.tvEndDate);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvPaymentStatus = itemView.findViewById(R.id.tvPaymentStatus);
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

    private String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(amount) + "đ";
    }
}
