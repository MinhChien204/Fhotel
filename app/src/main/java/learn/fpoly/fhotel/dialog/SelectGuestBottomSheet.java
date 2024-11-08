package learn.fpoly.fhotel.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import learn.fpoly.fhotel.R;

public class SelectGuestBottomSheet extends BottomSheetDialogFragment {
    private int adults = 1, children = 0, infants = 0;
    private TextView txtAdults, txtChildren, txtInfants;
    private Button btnAdultsMinus, btnAdultsPlus, btnChildrenMinus, btnChildrenPlus, btnInfantsMinus, btnInfantsPlus,btnadd;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheet_select_guest, container, false);

        txtAdults = view.findViewById(R.id.txt_adults_count);
        btnAdultsMinus = view.findViewById(R.id.btn_adults_minus);
        btnAdultsPlus = view.findViewById(R.id.btn_adults_plus);

        txtChildren = view.findViewById(R.id.txt_childrens_count);
        btnChildrenMinus = view.findViewById(R.id.btn_childrens_minus);
        btnChildrenPlus = view.findViewById(R.id.btn_childrens_plus);

        txtInfants = view.findViewById(R.id.txt_infants_count);
        btnInfantsMinus = view.findViewById(R.id.btn_infants_minus);
        btnInfantsPlus = view.findViewById(R.id.btn_infants_plus);

        btnadd =view.findViewById(R.id.btn_select_done);


        // Cập nhật số lượng
        txtAdults.setText(String.valueOf(adults));
        txtChildren.setText(String.valueOf(children));
        txtInfants.setText(String.valueOf(infants));


        // Xử lý các nút cộng/trừ
        btnAdultsMinus.setOnClickListener(v -> updateCount(txtAdults, --adults));
        btnAdultsPlus.setOnClickListener(v -> updateCount(txtAdults, ++adults));
        btnChildrenMinus.setOnClickListener(v -> updateCount(txtChildren, --children));
        btnChildrenPlus.setOnClickListener(v -> updateCount(txtChildren, ++children));
        btnInfantsMinus.setOnClickListener(v -> updateCount(txtInfants, --infants));
        btnInfantsPlus.setOnClickListener(v -> updateCount(txtInfants, ++infants));

        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }
    private void updateCount(TextView textView, int count) {
        if (count < 0) count = 0;
        textView.setText(String.valueOf(count));
    }
}
