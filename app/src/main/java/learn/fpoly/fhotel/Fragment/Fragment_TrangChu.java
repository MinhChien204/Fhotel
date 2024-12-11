package learn.fpoly.fhotel.Fragment;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


import learn.fpoly.fhotel.Adapter.RecentsAdapter;
import learn.fpoly.fhotel.Adapter.TopPlacesAdapter;
import learn.fpoly.fhotel.Adapter.TypeRoomAdapter;
import learn.fpoly.fhotel.Model.Room;
import learn.fpoly.fhotel.Model.TypeRoom;
import learn.fpoly.fhotel.Model.User;
import learn.fpoly.fhotel.activity.Home_User;
import learn.fpoly.fhotel.chatbot.ChatBotActivity;
import learn.fpoly.fhotel.response.Response;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.Retrofit.HttpRequest; // Import your Retrofit client
import retrofit2.Call;
import retrofit2.Callback;

import android.widget.TextView;


public class Fragment_TrangChu extends Fragment {

    private RecyclerView recentRecycler, topPlacesRecycler, typeroomRecyclerview;
    private RecentsAdapter recentsAdapter;
    private TopPlacesAdapter topPlacesAdapter;
    private TypeRoomAdapter typeRoomAdapter;
    private HttpRequest httpRequest;
    private FloatingActionButton floatingActionButton;
    private TextView txtSeeall;
    private ImageView imgUser;

    public Fragment_TrangChu() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__trang_chu, container, false);
//        txtSeeall = view.findViewById(R.id.txtSeeall);
        imgUser = view.findViewById(R.id.imageUser);
        // Khởi tạo ApiService
        httpRequest = new HttpRequest();
        // Khởi tạo RecyclerView
        recentRecycler = view.findViewById(R.id.recent_recycler);
        topPlacesRecycler = view.findViewById(R.id.top_places_recycler);
        typeroomRecyclerview = view.findViewById(R.id.rcv_typeroom);
        floatingActionButton = view.findViewById(R.id.fab_chatbot);

        // Gọi API để lấy dữ liệu
        fetchdata(view);
        fetchRecentsData(view);
        fetchTopRoom(view);

        // Lấy userId từ SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (userId != null && !userId.isEmpty()) {
            fetchimgUser(userId);
        } else {
            Toast.makeText(getContext(), "Invalid User ID", Toast.LENGTH_SHORT).show();
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ChatBotActivity.class);
                startActivity(intent);
            }
        });
//        txtSeeall.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), ChatBotActivity.class);
//                startActivity(intent);
//            }
//        });

        return view;
    }

    private void fetchdata(View view) {
        httpRequest.callAPI().getTypeRooms().enqueue(new Callback<Response<ArrayList<TypeRoom>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<TypeRoom>>> call, retrofit2.Response<Response<ArrayList<TypeRoom>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArrayList<TypeRoom> typeRooms = response.body().getData();
                    Log.d("API_RESPONSE", "Data: " + typeRooms.toString());
                    setTypeRoom(view, typeRooms);
                } else {
                    Log.e("API_ERROR", "Response failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Response<ArrayList<TypeRoom>>> call, Throwable t) {
                Log.e("API_FAILURE", "Error: " + t.getMessage());
            }
        });

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

    private void fetchTopRoom(View view) {
        httpRequest.callAPI().getTopRooms().enqueue(new Callback<Response<ArrayList<Room>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<Room>>> call, retrofit2.Response<Response<ArrayList<Room>>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    ArrayList<Room> topPlacesDataList = response.body().getData();
                    setTopPlacesRecycler(view, topPlacesDataList);
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

    private void fetchimgUser(String userId) {
        Call<Response<User>> call = httpRequest.callAPI().getuserbyid(userId);
        call.enqueue(new Callback<Response<User>>() {
            @Override
            public void onResponse(Call<Response<User>> call, retrofit2.Response<Response<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body().getData();
                    if (user != null) {
                        String avatarUrl = user.getAvatar();  // Thêm URL gốc vào đường dẫn ảnh
                        Glide.with(getContext())
                                .load(avatarUrl)  // Sử dụng URL đầy đủ
                                .circleCrop()
                                .into(imgUser);
                    }
                }
            }

            @Override
            public void onFailure(Call<Response<User>> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to fetch user: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setTypeRoom(View view, List<TypeRoom> typeRoomList) {
        typeroomRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        typeRoomAdapter = new TypeRoomAdapter(getContext(), typeRoomList);
        Log.d(TAG, "setTypeRoom: " + typeRoomList);
        typeroomRecyclerview.setAdapter(typeRoomAdapter);
        typeRoomAdapter.notifyDataSetChanged(); // Bổ sung dòng này
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
