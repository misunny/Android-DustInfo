package com.example.sms.dustinfo.data;

import com.example.sms.dustinfo.dust_material.FindDust;

import retrofit2.Callback;

/**
 * Created by SCIT on 2018-07-18.
 */

public interface FineDustRepository {

    boolean isAvailable();
    void getFindDustData(Callback<FindDust> callback);

}
