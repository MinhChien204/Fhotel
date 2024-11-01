package learn.fpoly.fhotel.Retrofit;


import static learn.fpoly.fhotel.Retrofit.ApiService.DOMAIN;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class HttpRequest {
    private ApiService apiService;

    public HttpRequest(){
        apiService = new Retrofit.Builder()//khởi tạo dối tượng Restrofit thông qua Restrofit.Build
                .baseUrl(DOMAIN)//cáu hình các thông số như base URL
                .addConverterFactory(GsonConverterFactory.create())//chuyển đổi đối tượng gson sang dối tượng
                .build().create(ApiService.class);
    }
    public ApiService callAPI(){
        return apiService;
    }
}

