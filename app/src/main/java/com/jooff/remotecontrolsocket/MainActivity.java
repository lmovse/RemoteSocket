package com.jooff.remotecontrolsocket;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.jooff.remotecontrolsocket.MyInterface.OnDataReceivedListener;
import com.jooff.remotecontrolsocket.MyInterface.TimeChoose;
import com.jooff.remotecontrolsocket.MyThread.ReceiveThread;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

import static com.jooff.remotecontrolsocket.LoginActivity.mSocket;

public class MainActivity extends AppCompatActivity implements TimeChoose {
    private static final String TAG = "MainActivity";
    private static final String DIALOG_TIME = "dialogTime";
    private static final boolean READ_FLAG = false;
    private StringBuilder sb;
    private static OutputStream mOutputStream;
    public static InputStream mInputStream;
    public ReceiveThread mReceiveThread;

    @Bind(R.id.socket1)
    ImageView socket1;
    @Bind(R.id.socket2)
    ImageView socket2;
    @Bind(R.id.socket3)
    ImageView socket3;
    @Bind(R.id.socket4)
    ImageView socket4;
    @Bind(R.id.switch_socket1)
    SwitchCompat mSwitchCompat1;
    @Bind(R.id.switch_socket2)
    SwitchCompat mSwitchCompat2;
    @Bind(R.id.switch_socket3)
    SwitchCompat mSwitchCompat3;
    @Bind(R.id.switch_socket4)
    SwitchCompat mSwitchCompat4;
    @Bind(R.id.bmb)
    BoomMenuButton mButton;
    @BindString(R.string.socket1)
    String s1;
    @BindString(R.string.socket2)
    String s2;
    @BindString(R.string.socket3)
    String s3;
    @BindString(R.string.socket4)
    String s4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setTitle("已连接到：" + mSocket.getInetAddress().toString().substring(1, mSocket.getInetAddress().toString().length()));
        initSocket();

        for (int i = 0; i < mButton.getPiecePlaceEnum().pieceNumber(); i++){
            TextInsideCircleButton.Builder builder = new TextInsideCircleButton.Builder()
                    .normalColor(R.color.colorPrimary)
                    .normalText("S" + i)
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {
                            TimePickerFragment tf = TimePickerFragment.newInstance(String.valueOf(index));
                            tf.show(getSupportFragmentManager(), DIALOG_TIME);
                        }
                    });
            mButton.addBuilder(builder);
        }

        mButton.setNormalColor(Color.TRANSPARENT);
        mButton.setShadowEffect(false);
        mButton.setHighlightedColor(Color.TRANSPARENT);

        mReceiveThread.setOnDataReceivedListener(new OnDataReceivedListener() {
            @Override
            public void onDataReceived(StringBuilder sb) {
                MainActivity.this.sb = sb;
            }
        });

        Observable.create(new Observable.OnSubscribe<StringBuilder>() {
            @Override
            public void call(Subscriber<? super StringBuilder> subscriber) {
                subscriber.onNext(sb);
            }
        }).filter(new Func1<StringBuilder, Boolean>() {
            @Override
            public Boolean call(StringBuilder sb) {
                return sb.length() == 4;
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StringBuilder>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this, sb.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(StringBuilder sb) {
                        setSwitchCompat(sb, mSwitchCompat1, socket1, 0);
                        setSwitchCompat(sb, mSwitchCompat2, socket2, 1);
                        setSwitchCompat(sb, mSwitchCompat3, socket3, 2);
                        setSwitchCompat(sb, mSwitchCompat4, socket4, 3);
                    }
                });
        switchOnCheckedChangeListener(mSwitchCompat1, socket1, "0");
        switchOnCheckedChangeListener(mSwitchCompat2, socket2, "1");
        switchOnCheckedChangeListener(mSwitchCompat3, socket3, "2");
        switchOnCheckedChangeListener(mSwitchCompat4, socket4, "3");
    }

    private void setSwitchCompat(StringBuilder sb, SwitchCompat switchCompat, ImageView socket, int number) {
        if (sb.toString().charAt(number) == '0') {
            switchCompat.setChecked(true);
            socket.setImageResource(R.drawable.ic_power_settings_new_black_24dp);
        }
    }

    private void switchOnCheckedChangeListener(final SwitchCompat switchCompat, final ImageView socket, final String sendMessage) {
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                try {
                    mOutputStream.write(sendMessage.getBytes("UTF-8"));
                    mOutputStream.flush();
                    if (switchCompat.isChecked()) {
                        socket.setImageResource(R.drawable.ic_power_settings_new_black_24dp);
                    } else {
                        socket.setImageResource(R.drawable.ic_power_settings_new_grey_500_24dp);
                    }
                    Log.d(TAG, "onCheckedChanged: send ok");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onTimeChooseOk(String alarm) {
        Toast.makeText(MainActivity.this, alarm, Toast.LENGTH_SHORT).show();
        try {
            mOutputStream.write(alarm.getBytes("UTF-8"));
            mOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initSocket() {
        try {
            mOutputStream = mSocket.getOutputStream();
            mInputStream = mSocket.getInputStream();
            mReceiveThread = new ReceiveThread();
            new Thread(mReceiveThread).start();
            mOutputStream.write("k".getBytes("UTF-8"));
            mOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
