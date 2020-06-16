package com.example.sms.dustinfo.finddust;

import com.example.sms.dustinfo.data.FineDustRepository;
import com.example.sms.dustinfo.dust_material.FindDust;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by SCIT on 2018-07-18.
 */

public class FineDustPresenter implements FineDustContract.UserActionListener{

    private final FineDustRepository mRepository;
    private final FineDustContract.View mView;

    public FineDustPresenter(FineDustRepository repository, FineDustContract.View view){
        this.mRepository = repository;
        this.mView = view;
    }

    @Override
    public void loadFineDustData(){
        if(mRepository.isAvailable()){
            mView.loadingStart();
            mRepository.getFindDustData(new Callback<FindDust>() {
                @Override
                public void onResponse(Call<FindDust> call, Response<FindDust> response) {
                    mView.showFindDustResult(response.body());
                    mView.loadingEnd();
                }

                @Override
                public void onFailure(Call<FindDust> call, Throwable t) {
                    mView.showLoadError(t.getLocalizedMessage());
                    mView.loadingEnd();
                }
            });
        }
    }

}
