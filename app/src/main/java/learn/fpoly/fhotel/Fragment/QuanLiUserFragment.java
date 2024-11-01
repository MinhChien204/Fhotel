package learn.fpoly.fhotel.Fragment;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import learn.fpoly.fhotel.Adapter.UserAdapter;
import learn.fpoly.fhotel.Model.User;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.Retrofit.ApiService;
import learn.fpoly.fhotel.Retrofit.Config;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuanLiUserFragment extends Fragment {
    ApiService apiService;
    UserAdapter adapter;
    RecyclerView rcv;
    List<User> list;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quan_li_user, container, false);
        rcv = view.findViewById(R.id.rcv_listUser);

        // Thiết lập LayoutManager
        rcv.setLayoutManager(new LinearLayoutManager(getContext()));

        // Cấu hình Retrofit với ApiService
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.DOMAIN) // Sử dụng baseUrl từ ApiService
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        // Gọi loadData() để tải dữ liệu
        loadData();

        return view;
    }

    void loadData() {
        Call<List<User>> call = apiService.getUsers();

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.isSuccessful() && response.body() != null){
                    list = response.body();
                    Log.d("listsize", "Number of users: " + list.size()); // Kiểm tra số lượng user nhận được từ API

                    // Thiết lập adapter và RecyclerView
                    adapter = new UserAdapter(getContext(), list);
                    adapter.setOnItemClickListener(new UserAdapter.OnItemClickListener(){
                        @Override
                        public void onDeleteClick(int position) {
                            deleteAddress(position);
                        }
                    });

                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
                    rcv.setLayoutManager(gridLayoutManager);
                    rcv.setAdapter(adapter);
                    adapter.notifyDataSetChanged(); // Thông báo dữ liệu đã thay đổi
                } else {
                    Log.e(TAG, "Failed to fetch users. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable throwable) {
                Log.e(TAG, "Error fetching users: " + throwable.getMessage());
            }
        });
    }

    private void deleteAddress(int position) {
        String userId = list.get(position).get_id();
        Log.d(TAG, "Deleting address with ID: " + userId);
        Call<Void> call = apiService.deleteUser(userId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    list.remove(position);
                    adapter.notifyItemRemoved(position);
                } else {
                    Log.e(TAG, "Failed to delete user. Code: " + response.code());
                    if (response.code() == 404) {
                        Toast.makeText(getContext(), "User không tồn tại", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error deleting user: " + t.getMessage());
            }
        });
    }



    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}