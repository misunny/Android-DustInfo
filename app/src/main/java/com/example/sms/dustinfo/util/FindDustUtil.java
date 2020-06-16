package com.example.sms.dustinfo.util;

import com.example.sms.dustinfo.dust_material.FindDust;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by SCIT on 2018-07-17.
 */

public class FindDustUtil {

    private FindDustApi mGetApi;

    public FindDustUtil() {

        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(FindDustApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mGetApi = mRetrofit.create(FindDustApi.class);
    }

    public FindDustApi getApi(){
        return mGetApi;
    }
}
