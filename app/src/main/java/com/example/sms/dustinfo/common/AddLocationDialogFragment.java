package com.example.sms.dustinfo.common;

import android.app.Dialog;
import android.content.DialogInterface;
import android.icu.util.BuddhistCalendar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.sms.dustinfo.R;

/**
 * Created by SCIT on 2018-07-23.
 */

public class AddLocationDialogFragment extends DialogFragment {

    private EditText mCityEditText;

    private OnClickListener mOkClickListener;

    public interface OnClickListener{
        void onOkClicked(String city);

    }

    public void setOnClickListener(OnClickListener listener){
        mOkClickListener = listener;
    }

    public static AddLocationDialogFragment newInstance(OnClickListener listener){
        //Bundle args = new Bundle();
        AddLocationDialogFragment fragment = new AddLocationDialogFragment();
        fragment.setOnClickListener(listener);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_add_location, null, false);

        mCityEditText = view.findViewById(R.id.city_edit);


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("위치 추가");
        builder.setView(view);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String city = mCityEditText.getText().toString();
                mOkClickListener.onOkClicked(city);
            }
        });
        builder.setNegativeButton("취소", null);

        return builder.create();
    }
}
