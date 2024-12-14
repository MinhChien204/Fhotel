package learn.fpoly.fhotel.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import learn.fpoly.fhotel.Adapter.SeelallAdapter;
import learn.fpoly.fhotel.Model.Room;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.Retrofit.HttpRequest;
import learn.fpoly.fhotel.response.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class seel_all extends AppCompatActivity {
    private RecyclerView recyclerViewRooms;
    private HttpRequest httpRequest;
    private SeelallAdapter seelallAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seel_all);

        recyclerViewRooms = findViewById(R.id.rclv_roms_sellall);
        httpRequest = new HttpRequest();

        fetchRecentsData();
    }

    private void fetchRecentsData() {
        httpRequest.callAPI().getRooms().enqueue(new Callback<Response<ArrayList<Room>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<Room>>> call, retrofit2.Response<Response<ArrayList<Room>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArrayList<Room> roomDataList = response.body().getData();
                    setRecyclerView(roomDataList);
                } else {
                    Toast.makeText(seel_all.this, "Failed to load recent rooms", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<ArrayList<Room>>> call, Throwable t) {
                Log.e("API_ERROR", "Error fetching rooms: " + t.getMessage(), t);
                Toast.makeText(seel_all.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setRecyclerView(List<Room> roomDataList) {
        recyclerViewRooms.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        seelallAdapter = new SeelallAdapter(this, roomDataList);
        recyclerViewRooms.setAdapter(seelallAdapter);
    }
}