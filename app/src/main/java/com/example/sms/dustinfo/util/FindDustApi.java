package com.example.sms.dustinfo.util;

import com.example.sms.dustinfo.dust_material.FindDust;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by SCIT on 2018-07-17.
 */

public interface FindDustApi {

    String BASE_URL = "http://api.weatherplanet.co.kr/";

    @Headers("appKey: 6b200e091d1a4d7e83fb9b4732809b33")
    @GET("weather/dust?version=1")
    Call<FindDust> getFindDust(@Query("lat")double latitude,
                               @Query("lon")double longitude);

}
