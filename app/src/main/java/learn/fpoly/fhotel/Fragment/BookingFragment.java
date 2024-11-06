package learn.fpoly.fhotel.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import learn.fpoly.fhotel.Adapter.ViewPagerBookingAdapter;
import learn.fpoly.fhotel.R;


public class BookingFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_booking, container, false);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager viewPager = view.findViewById(R.id.viewpagerbooking);

        // Set up the ViewPager with the adapter
        ViewPagerBookingAdapter adapter = new ViewPagerBookingAdapter(getChildFragmentManager());
        adapter.addFragment(new UpcomingBookingFragment(), "Upcoming");
        adapter.addFragment(new PastBookingFragment(), "Past");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}