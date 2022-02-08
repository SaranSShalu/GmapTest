package com.mytoshiba.gmap_test;

import com.google.android.gms.maps.model.LatLng;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitApi {

    String URL_BASE = "https://dev.d-inventions.com/";

    @POST("api.php")
    Call<ResponseBody> createPost(@Body Data data);

}
