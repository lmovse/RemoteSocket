package com.jooff.remotecontrolsocket;

import android.view.View;

/**
 * Created by Jooff on 2017/1/11.
 */

public class RemoteSocket {
    private int socketImage;
    private int alarmImage;
    private String socketName;
    private String alarmTime;
    private boolean mSwitchCompat;

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public int getAlarmImage() {
        if (alarmImage == 0) {
            return View.GONE;
        }
        else return View.VISIBLE;
    }

    public void setAlarmImage(int alarmImage) {
        this.alarmImage = alarmImage;
    }

    public String getSocketName() {
        return socketName;
    }

    public void setSocketName(String socketName) {
        this.socketName = socketName;
    }

    public boolean getSwitchCompat() {
        return mSwitchCompat;
    }

    public void setSwitchCompat(boolean switchCompat) {
        mSwitchCompat = switchCompat;
    }

    public int getSocketImage() {
        return socketImage;
    }

    public void setSocketImage(int socketImage) {
        this.socketImage = socketImage;
    }
}
