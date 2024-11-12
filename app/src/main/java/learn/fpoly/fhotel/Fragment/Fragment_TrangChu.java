package learn.fpoly.fhotel.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


import learn.fpoly.fhotel.Adapter.RecentsAdapter;
import learn.fpoly.fhotel.Adapter.TopPlacesAdapter;
import learn.fpoly.fhotel.Model.Room;
import learn.fpoly.fhotel.activity.Home_User;
import learn.fpoly.fhotel.chatbot.ChatBotActivity;
import learn.fpoly.fhotel.response.Response;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.Retrofit.HttpRequest; // Import your Retrofit client
import retrofit2.Call;
import retrofit2.Callback;


public class Fragment_TrangChu extends Fragment {

    private RecyclerView recentRecycler, topPlacesRecycler;
    private RecentsAdapter recentsAdapter;
    private TopPlacesAdapter topPlacesAdapter;
    private HttpRequest httpRequest;
    private FloatingActionButton floatingActionButton;

    public Fragment_TrangChu() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__trang_chu, container, false);

        // Khởi tạo ApiService
        httpRequest = new HttpRequest();

        // Khởi tạo RecyclerView
        recentRecycler = view.findViewById(R.id.recent_recycler);
        topPlacesRecycler = view.findViewById(R.id.top_places_recycler);
        floatingActionButton =view.findViewById(R.id.fab_chatbot);

        // Gọi API để lấy dữ liệu
        fetchRecentsData(view);
        fetchTopPlacesData(view);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ChatBotActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void fetchRecentsData(View view) {
        httpRequest.callAPI().getRooms().enqueue(new Callback<Response<ArrayList<Room>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<Room>>> call, retrofit2.Response<Response<ArrayList<Room>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArrayList<Room> recentsDataList = response.body().getData();
                    setRecentRecycler(view, recentsDataList);
                } else {
                    Toast.makeText(getContext(), "Failed to load recent rooms", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<ArrayList<Room>>> call, Throwable t) {
                Log.e("API_ERROR", "Error fetching top places: " + t.getMessage(), t);
                Toast.makeText(getContext(), "E: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchTopPlacesData(View view) {
        httpRequest.callAPI().getRooms().enqueue(new Callback<Response<ArrayList<Room>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<Room>>> call, retrofit2.Response<Response<ArrayList<Room>>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    ArrayList<Room> topPlacesDataList = response.body().getData();
                    setTopPlacesRecycler(view, topPlacesDataList);
                    Toast.makeText(getContext(),"Get data success",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Failed to load top places", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<ArrayList<Room>>> call, Throwable t) {

                Log.e("API_ERROR", "Error fetching top places", t);
                Toast.makeText(getContext(), "Error fetching top places", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRecentRecycler(View view, List<Room> recentsDataList) {
        recentRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        recentsAdapter = new RecentsAdapter(getContext(), recentsDataList);
        recentRecycler.setAdapter(recentsAdapter);
    }

    private void setTopPlacesRecycler(View view, List<Room> topPlacesDataList) {
        topPlacesRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        topPlacesAdapter = new TopPlacesAdapter(getContext(), topPlacesDataList);
        topPlacesRecycler.setAdapter(topPlacesAdapter);
    }
}
