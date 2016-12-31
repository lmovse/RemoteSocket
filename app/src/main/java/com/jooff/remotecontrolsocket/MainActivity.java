package com.jooff.remotecontrolsocket;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jooff.remotecontrolsocket.MyInterface.OnDataReceivedListener;
import com.jooff.remotecontrolsocket.MyInterface.TimeChoose;
import com.jooff.remotecontrolsocket.MyThread.receiveThread;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

import static com.jooff.remotecontrolsocket.LoginActivity.mSocket;

public class MainActivity extends AppCompatActivity implements TimeChoose {

    private static final String TAG = "MainActivity";
    private static final String DIALOG_TIME = "dialogTime";
    private static final boolean READ_FLAG = false;
    private static OutputStream mOutputStream;
    public static InputStream mInputStream;
    public receiveThread mReceiveThread;

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

        mReceiveThread.setOnDataReceivedListener(new OnDataReceivedListener() {
            @Override
            public void onDataReceived(final StringBuilder sb) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (sb.toString().length() == 4) {
                            setSwitchCompat(sb, mSwitchCompat1, socket1, 0);
                            setSwitchCompat(sb, mSwitchCompat2, socket2, 1);
                            setSwitchCompat(sb, mSwitchCompat3, socket3, 2);
                            setSwitchCompat(sb, mSwitchCompat4, socket4, 3);
                        } else {
                            Toast.makeText(MainActivity.this, sb.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        ImageView fabIcon = new ImageView(this);
        fabIcon.setImageResource(R.drawable.ic_add_alert_white_24dp);

        switchOnCheckedChangeListener(mSwitchCompat1, socket1, "0");
        switchOnCheckedChangeListener(mSwitchCompat2, socket2, "1");
        switchOnCheckedChangeListener(mSwitchCompat3, socket3, "2");
        switchOnCheckedChangeListener(mSwitchCompat4, socket4, "3");

        FloatingActionButton fab = new FloatingActionButton.Builder(this)
                .setBackgroundDrawable(R.drawable.button_action_color3)
                .setContentView(fabIcon)
                .build();

        TextView S1 = setTextView(s1);
        TextView S2 = setTextView(s2);
        TextView S3 = setTextView(s3);
        TextView S4 = setTextView(s4);

        SubActionButton.Builder builder = new SubActionButton.Builder(this);

        SubActionButton s1Timing = getActionButton(S1, builder);
        SubActionButton s2Timing = getActionButton(S2, builder);
        SubActionButton s3Timing = getActionButton(S3, builder);
        SubActionButton s4Timing = getActionButton(S4, builder);

        setRelayTiming(s1Timing, "s1");
        setRelayTiming(s2Timing, "s2");
        setRelayTiming(s3Timing, "s3");
        setRelayTiming(s4Timing, "s4");

        new FloatingActionMenu.Builder(this)
                .addSubActionView(s1Timing)
                .addSubActionView(s2Timing)
                .addSubActionView(s3Timing)
                .addSubActionView(s4Timing)
                .attachTo(fab).build();
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
            mReceiveThread = new receiveThread();
            new Thread(mReceiveThread).start();
            mOutputStream.write("k".getBytes("UTF-8"));
            mOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SubActionButton getActionButton(TextView s1, SubActionButton.Builder builder) {
        return builder
                .setContentView(s1)
                .setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.button_action_color3))
                .build();
    }

    private void setRelayTiming(SubActionButton s1Timing, final String relayName) {
        s1Timing.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                FragmentManager manager = getSupportFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(relayName);
                dialog.show(manager, DIALOG_TIME);
            }
        });
    }


    private TextView setTextView(String textView) {
        TextView mTextView = new TextView(this);
        mTextView.setText(textView);
        mTextView.setTextColor(getResources().getColor(R.color.white));
        return mTextView;
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

}
