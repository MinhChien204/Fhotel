package learn.fpoly.fhotel.Fragment;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import learn.fpoly.fhotel.Adapter.QLiServiceAdapter;
import learn.fpoly.fhotel.Model.Service;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.Retrofit.ApiService;
import learn.fpoly.fhotel.response.Response;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuanLiServiceFragment extends Fragment {
    private static final int IMAGE_PICK_REQUEST_CODE = 1001;
    private ApiService apiService;
    private QLiServiceAdapter adapter;
    private RecyclerView rcv;
    private Uri selectedImageUri;
    private AlertDialog dialog;
    private ImageView ivSelectedImage;
    private List<Service> list;
    private Button btnadd;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quan_li_service, container, false);
        rcv = view.findViewById(R.id.rcv_listRoom);
        btnadd = view.findViewById(R.id.btnAdd);

        // Thiết lập LayoutManager cho RecyclerView
        rcv.setLayoutManager(new LinearLayoutManager(getContext()));
        // Khởi tạo Retrofit API service
        apiService = createApiService();

        loadData();  // Tải dữ liệu dịch vụ

        btnadd.setOnClickListener(v -> showAddRoomDialog());

        return view;
    }

    private ApiService createApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(ApiService.class);
    }

    private void loadData() {
        apiService.getServices().enqueue(new Callback<Response<ArrayList<Service>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<Service>>> call, retrofit2.Response<Response<ArrayList<Service>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Service> services = response.body().getData();
                    Log.d("listsize", "Number of services: " + services.size());
                    list = services; // Cập nhật lại list
                    setupRecyclerView(services);
                    adapter.setOnItemClickListener(new QLiServiceAdapter.OnItemClickListener() {
                        @Override
                        public void onDeleteClick(int position) {
                            showDeleteConfirmationDialog(position); // Hiển thị hộp thoại xác nhận xóa
                        }
                    });
                } else {
                    Log.e("QuanLiService", "Failed to fetch services. Response code: " + response.code());
                    showToast("Failed to load services");
                }
            }

            @Override
            public void onFailure(Call<Response<ArrayList<Service>>> call, Throwable throwable) {
                Log.e("QuanLiService", "Error fetching services: " + throwable.getMessage());
                showToast("Error: " + throwable.getMessage());
            }
        });
    }


    private void setupRecyclerView(List<Service> services) {
        if (adapter == null) {
            adapter = new QLiServiceAdapter(getContext(), services);
            rcv.setAdapter(adapter);
        } else {
            adapter.updateData(services);
        }
    }

    private void showAddRoomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_service, null);
        builder.setView(dialogView);

        ivSelectedImage = dialogView.findViewById(R.id.ivSelectedImage);
        EditText edNameService = dialogView.findViewById(R.id.edNameService);
        EditText edPrice = dialogView.findViewById(R.id.edPrice);
        EditText edDescription = dialogView.findViewById(R.id.edDescription);
        Button btnSelectImage = dialogView.findViewById(R.id.btnSelectImage);
        Button btnSave = dialogView.findViewById(R.id.btnSaveService);
        Button btnCancel = dialogView.findViewById(R.id.btnCancelService);

        btnSelectImage.setOnClickListener(v -> checkPermissionAndOpenGallery());
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String name = edNameService.getText().toString().trim();
            String priceStr = edPrice.getText().toString().trim();
            String description = edDescription.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty() || description.isEmpty()) {
                showToast("Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            double price = Double.parseDouble(priceStr);
            addService(name, price, description, selectedImageUri);
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

    private void addService(String name, double price, String description, Uri imageUri) {
        RequestBody namePart = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody pricePart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(price));
        RequestBody descriptionPart = RequestBody.create(MediaType.parse("text/plain"), description);

        MultipartBody.Part imagePart = null;
        if (imageUri != null) {
            File file = new File(getRealPathFromURI(imageUri));
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
            imagePart = MultipartBody.Part.createFormData("image", file.getName(), fileBody);
        }

        apiService.addService(namePart, pricePart, descriptionPart, imagePart).enqueue(new Callback<Response<Service>>() {
            @Override
            public void onResponse(Call<Response<Service>> call, retrofit2.Response<Response<Service>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showToast("Thêm dịch vụ thành công!");
                    loadData();
                } else {
                    showToast("Thêm dịch vụ thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Response<Service>> call, Throwable t) {
                showToast("Lỗi: " + t.getMessage());
            }
        });
    }

    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            return cursor.getString(index);
        }
        return uri.getPath();
    }

    private void showDeleteConfirmationDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa người dùng này không?");

        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteService(position); // Thực hiện xóa nếu người dùng đồng ý
            }
        });

        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Đóng hộp thoại nếu người dùng hủy
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void deleteService(int position) {
        // Lấy id của dịch vụ từ danh sách
        String serviceId = list.get(position).get_id(); // _id là tên thuộc tính MongoDB
        Log.d(TAG, "Attempting to delete service with ID: " + serviceId);

        // Gọi API xóa
        apiService.deleteService(serviceId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    // Xóa dịch vụ khỏi danh sách hiển thị
                    list.remove(position);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(getContext(), "Xóa dịch vụ thành công", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String errorMessage = response.errorBody().string(); // Đọc chi tiết lỗi
                        Log.e(TAG, "Error response: " + errorMessage);
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    Toast.makeText(getContext(), "Lỗi khi xóa dịch vụ. Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error deleting service: " + t.getMessage());
                Toast.makeText(getContext(), "Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
