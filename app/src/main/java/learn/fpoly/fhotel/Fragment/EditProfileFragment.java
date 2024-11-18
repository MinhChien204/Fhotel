package learn.fpoly.fhotel.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import learn.fpoly.fhotel.Model.Room;
import learn.fpoly.fhotel.Model.User;
import learn.fpoly.fhotel.R;
import learn.fpoly.fhotel.Retrofit.HttpRequest;
import learn.fpoly.fhotel.activity.Change_password;
import learn.fpoly.fhotel.activity.Home_User;
import learn.fpoly.fhotel.response.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class EditProfileFragment extends Fragment {
    private HttpRequest httpRequest;
    EditText mail_pro,txten_prf,date_pof,Address_prf,Phone_prf;
    ImageView image_prf;
    RadioButton Male_prf,Famal_prf;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        mail_pro = view.findViewById(R.id.mail_pro);
        txten_prf = view.findViewById(R.id.txten_prf);
        date_pof = view.findViewById(R.id.date_pof);
        Address_prf = view.findViewById(R.id.Address_prf);
        Phone_prf = view.findViewById(R.id.Phone_prf);
        image_prf = view.findViewById(R.id.image_prf);
        Male_prf = view.findViewById(R.id.Male_prf);
        Famal_prf = view.findViewById(R.id.Famal_prf);

        // Kiểm tra nếu activity là Home_User thì ẩn BottomNavigationView
        if (getActivity() instanceof Home_User) {
            ((Home_User) getActivity()).bottomNavigationView.setVisibility(View.GONE);
        }
        httpRequest = new HttpRequest();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);
        Log.d("dcm", "onCreate: " + userId);

        // Kiểm tra nếu ID hợp lệ và gọi API
        if (userId != null && !userId.isEmpty()) {
            fetchuserById(userId);
        } else {
            Toast.makeText(getContext(), "Invalid Room ID", Toast.LENGTH_SHORT).show();
        }


        return view;
    }
    public void fetchuserById(String userId) {
        Call<Response<User>> call = httpRequest.callAPI().getuserbyid(userId);
        call.enqueue(new Callback<Response<User>>() {
            @Override
            public void onResponse(Call<Response<User>> call, retrofit2.Response<Response<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Response<User> roomResponse = response.body(); // Lấy đối tượng Response
                    if (roomResponse.getStatus() == 200) { // Kiểm tra status
                        User user = roomResponse.getData(); // Lấy dữ liệu Room
                        Log.d("User dcm huy oc cho", "onResponse: " + user.getName());
                        // Gán dữ liệu phòng vào các View
                        mail_pro.setText(user.getEmail());
                        txten_prf.setText(user.getName());
                        Address_prf.setText(user.getAddress());
                        Phone_prf.setText(String.valueOf(user.getPhonenumber()));
                        if(user.getGender().equalsIgnoreCase("female")){
                            Famal_prf.setChecked(true);
                            Male_prf.setChecked(false);
                        }if(user.getGender().equalsIgnoreCase("male")){
                            Famal_prf.setChecked(false);
                            Male_prf.setChecked(true);
                        }
                        // Nếu có hình ảnh, hãy gán nó vào imgRom_details
                        Glide.with(getContext())
                                .load(user.getAvartar()) // Thay `getImageUrl()` bằng phương thức lấy URL ảnh
                                .into(image_prf);
                    } else {
                        Toast.makeText( getContext(), "Room not found: " + roomResponse.getMessenger(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText( getContext(), "Room not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<User>> call, Throwable t) {
                Toast.makeText( getContext(), "Failed to load room details: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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