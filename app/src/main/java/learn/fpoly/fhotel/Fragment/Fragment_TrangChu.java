package learn.fpoly.fhotel.Fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import learn.fpoly.fhotel.Adapter.RecentsAdapter;
import learn.fpoly.fhotel.Adapter.TopPlacesAdapter;
import learn.fpoly.fhotel.Model.RecentsData;
import learn.fpoly.fhotel.Model.TopPlacesData;
import learn.fpoly.fhotel.R;

public class Fragment_TrangChu extends Fragment {

    public Fragment_TrangChu() {
        // Required empty public constructor
    }

    RecyclerView recentRecycler, topPlacesRecycler;
    RecentsAdapter recentsAdapter;
    TopPlacesAdapter topPlacesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__trang_chu, container, false);

        List<RecentsData> recentsDataList = new ArrayList<>();
        recentsDataList.add(new RecentsData("AM Lake", "India", "From $200", R.drawable.bg));
        recentsDataList.add(new RecentsData("Nilgiri Hills", "India", "From $300", R.drawable.bg));
        recentsDataList.add(new RecentsData("AM Lake", "India", "From $200", R.drawable.bg));
        recentsDataList.add(new RecentsData("Nilgiri Hills", "India", "From $300", R.drawable.bg));
        recentsDataList.add(new RecentsData("AM Lake", "India", "From $200", R.drawable.bg));
        recentsDataList.add(new RecentsData("Nilgiri Hills", "India", "From $300", R.drawable.bg));

        setRecentRecycler(view, recentsDataList);

        List<TopPlacesData> topPlacesDataList = new ArrayList<>();
        topPlacesDataList.add(new TopPlacesData("Kasimir Hill", "India", "$200 - $500", R.drawable.bg));
        topPlacesDataList.add(new TopPlacesData("Kasimir Hill", "India", "$200 - $500", R.drawable.bg));
        topPlacesDataList.add(new TopPlacesData("Kasimir Hill", "India", "$200 - $500", R.drawable.bg));
        topPlacesDataList.add(new TopPlacesData("Kasimir Hill", "India", "$200 - $500", R.drawable.bg));
        topPlacesDataList.add(new TopPlacesData("Kasimir Hill", "India", "$200 - $500", R.drawable.bg));

        setTopPlacesRecycler(view, topPlacesDataList);

        return view;
    }

    private void setRecentRecycler(View view, List<RecentsData> recentsDataList) {
        recentRecycler = view.findViewById(R.id.recent_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        recentRecycler.setLayoutManager(layoutManager);
        recentsAdapter = new RecentsAdapter(getContext(), recentsDataList);
        recentRecycler.setAdapter(recentsAdapter);
    }

    private void setTopPlacesRecycler(View view, List<TopPlacesData> topPlacesDataList) {
        topPlacesRecycler = view.findViewById(R.id.top_places_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        topPlacesRecycler.setLayoutManager(layoutManager);
        topPlacesAdapter = new TopPlacesAdapter(getContext(), topPlacesDataList);
        topPlacesRecycler.setAdapter(topPlacesAdapter);
    }
}
