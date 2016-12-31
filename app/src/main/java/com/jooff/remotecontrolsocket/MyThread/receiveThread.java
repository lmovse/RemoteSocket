package com.jooff.remotecontrolsocket.MyThread;

import android.util.Log;

import com.jooff.remotecontrolsocket.MyInterface.OnDataReceivedListener;

import java.io.IOException;

import static android.content.ContentValues.TAG;
import static com.jooff.remotecontrolsocket.MainActivity.mInputStream;

/**
 * Created by Jooff on 2016/12/27.
 */

public class receiveThread implements Runnable {

    private OnDataReceivedListener mOnDataReceivedListener;

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        while (true) {
            try {
                final StringBuilder sb = new StringBuilder();
                int readSize = mInputStream.read(buffer);
                sb.append(new String(buffer, 0, readSize));
                Log.d(TAG, "run: " + sb.toString());

                if (mOnDataReceivedListener != null){
                    mOnDataReceivedListener.onDataReceived(sb);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setOnDataReceivedListener (OnDataReceivedListener onDataReceivedListener){
        this.mOnDataReceivedListener = onDataReceivedListener;
    }
}