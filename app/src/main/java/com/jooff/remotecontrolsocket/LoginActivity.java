package com.jooff.remotecontrolsocket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.net.Socket;
import java.util.concurrent.Callable;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Single;
import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {
    public static Socket mSocket;
    private SharedPreferences pref;

    @Bind(R.id.input_ip) EditText ipText;
    @Bind(R.id.input_port) EditText portText;
    @Bind(R.id.remember_ip) CheckBox ipRemember;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemember = pref.getBoolean("remember_ip", false);
        if (isRemember) {
            ipText.setText(pref.getString("ip", ""));
            portText.setText(String.valueOf(pref.getInt("port", 8080)));
            ipRemember.setChecked(true);
        }
    }

    @OnClick(R.id.btn_login)
    void setLoginButton() {
        final String ip = ipText.getText().toString();
        final int port = Integer.valueOf(portText.getText().toString());
        Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                mSocket = new Socket(ip, port);
                return true;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Boolean>() {
                    @Override
                    public void onSuccess(Boolean value) {
                        Toast.makeText(LoginActivity.this, "已连接到 " + ip, Toast.LENGTH_SHORT).show();
                        if (ipRemember.isChecked()) {
                            pref.edit().putBoolean("remember_ip", true).apply();
                            pref.edit().putString("ip", ip).apply();
                            pref.edit().putInt("port", port).apply();
                        } else {
                            pref.edit().putBoolean("remember_ip", false).apply();
                        }
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(LoginActivity.this, R.string.error_invalid_ip, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        LoginActivity.this.finish();
    }

}

