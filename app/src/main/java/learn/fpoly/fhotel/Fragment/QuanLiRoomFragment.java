package learn.fpoly.fhotel.Fragment;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import learn.fpoly.fhotel.Adapter.QLiRoomAdapter;
import learn.fpoly.fhotel.Model.Room;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.Retrofit.ApiService;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import learn.fpoly.fhotel.response.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuanLiRoomFragment extends Fragment {
    private static final int IMAGE_PICK_REQUEST_CODE = 1001;
    ApiService apiService;
    QLiRoomAdapter adapter;
    RecyclerView rcv;
    private List<Room> list;
    private Uri selectedImageUri;
    private AlertDialog dialog;
    private ImageView ivSelectedImage;
    FloatingActionButton fab;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quan_li_room, container, false);
        rcv = view.findViewById(R.id.rcv_listRoom);
        fab = view.findViewById(R.id.fab);
        rcv.setLayoutManager(new LinearLayoutManager(getContext()));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        loadData();  // Gọi loadData để tải dữ liệu phòng

        fab.setOnClickListener(v -> showAddRoomDialog());

        return view;
    }

    void loadData() {
        apiService.getRooms().enqueue(new Callback<Response<ArrayList<Room>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<Room>>> call, retrofit2.Response<Response<ArrayList<Room>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Room> listroom = response.body().getData();
                    list = listroom;
                    adapter = new QLiRoomAdapter(getContext(), listroom);
                    adapter.setOnItemClickListener(position -> showDeleteConfirmationDialog(position));
                    rcv.setLayoutManager(new GridLayoutManager(getContext(), 1));
                    rcv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    showToast("Failed to load rooms");
                }
            }

            @Override
            public void onFailure(Call<Response<ArrayList<Room>>> call, Throwable throwable) {
                showToast("Error: " + throwable.getMessage());
            }
        });
    }

    private void showAddRoomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_room, null);
        builder.setView(dialogView);

        ivSelectedImage = dialogView.findViewById(R.id.ivSelectedImage);
        EditText edNameService = dialogView.findViewById(R.id.edName);
        EditText edPrice = dialogView.findViewById(R.id.edPrice);
        EditText edDescription = dialogView.findViewById(R.id.edDescription);
        EditText edRating = dialogView.findViewById(R.id.edRating);
        EditText edCapacity = dialogView.findViewById(R.id.edCapacity);
        EditText edRoomCode = dialogView.findViewById(R.id.edRoomCode);
        Button btnSelectImage = dialogView.findViewById(R.id.btnSelectImage);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        btnSelectImage.setOnClickListener(v -> checkPermissionAndOpenGallery());
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String name = edNameService.getText().toString().trim();
            String priceStr = edPrice.getText().toString().trim();
            String description = edDescription.getText().toString().trim();
            String rating = edRating.getText().toString().trim();
            String capacity = edCapacity.getText().toString().trim();
            String roomcode = edRoomCode.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty() || description.isEmpty() || rating.isEmpty() || capacity.isEmpty() || roomcode.isEmpty()) {
                showToast("Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            double price = Double.parseDouble(priceStr);
            addRoom(name, price, Integer.parseInt(capacity), roomcode, Double.parseDouble(rating), description, selectedImageUri);
            dialog.dismiss();
        });

        dialog = builder.create();
        dialog.show();
    }

    private void checkPermissionAndOpenGallery() {
        String permission = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, 1);
        } else {
            openGallery();
        }
    }

    private void addRoom(String name, double price, int capacity, String room_code, double rating, String description, Uri imageUri) {
        RequestBody namePart = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody pricePart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(price));
        RequestBody ratingPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(rating));
        RequestBody capacityPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(capacity));
        RequestBody roomcodePart = RequestBody.create(MediaType.parse("text/plain"), room_code);
        RequestBody descriptionPart = RequestBody.create(MediaType.parse("text/plain"), description);

        MultipartBody.Part imagePart = null;
        if (imageUri != null) {
            File file = new File(getRealPathFromURI(imageUri));
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
            imagePart = MultipartBody.Part.createFormData("image", file.getName(), fileBody);
        }

        apiService.addRoom(namePart, pricePart, descriptionPart, ratingPart, capacityPart, roomcodePart, imagePart)
                .enqueue(new Callback<Response<Room>>() {
                    @Override
                    public void onResponse(Call<Response<Room>> call, retrofit2.Response<Response<Room>> response) {
                        if (response.isSuccessful()) {
                            showToast("Thêm room thành công!");
                            loadData();  // Load lại dữ liệu phòng sau khi thêm thành công
                        } else {
                            showToast("Thêm room thất bại: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Response<Room>> call, Throwable throwable) {
                        showToast("Lỗi: " + throwable.getMessage());
                    }
                });
    }

    private String getRealPathFromURI(Uri uri) {
        try (Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                return cursor.getString(index);
            }
        }
        return uri.getPath();
    }

    private void showDeleteConfirmationDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa phòng này không?");
        builder.setPositiveButton("Đồng ý", (dialog, which) -> deleteRoom(position));
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void deleteRoom(int position) {
        String roomId = list.get(position).getId(); // _id là tên thuộc tính MongoDB
        Log.d(TAG, "Attempting to delete room with ID: " + roomId);

        apiService.deleteRoom(roomId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    list.remove(position);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(getContext(), "Xóa room thành công", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String errorMessage = response.errorBody() != null ? response.errorBody().string() : "Lỗi khi xóa room";
                        Log.e(TAG, "Error response: " + errorMessage);
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error body", e);
                        Toast.makeText(getContext(), "Lỗi khi xóa room", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error deleting room: " + t.getMessage());
                Toast.makeText(getContext(), "Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_REQUEST_CODE);
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                ivSelectedImage.setVisibility(View.VISIBLE);
                ivSelectedImage.setImageURI(selectedImageUri);
                showToast("Đã chọn ảnh");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showToast("Quyền truy cập được cấp!");
            openGallery();
        } else {
            showToast("Quyền truy cập bị từ chối!");
        }
    }
}
