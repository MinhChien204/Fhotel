package learn.fpoly.fhotel.Retrofit;

import java.util.ArrayList;
import java.util.List;

import learn.fpoly.fhotel.Model.Response;
import learn.fpoly.fhotel.Model.Room;
import learn.fpoly.fhotel.Model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    String DOMAIN ="http://10.0.2.2:3000/";
    @GET("api/rooms")
    Call<Response<ArrayList<Room>>> getRooms();
    //API user
    @GET("api/room/{id}")
    Call<Response<Room>> getRoomById(@Path("id") String roomId);
    //
    @GET("api/user")
    Call<List<User>> getUsers();
    /////
    @GET("api/detail_user/{id}")
    Call<User> getUserDetails(@Path("id") String userId);
    /////
    @POST("api/add_user")
    Call<User> createUser(@Body User user);
    @PUT("api/update_user/{id}")
    Call<User> updateUser(@Path("id") String userId, @Body User user);
    @DELETE("api/delete_user/{id}")
    Call<Void> deleteUser(@Path("id") String userId);

}
