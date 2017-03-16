package com.jooff.remotecontrolsocket;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jooff on 2017/1/11.
 */

public class SocketAdapter extends RecyclerView.Adapter<SocketAdapter.SocketHolder> {
    private ArrayList<RemoteSocket> list;

    public SocketAdapter(ArrayList<RemoteSocket> list) {
        this.list = list;
    }

    @Override
    public SocketHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SocketHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_item, parent, false));
    }

    public interface OnCheckChangeListener {
        void onCheckChange(CompoundButton v, int position, boolean isChecked);
    }

    private OnCheckChangeListener onCheckChangeListener;

    public void setOnCheckChangeListener(OnCheckChangeListener onCheckChangeListener) {
        this.onCheckChangeListener = onCheckChangeListener;
    }

    @Override
    public void onBindViewHolder(final SocketHolder holder, int position) {
        RemoteSocket rs = list.get(position);
        holder.mSocket.setImageResource(rs.getSocketImage());
        holder.socketNumber.setText(rs.getSocketName());
        holder.socketSwitch.setChecked(rs.getSwitchCompat());
        if (onCheckChangeListener != null) {
            holder.socketSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        holder.mSocket.setImageResource(R.drawable.ic_power_settings_new_black_24dp);
                    } else {
                        holder.mSocket.setImageResource(R.drawable.ic_power_settings_new_grey_500_24dp);
                    }
                    onCheckChangeListener.onCheckChange(buttonView, holder.getLayoutPosition(), isChecked);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class SocketHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.socket)
        ImageView mSocket;
        @Bind(R.id.socket_number)
        TextView socketNumber;
        @Bind(R.id.alarm_image)
        ImageView alarmImage;
        @Bind(R.id.alarm_time)
        TextView alarmTime;
        @Bind(R.id.socket_switch)
        SwitchCompat socketSwitch;

        SocketHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
