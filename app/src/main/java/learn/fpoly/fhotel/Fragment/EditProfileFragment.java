package learn.fpoly.fhotel.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.activity.Home_User;

public class EditProfileFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        // Kiểm tra nếu activity là Home_User thì ẩn BottomNavigationView
        if (getActivity() instanceof Home_User) {
            ((Home_User) getActivity()).bottomNavigationView.setVisibility(View.GONE);
        }
        return view;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Hiện lại BottomNavigationView khi thoát TkhoanFragment
        if (getActivity() instanceof Home_User) {
            ((Home_User) getActivity()).bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }
}