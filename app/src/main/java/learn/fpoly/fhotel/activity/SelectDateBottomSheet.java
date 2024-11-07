package learn.fpoly.fhotel.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import learn.fpoly.fhotel.R;

public class SelectDateBottomSheet extends BottomSheetDialogFragment {

    private CalendarView calendarView;
    private Calendar currentCalendar;
    Button btnSelectGuest;

    @SuppressLint("WrongViewCast")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheet_select_date, container, false);

        calendarView = view.findViewById(R.id.calendar_view);
        btnSelectGuest =view.findViewById(R.id.btn_select_guest);
        currentCalendar = Calendar.getInstance();


        btnSelectGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Handle date selection
                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                // Do something with the selected date, e.g., display it or store it
            }
        });




        return view;
    }

    private void changeMonth(int delta) {
        currentCalendar.add(Calendar.MONTH, delta);
        calendarView.setDate(currentCalendar.getTimeInMillis());
    }


}
