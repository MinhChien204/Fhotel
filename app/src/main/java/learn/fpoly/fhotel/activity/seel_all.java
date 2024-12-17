package learn.fpoly.fhotel.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
    private ImageView ivFilterrom;
    private List<Room> originalRoomList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seel_all);

        recyclerViewRooms = findViewById(R.id.rclv_roms_sellall);
        ivFilterrom = findViewById(R.id.ivFilterrom);

        ivFilterrom.setOnClickListener(v -> showFilterDialog());
        httpRequest = new HttpRequest();

        fetchRecentsData();
    }

    private void fetchRecentsData() {
        httpRequest.callAPI().getRooms().enqueue(new Callback<Response<ArrayList<Room>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<Room>>> call, retrofit2.Response<Response<ArrayList<Room>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArrayList<Room> roomDataList = response.body().getData();
                    originalRoomList.clear();
                    originalRoomList.addAll(roomDataList); // Lưu danh sách gốc
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

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lọc phòng");

        // Các tiêu chí lọc
        String[] filterOptions = {"Lọc theo giá", "Lọc theo sao", "Xem tất cả"};
        builder.setItems(filterOptions, (dialog, which) -> {
            switch (which) {
                case 0:
                    showPriceFilterDialog();
                    break;
                case 1:
                    showRatingFilterDialog();
                    break;
                case 2: // Xem tất cả
                    resetFilters();
                    break;
//                case 2:
//                    showTypeFilterDialog();
//                    break;
            }
        });

        builder.create().show();
    }
    private void showPriceFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lọc theo giá trị");

        View view = getLayoutInflater().inflate(R.layout.dialog_filter_price, null);
        builder.setView(view);

        EditText etMinPrice = view.findViewById(R.id.etMinPrice);
        EditText etMaxPrice = view.findViewById(R.id.etMaxPrice);

        builder.setPositiveButton("Lọc", (dialog, which) -> {
            String minPriceStr = etMinPrice.getText().toString().trim();
            String maxPriceStr = etMaxPrice.getText().toString().trim();

            if (!minPriceStr.isEmpty() && !maxPriceStr.isEmpty()) {
                int minPrice = Integer.parseInt(minPriceStr);
                int maxPrice = Integer.parseInt(maxPriceStr);
                filterRoomsByPrice(minPrice, maxPrice);
            } else {
                Toast.makeText(this, "Please enter valid prices", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.create().show();
    }

    private void filterRoomsByPrice(int minPrice, int maxPrice) {
        List<Room> filteredList = new ArrayList<>();
        for (Room room : originalRoomList) {
            if (room.getPrice() >= minPrice && room.getPrice() <= maxPrice) {
                filteredList.add(room);
            }
        }
        updateRoomList(filteredList);
    }

    private void showRatingFilterDialog() {
        String[] ratings = {"5 stars", "4 stars", "3 stars"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lọc theo rating");

        builder.setItems(ratings, (dialog, which) -> {
            int minRating = 5 - which; // 5 sao, 4 sao, 3 sao
            filterRoomsByRating(minRating);
        });

        builder.create().show();
    }

    private void filterRoomsByRating(int minRating) {
        List<Room> filteredList = new ArrayList<>();
        for (Room room : originalRoomList) {
            if (room.getRating() >= minRating) {
                filteredList.add(room);
            }
        }
        updateRoomList(filteredList);
    }
    private void updateRoomList(List<Room> filteredList) {
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No rooms match the filter criteria.", Toast.LENGTH_SHORT).show();
        }
        seelallAdapter.updateList(filteredList); // Cập nhật danh sách trong adapter
    }
    private void resetFilters() {
        updateRoomList(originalRoomList); // Hiển thị lại danh sách gốc
        Toast.makeText(this, "All filters cleared. Showing all rooms.", Toast.LENGTH_SHORT).show();
    }


}