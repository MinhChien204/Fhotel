package learn.fpoly.fhotel.Retrofit;

import java.util.ArrayList;
import java.util.List;

import learn.fpoly.fhotel.Model.Booking;
import learn.fpoly.fhotel.Model.Favourite;
import learn.fpoly.fhotel.Model.LoginRequest;
import learn.fpoly.fhotel.Model.Notification;
import learn.fpoly.fhotel.Model.PasswordUpdateRequest;
import learn.fpoly.fhotel.Model.TokenFacebookRequest;
import learn.fpoly.fhotel.Model.TokenRequest;
import learn.fpoly.fhotel.Model.TypeRoom;
import learn.fpoly.fhotel.Model.UserVoucher;
import learn.fpoly.fhotel.response.LoginResponse;
import learn.fpoly.fhotel.response.Response;
import learn.fpoly.fhotel.Model.Room;
import learn.fpoly.fhotel.Model.User;
import learn.fpoly.fhotel.response.UpdateFcmTokenRequest;
import okhttp3.MultipartBody;
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
import retrofit2.http.Query;

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
    @POST("api/login/google")
    Call<Response<User>> loginWithGoogle(@Body TokenRequest tokenRequest);
    @POST("api/login/facebook")
    Call<Response<User>> loginWithFacebook(@Body TokenFacebookRequest tokenFacebookRequest);
    @FormUrlEncoded
    @POST("api/register")
    Call<Response<User>> register(
            @Field("username") String username,
            @Field("password") String password,
            @Field("name") String name,
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

    //booking
    @GET("api/user/{userId}/bookings")
    Call<Response<List<Booking>>> getUserBookings(@Path("userId") String userId);

    //get 1 booking
    @GET("api/bookings/{id}")
    Call<Response<Booking>> getBookingById(@Path("id") String bookingId);

    @POST("api/book_room")
    Call<Response<Booking>> createBooking(@Body Booking booking);

    @PUT("api/update-status-booking/{id}")
    Call<Response<Booking>> updateBookingStatus(@Path("id") String id, @Body Booking status);

    @PUT("api/update-status-room/{id}")
    Call<Response<Room>> updateRoomStatus(@Path("id") String id, @Body Room status);
    @DELETE("api/cancel_booking/{id}")
    Call<Void> cancelBooking(@Path("id") String bookingId);
    /////////////
    @POST("api/addFavourite")
    Call<Response<Favourite>> createfavourite(@Body Favourite favourite);

    //////////////////
    @DELETE("api/delete_Favourite/{id}")
    Call<Void> deletefavourites(@Path("id") String favouritesId);
    /////////////
    @GET("api/favourites/check")
    Call<Response<Favourite>> checkFavourite(@Query("userId") String userId, @Query("roomId") String roomId);

    @GET("api/user/{userId}/favourites")
    Call<Response<List<Favourite>>> getUserFavourites(@Path("userId") String userId);
    /////////////////// get one
    @GET("api/favouritebyid/{id}")
    Call<Response<Favourite>> getbyidFavourites(@Path("id") String id);

    //Type Room
    @GET("api/typerooms")
    Call<Response<ArrayList<TypeRoom>>> getTypeRooms();

    //Voucher
    @GET("api/user/{userId}/vouchers")
    Call<Response<List<UserVoucher>>> getUserVouchers(@Path("userId") String userId);

    //Notification
    @GET("/api/notifications")
    Call<Response<List<Notification>>> getNotifications(@Query("userId") String userId);

    @POST("api/add_notification")
    Call<Response<Notification>> createNotification(@Body Notification notification);

    @POST("api/update-fcm-token")
    Call<Response<Void>> updateFcmToken(@Body UpdateFcmTokenRequest request);

}
