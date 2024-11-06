package learn.fpoly.fhotel.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.activity.Home_User;
import learn.fpoly.fhotel.activity.MainActivity;


public class TkhoanFragment extends Fragment {

    LinearLayout editProfile, editPassword, payment, booking, privacy, terms;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tkhoan, container, false);



        editProfile = view.findViewById(R.id.editProfile);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new EditProfileFragment());
            }
        });
        return view;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout_u, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


}