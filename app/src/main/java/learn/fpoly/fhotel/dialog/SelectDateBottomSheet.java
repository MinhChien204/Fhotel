package learn.fpoly.fhotel.dialog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Calendar;

import learn.fpoly.fhotel.Model.OnDateSelectedListener;
import learn.fpoly.fhotel.R;

public class SelectDateBottomSheet extends BottomSheetDialogFragment {

    private CalendarView calendarView;
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

        calendarView = view.findViewById(R.id.calendar_view);
        btnSelectDate = view.findViewById(R.id.btn_select_date);

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            // Lưu ngày đã chọn
            String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            if (listener != null) {
                // Gửi ngày đã chọn về PaymentFragment qua listener
                listener.onDateSelected(selectedDate);
            }
        });

        btnSelectDate.setOnClickListener(v -> dismiss());

        return view;
    }
}

