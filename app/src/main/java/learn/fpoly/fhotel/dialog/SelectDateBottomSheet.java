package learn.fpoly.fhotel.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import learn.fpoly.fhotel.Model.OnDateSelectedListener;
import learn.fpoly.fhotel.R;

public class SelectDateBottomSheet extends BottomSheetDialogFragment {

    private Button btnSelectDate;
    private OnDateSelectedListener listener;

    // Đặt listener
    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheet_select_date, container, false);

        btnSelectDate = view.findViewById(R.id.btn_select_date);

        btnSelectDate.setOnClickListener(v -> openDateRangePicker());

        return view;
    }

    private void openDateRangePicker() {
        // Giới hạn ngày: chỉ cho phép chọn từ hôm nay trở đi
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now());

        // Xây dựng MaterialDatePicker
        MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> builder =
                MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText("Select a date range")
                        .setCalendarConstraints(constraintsBuilder.build());

        MaterialDatePicker<androidx.core.util.Pair<Long, Long>> datePicker = builder.build();

        // Hiển thị DatePicker
        datePicker.show(getParentFragmentManager(), "DateRangePicker");

        // Lắng nghe khi người dùng nhấn "OK"
        datePicker.addOnPositiveButtonClickListener(selection -> {
            // Lấy ngày bắt đầu và ngày kết thúc
            Long startMillis = selection.first;
            Long endMillis = selection.second;

            // Định dạng ngày thành chuỗi
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String startDate = dateFormat.format(new Date(startMillis));
            String endDate = dateFormat.format(new Date(endMillis));

            // Gửi giá trị đã chọn qua listener
            if (listener != null) {
                listener.onDateSelected("From: " + startDate + "     To: " + endDate);
            }
        });
    }

}
