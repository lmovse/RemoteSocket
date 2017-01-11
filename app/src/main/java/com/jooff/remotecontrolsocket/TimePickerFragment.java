package com.jooff.remotecontrolsocket;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import com.jooff.remotecontrolsocket.MyInterface.TimeChoose;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.R.string.ok;

/**
 * Created by Jooff on 2016/12/16.
 */

public class   TimePickerFragment extends DialogFragment {

    private static final String ARG_NAME = "relayName";

    @Bind(R.id.time_picker)
    TimePicker mTimePicker;

    public static TimePickerFragment newInstance(String relayName){
        Bundle bundle = new Bundle();
        bundle.putString(ARG_NAME, relayName);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);
        ButterKnife.bind(this, view);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton(ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TimeChoose choose = (TimeChoose) getActivity();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            String relayName = getArguments().getString(ARG_NAME);
                            String hour = timeTwoNumber(String.valueOf(mTimePicker.getHour()));
                            String minute = timeTwoNumber(String.valueOf(mTimePicker.getMinute()));
                            String alarm = relayName + hour + minute;
                            choose.onTimeChooseOk(alarm);
                        }
                    }
                })
                .create();
    }

    public String timeTwoNumber(String s){
        if (s.length() == 1){
            return "0" + s ;
        }
        else return s;
    }
}
