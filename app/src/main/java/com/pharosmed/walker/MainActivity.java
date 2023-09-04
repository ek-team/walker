package com.pharosmed.walker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.pharosmed.walker.beans.Battery;
import com.pharosmed.walker.constants.AppKeyManager;
import com.pharosmed.walker.constants.Global;
import com.pharosmed.walker.constants.MessageEvent;
import com.pharosmed.walker.database.TrainPlanManager;
import com.pharosmed.walker.ui.BaseActivity;
import com.pharosmed.walker.ui.ConnectDeviceActivity;
import com.pharosmed.walker.ui.DoctorInfoActivity;
import com.pharosmed.walker.ui.NewsActivity;
import com.pharosmed.walker.ui.PlanActivity;
import com.pharosmed.walker.ui.SettingActivity;
import com.pharosmed.walker.ui.UserActivity;
import com.pharosmed.walker.ui.UserInfoActivity;
import com.pharosmed.walker.ui.VideoPlayerActivity;
import com.pharosmed.walker.utils.DateFormatUtil;
import com.pharosmed.walker.utils.SPHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.Calendar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglun on 2021/4/13
 * Describe:
 */
public class MainActivity extends BaseActivity implements View.OnTouchListener{
    @BindView(R.id.iv_power)
    ImageView ivPower;
    @BindView(R.id.tv_battery)
    TextView tvBattery;
    @BindView(R.id.iv_notification)
    ImageView ivNotification;
    @BindView(R.id.iv_red)
    ImageView ivRed;
    @BindView(R.id.iv_voice)
    ImageView ivVoice;
    @BindView(R.id.iv_point)
    ImageView ivPoint;
    @BindView(R.id.iv_bt)
    ImageView ivBt;
    @BindView(R.id.layout_bluetooth)
    LinearLayout layoutBluetooth;
    @BindView(R.id.iv_header)
    ImageView ivHeader;
    @BindView(R.id.layout_header)
    RelativeLayout layoutHeader;
    @BindView(R.id.tv_header)
    TextView tvHeader;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_am_pm)
    TextView tvAmPm;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.iv_video)
    ImageView ivVideo;
    @BindView(R.id.tv_help)
    TextView tvHelp;
    @BindView(R.id.layout_help)
    RelativeLayout layoutHelp;
    @BindView(R.id.iv_training)
    ImageView ivTraining;
    @BindView(R.id.iv_plan)
    TextView ivPlan;
    @BindView(R.id.iv_doctor)
    TextView ivDoctor;
    @BindView(R.id.iv_news)
    TextView ivNews;
    @BindView(R.id.iv_user_center)
    TextView ivUserCenter;
    @BindView(R.id.iv_setting)
    TextView ivSetting;
    private boolean touchFlag;
    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
        // Android 动态请求权限 Android 10 需要单独处理
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                String[] strings ={Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE };
                ActivityCompat.requestPermissions(this, strings, 1);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    "android.permission.ACCESS_BACKGROUND_LOCATION") != PackageManager.PERMISSION_GRANTED) {
                String[] strings = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        "android.permission.ACCESS_BACKGROUND_LOCATION"};
                ActivityCompat.requestPermissions(this, strings, 2);
            }
        }
//        WakeUtil.getInstance(this).startWake();
//        OneShotUtil.getInstance(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        ivTraining.setOnTouchListener(this);
        ivPlan.setOnTouchListener(this);
        ivNews.setOnTouchListener(this);
        ivDoctor.setOnTouchListener(this);
        ivUserCenter.setOnTouchListener(this);
        ivSetting.setOnTouchListener(this);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.getAction()) {
            case MessageEvent.ACTION_GATT_CONNECTED:
                setPoint(true);
                break;
            case MessageEvent.UPDATE_TOP_TIME:
                updateTime();
                break;
            case MessageEvent.ACTION_GATT_DISCONNECTED:
                setPoint(false);
                break;
            case MessageEvent.BATTERY_REFRESH:
                Battery battery = (Battery) event.getData();
                setBattery(battery.getBatteryVolume(),battery.getBatteryStatus());
                break;
            case MessageEvent.ACTION_READ_DEVICE:
                int bleBattery  = (int) event.getData();
                setTvBleBattery(bleBattery);
                break;
            default:
                break;
        }
    }
    @OnClick({R.id.iv_notification, R.id.iv_red, R.id.iv_voice, R.id.iv_video, R.id.tv_help, R.id.layout_help, R.id.iv_training, R.id.iv_plan, R.id.iv_doctor, R.id.iv_news, R.id.iv_user_center, R.id.iv_setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_notification:
                break;
            case R.id.iv_red:
                break;
            case R.id.iv_voice:
                break;
            case R.id.iv_training:
                if (DateFormatUtil.avoidFastClick(1000)){
                    Bundle bundle = new Bundle();
                    bundle.putInt("ConnectMode",0);
                    startTargetActivity(bundle,ConnectDeviceActivity.class,false);
                }
                break;
            case R.id.iv_doctor:
                if (DateFormatUtil.avoidFastClick(1000)){
                    startTargetActivity(DoctorInfoActivity.class,false);
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("ConnectMode",1);
//                    startTargetActivity(bundle,ConnectDeviceActivity.class,false);
                }
                break;
            case R.id.iv_video:
            case R.id.tv_help:
            case R.id.layout_help:
                Intent intent = new Intent(this, VideoPlayerActivity.class);
                intent.putExtra(AppKeyManager.EXTRA_VIDEO_FILE, "asset:///video_user_help.mp4");
                startActivity(intent);
                break;
            case R.id.iv_plan:
                if (DateFormatUtil.avoidFastClick(1500)){
                    startTargetActivity(PlanActivity.class,false);
                }
                break;
            case R.id.iv_news:
                if (DateFormatUtil.avoidFastClick(1500)){
                    startTargetActivity(NewsActivity.class,false);
                }
                break;
            case R.id.iv_user_center:
                startTargetActivity(UserInfoActivity.class,false);
                break;
            case R.id.iv_setting:
                startTargetActivity(SettingActivity.class,false);
                break;
        }
    }
    private void updateTime() {
        Calendar cal = Calendar.getInstance();
        String date = DateFormatUtil.getDate2String(System.currentTimeMillis(),"yyyy/MM/dd");
        String time = DateFormatUtil.getDate2String(System.currentTimeMillis(),"HH : mm");
        tvDate.setText(date);
        tvTime.setText(time);
        if (cal.get(Calendar.AM_PM) == Calendar.AM) {
            tvAmPm.setText("AM");
        } else {
            tvAmPm.setText("PM");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        TrainPlanManager.getInstance().refreshPlanStatus(SPHelper.getUserId());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://收缩到0.8(正常值是1)，速度200
                v.animate().scaleX(0.8f).scaleY(0.8f).setDuration(200).start();
                touchFlag = false;
                break;
            case MotionEvent.ACTION_UP:
                v.animate().scaleX(1).scaleY(1).setDuration(200).start();
                if (touchFlag) return true;
                break;
        }
        return false;
    }
    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
