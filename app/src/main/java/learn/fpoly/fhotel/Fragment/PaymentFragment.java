package learn.fpoly.fhotel.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.dialog.SelectDateBottomSheet;
import learn.fpoly.fhotel.dialog.SelectGuestBottomSheet;

public class PaymentFragment extends Fragment {
    TextView tvdate,tvPerson;
    ImageView btnback;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        View view = inflater.inflate(R.layout.fragment_payment, container, false);
        tvdate = view.findViewById(R.id.dates);
        tvPerson = view.findViewById(R.id.guests);
        btnback =view.findViewById(R.id.ivBackPayment);
        tvdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị bottom sheet dialog
                SelectDateBottomSheet bottomSheet = new SelectDateBottomSheet();
                bottomSheet.show(getParentFragmentManager(), "SelectDateBottomSheet");

            }
        });
        tvPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectGuestBottomSheet bottomSheet = new SelectGuestBottomSheet();
                bottomSheet.show(getParentFragmentManager(), "SelectGuestBottomSheet");
            }
        });
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().onBackPressed();
            }
        });
        return view;
    }
}
