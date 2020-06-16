package com.example.sms.dustinfo.data;

import com.example.sms.dustinfo.dust_material.FindDust;
import com.example.sms.dustinfo.util.FindDustUtil;

import retrofit2.Callback;

/**
 * Created by SCIT on 2018-07-18.
 */

public class LocationFineDustRepository implements FineDustRepository
{
    private FindDustUtil mFindDustUtil;
    private double mLatitude;
    private double mLongitude;

    public LocationFineDustRepository(){
        mFindDustUtil = new FindDustUtil();
    }

    public LocationFineDustRepository(double lat, double lng){
        this();
        this.mLatitude = lat;
        this.mLongitude = lng;
    }

    @Override
    public boolean isAvailable() {
        if(mLatitude != 0.0 && mLongitude!=0.0){
            return true;
        }
        return false;
    }

    @Override
    public void getFindDustData(Callback<FindDust>callback){
        mFindDustUtil.getApi().getFindDust(mLatitude, mLongitude).enqueue(callback);
    }
}
