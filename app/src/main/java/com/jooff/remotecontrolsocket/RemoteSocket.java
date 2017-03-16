package com.jooff.remotecontrolsocket;

/**
 * Created by Jooff on 2017/1/11.
 */

public class RemoteSocket {
    private int socketImage;
    private String socketName;
    private boolean mSwitchCompat;

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
