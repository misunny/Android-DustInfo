package com.example.sms.dustinfo.finddust;

import com.example.sms.dustinfo.dust_material.FindDust;

/**
 * Created by SCIT on 2018-07-18.
 */

public class FineDustContract
{
    public interface View{
        void showFindDustResult(FindDust findDust);
        void showLoadError(String message);
        void loadingStart();
        void loadingEnd();
        void relaod(double lat, double lng);

    }


    interface UserActionListener{
        void loadFineDustData();
    }
}
