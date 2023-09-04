package com.pharosmed.walker.ui;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pharosmed.walker.R;
import com.pharosmed.walker.constants.Global;
import com.pharosmed.walker.utils.SPHelper;

import java.text.MessageFormat;

/**
 * Created by zhanglun on 2021/1/26
 * Describe:
 */
public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    private ImageView ivRed;
    private ImageView ivPoint;
    private TextView tvBattery;
    private ImageView ivPower;
    private ImageView ivVoice;
    private ImageView ivHeader;
    private TextView tvHeader;
    private TextView tvBleBattery;
    private AudioManager audioManager = null; // Audio管理器，
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initConfig(savedInstanceState);
        ivRed = findView(R.id.iv_red);
        ivPoint = findView(R.id.iv_point);
        tvBattery = findView(R.id.tv_battery);
        ivPower = findView(R.id.iv_power);
        ivVoice = findView(R.id.iv_voice);
        tvHeader = findView(R.id.tv_header);
        ivHeader = findView(R.id.iv_header);
        tvBleBattery = findView(R.id.tv_ble_battery);
        if (ivVoice != null){
            ivVoice.setOnClickListener(v -> {
                audioManager = (AudioManager)getSystemService(Service.AUDIO_SERVICE);
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
            });
        }
        if (tvHeader != null){
            tvHeader.setOnClickListener(v -> startTargetActivity(UserActivity.class,false));
        }
        if (ivHeader != null){
            ivHeader.setOnClickListener(v -> startTargetActivity(UserActivity.class,false));
        }

    }

    private void initConfig(Bundle savedInstanceState) {
        if (this.getLayoutResId() > 0) {
            this.setContentView(this.getLayoutResId());
        }
        this.initialize(savedInstanceState);
    }

    protected abstract void initialize(Bundle savedInstanceState);
    protected abstract int getLayoutResId();
    public void startTargetActivity(Class<?> targetActivity,boolean isEndActivity){
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
        if (isEndActivity){
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPoint(Global.isConnected);
        setHeader();
        setTvBleBattery(Global.BLE_BATTERY);

    }

    public void startTargetActivity(Bundle bundle, Class<?> targetActivity, boolean isEndActivity){
        Intent intent = new Intent(this, targetActivity);
        intent.putExtras(bundle);
        startActivity(intent);
        if (isEndActivity){
            finish();
        }
    }
    protected final <T extends View> T findView(int id) {
        return (T) super.findViewById(id);
    }
    public void setPoint(boolean isConnected) {
        if (ivPoint != null) {
            if (isConnected) {
                ivPoint.setImageResource(R.drawable.round_green_point);
            } else {
                ivPoint.setImageResource(R.drawable.round_red_point);
            }
        }
    }

    public void setBattery(int battery,int batteryStatus) {
        if (tvBattery != null) {
            tvBattery.setText(MessageFormat.format("{0}%", battery));
        }
        if (ivPower != null){
            if (batteryStatus == BatteryManager.BATTERY_STATUS_CHARGING){
                ivPower.setVisibility(View.VISIBLE);
            }else {
                ivPower.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void setHeader(){
        if (tvHeader != null){
            tvHeader.setText(SPHelper.getUserName());
        }
    }
    public void setRed() {
        if (ivRed != null) {
            ivRed.setImageResource(R.drawable.round_red_point);
        }
    }
    public void setTvBleBattery(int value) {
        if (tvBleBattery != null) {
            tvBleBattery.setText(MessageFormat.format("{0}%", value));
        }
    }
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
