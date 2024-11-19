package learn.fpoly.fhotel.Retrofit;

import java.util.ArrayList;
import java.util.List;

import learn.fpoly.fhotel.Model.LoginRequest;
import learn.fpoly.fhotel.Model.PasswordUpdateRequest;
import learn.fpoly.fhotel.response.LoginResponse;
import learn.fpoly.fhotel.response.Response;
import learn.fpoly.fhotel.Model.Room;
import learn.fpoly.fhotel.Model.RoomService;
import learn.fpoly.fhotel.Model.User;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {
    String DOMAIN ="http://10.0.2.2:3000/";
    @GET("api/rooms")
    Call<Response<ArrayList<Room>>> getRooms();
    //API user
    @GET("api/room/{id}")
    Call<Response<Room>> getRoomById(@Path("id") String roomId);
    //
    @GET("api/room/{id}/services")
    Call<Response<ArrayList<RoomService>>> getServiceByIdRoom(@Path("id") String roomId);
    @GET("api/user")
    Call<List<User>> getUsers();
    /////
    @GET("api/detail_user/{id}")
    Call<User> getUserDetails(@Path("id") String userId);
    ///////
    @GET("api/getuserbyid/{id}")
    Call <Response<User>> getuserbyid(@Path("id") String userId);
    //////
    @POST("api/add_user")
    Call<User> createUser(@Body User user);
    @PUT("api/update_user/{id}")
    Call<User> chage_pass(@Path("id") String userId);
    @PUT("api/update_user/{id}")
    Call<Response<User>> updateUser(@Path("id") String userId, @Body User user);
    @DELETE("api/delete_user/{id}")
    Call<Void> deleteUser(@Path("id") String userId);
    @POST("api/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
    @FormUrlEncoded
    @POST("api/register")
    Call<Response<User>> register(
            @Field("username") String username,
            @Field("password") String password,
            @Field("email") String email,
            @Field("phonenumber") String phonenumber
    );
    @Multipart
    @PUT("api/upload_user_image/{id}")
    Call<Response<User>> uploadUserImage(
            @Path("id") String userId, // user id
            @Part MultipartBody.Part image
    );
    @PUT("api/update_password/{id}")
    Call<Response<User>> updatePassword(
            @Path("id") String userId,
            @Body PasswordUpdateRequest passwordUpdateRequest
    );
}
