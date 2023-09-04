package com.pharosmed.walker.ui;

import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.anastr.speedviewlib.SpeedView;
import com.github.anastr.speedviewlib.components.Indicators.Indicator;
import com.pharosmed.walker.beans.Battery;
import com.pharosmed.walker.beans.BleBean;
import com.pharosmed.walker.R;
import com.pharosmed.walker.beans.TrainDataEntity;
import com.pharosmed.walker.bluetooth.BluetoothController;
import com.pharosmed.walker.constants.AppKeyManager;
import com.pharosmed.walker.constants.Global;
import com.pharosmed.walker.constants.MessageEvent;
import com.pharosmed.walker.customview.VerticalProgressBar;
import com.pharosmed.walker.customview.WaveLoadingView;
import com.pharosmed.walker.customview.rxdialog.RxDialogSureCancel;
import com.pharosmed.walker.database.TrainDataManager;
import com.pharosmed.walker.greendao.TrainDataEntityDao;
import com.pharosmed.walker.utils.DataTransformUtil;
import com.pharosmed.walker.utils.DateFormatUtil;
import com.pharosmed.walker.utils.OneShotUtil;
import com.pharosmed.walker.utils.SPHelper;
import com.pharosmed.walker.utils.SpeechUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglun on 2021/4/14
 * Describe:
 */
public class TrainingActivity extends BaseActivity {
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
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.waveLoadingView)
    WaveLoadingView waveLoadingView;
    @BindView(R.id.speedview)
    SpeedView speedview;
    @BindView(R.id.vp_progress)
    VerticalProgressBar vpProgress;
    @BindView(R.id.tv_effective_time)
    TextView tvEffectiveTime;
    @BindView(R.id.tv_total_time)
    TextView tvTotalTime;
    @BindView(R.id.layout_time)
    LinearLayout layoutTime;
    @BindView(R.id.tv_warning_time)
    TextView tvWarningTime;
    @BindView(R.id.layout_warning_time)
    LinearLayout layoutWarningTime;
    @BindView(R.id.iv_wifi_setting)
    ImageView ivWifiSetting;
    @BindView(R.id.iv_video_game)
    ImageView ivVideoGame;
    @BindView(R.id.iv_video_game_off)
    ImageView ivVideoGameOff;
    @BindView(R.id.tv_stop)
    TextView tvStop;
    @BindView(R.id.tv_train_time)
    TextView tvTrainTime;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private MediaPlayer mediaPlayer;
    private int count = 0;
    private int warningcount = 0;
    private int totalTrainingTime = 0;
    private int totalCount = 0;
    private int trainingTime = 0;
    private float weight = 0;
    private float minWeight;
    private float maxWeight;
    private int planid = 0;
    private int level = 1;
    private int planTrainNum = 10;
    private int musicPosition;
    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initData();
        initView();
        SPHelper.saveRebootTime(0);

    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            trainingTime = bundle.getInt(AppKeyManager.EXTRA_TIMENUM,0);
            totalTrainingTime = trainingTime * 60;
            totalCount = totalTrainingTime;
            weight = bundle.getInt(AppKeyManager.EXTRA_WEIGHT,0);
            musicPosition = bundle.getInt(AppKeyManager.EXTRA_MUSIC_FILE,0);
        }
        mediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor fd = getAssets().openFd(musicPosition + ".mp3");
            mediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Global.isReconnectBle = true;
    }

    private void initView() {
        minWeight = weight * 0.8f;
        maxWeight = weight * 1.2f;
        speedview.setIndicator(Indicator.Indicators.KiteIndicator);
        speedview.setIndicatorColor(Color.WHITE);
        speedview.setLowSpeedPercent(40);
        speedview.setMediumSpeedPercent(60);
        speedview.setWithTremble(false);
        speedview.setMaxSpeed(weight * 2);
        speedview.setTickNumber(11);
        speedview.setTickPadding(36);
//        planid = b.getInt(AppKeyManager.EXTRA_PLANID, 0);
//        trainingTime = b.getInt(AppKeyManager.EXTRA_TREAD_NUM, 0);
        tvTotalTime.setText(MessageFormat.format("/{0}次", totalTrainingTime));
        vpProgress.setMaxProgress(totalTrainingTime * 1.0f);

    }
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_training;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTask();
        countDownThread();
//        OneShotUtil.getInstance(this).startOneShot();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        BleBean bleBean;
        switch (event.getAction()) {
            case MessageEvent.ACTION_GATT_CONNECTED:
                bleBean = (BleBean) event.getData();
                countDownThread();
                setPoint(true);
                break;
            case MessageEvent.ACTION_GATT_DISCONNECTED:
                bleBean = (BleBean) event.getData();
                clearTimerTask();
                setPoint(false);
                Toast.makeText(this, getString(R.string.ble_disconnect), Toast.LENGTH_SHORT).show();
                break;
            case MessageEvent.ACTION_READ_DATA:
                bleBean = (BleBean) event.getData();
                String value = bleBean.getData();
                refreshView(Float.valueOf(value));
                break;
            case MessageEvent.GATT_TRANSPORT_OPEN:
                startTask();
                break;
            case MessageEvent.BATTERY_REFRESH:
                Battery battery = (Battery) event.getData();
                setBattery(battery.getBatteryVolume(),battery.getBatteryStatus());
                break;
            case MessageEvent.ACTION_READ_DEVICE:
                int bleBattery  = (int) event.getData();
                setTvBleBattery(bleBattery);
                break;
            case MessageEvent.ACTION_COUNTDOWN:
                int countTime = (int) event.getData();
                tvTrainTime.setText(DateFormatUtil.getMinuteTime(countTime));
                vpProgress.setProgress(totalCount - countTime);//设置进度条
                break;
            default:
                break;
        }
    }
    float currentMaxweight;
    boolean isEffect = false;
    boolean isWarning = false;
    boolean isDidNotMakeIt = false;//判断最大值是否在黄色范围，便于记录未达标的数据

    private void refreshView(Float value){
        if (value > weight * 2) {
            speedview.speedTo(weight * 2, 150);
        } else if (value > 0 && value < 0.5f){
            value = 0.0f;
            speedview.speedTo(value, 150);
        }else {
            speedview.speedTo(value, 150);
        }
        // 计算压力最高值并保存
        if (currentMaxweight < value) {
            currentMaxweight = value;
        }
        if (currentMaxweight >= minWeight && currentMaxweight < maxWeight){
            if (!isWarning){
                isEffect = true;
                isDidNotMakeIt = false;
            }
        }else if (currentMaxweight >= maxWeight){
            isWarning = true;
            isEffect = false;
            isDidNotMakeIt = false;
        }else if (currentMaxweight < minWeight && currentMaxweight > weight * 0.4){
            if (!isWarning && !isEffect){
                isDidNotMakeIt = true;
            }
        }

        if (value <= weight * 0.4 && (isDidNotMakeIt || isEffect || isWarning)){
            insertData(currentMaxweight,weight);
            currentMaxweight = 0;
            if (isWarning){
                warningcount++;
                tvWarningTime.setText(MessageFormat.format("{0}", warningcount));
            }
            if (isEffect){
                SpeechUtil.getInstance(this).speak("完成一次");
                count++;
                tvEffectiveTime.setText(MessageFormat.format("{0}", count));
            }
            isEffect = false;
            isWarning = false;
            isDidNotMakeIt = false;
        }
        if (value >= maxWeight) {
            SpeechUtil.getInstance(this).speak("太用力了");
        }

    }
    private void insertData(float realLoad,float targetLoad){
        if (!Global.USER_MODE)//访客模式下不记录数据
            return;
        TrainDataEntity entity = new TrainDataEntity();
        entity.setRealLoad((int) realLoad);
        entity.setTargetLoad((int) targetLoad);
        TrainDataManager.getInstance().insert(entity);
    }
    private void startTask(){
        if (mTimer == null && mTimerTask == null){
            mTimer = new Timer();
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    int a = 0x1A;
                    int b = 0x04 | 0x10;
                    int c = 0x00;
                    int d = 0xFF - (a + b + c) + 1;
                    String message = ":1A" + DataTransformUtil.toHexString((byte) b) + "00" + DataTransformUtil.toHexString((byte) d);
                    Log.e("发送数据", "run: "  +message );
                    BluetoothController.getInstance().writeRXCharacteristic(Global.ConnectedAddress,message.getBytes(StandardCharsets.UTF_8));
                }
            };
            mTimer.schedule(mTimerTask, 0, 1000);
        }
    }
    private Timer mTimer1;
    private TimerTask mTimerTask1;
    private void countDownThread(){
        if (mTimer1 == null && mTimerTask1 == null) {
            mTimer1 = new Timer();
            mTimerTask1 = new TimerTask() {
                @Override
                public void run() {
                    totalTrainingTime--;
                    if (totalTrainingTime <= 0){
                        mTimer1.cancel();
                        mTimerTask1.cancel();
                        startFeedback();
                    }
                    EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_COUNTDOWN,totalTrainingTime));
                }
            };
            mTimer1.schedule(mTimerTask1, 0, 1000);
        }
    }
    private void clearTimerTask(){
        if (mTimer != null){
            mTimer.cancel();
            mTimerTask.cancel();
            mTimer = null;
            mTimerTask = null;
        }
        if (mTimer1 != null){
            mTimer1.cancel();
            mTimerTask1.cancel();
            mTimer1 = null;
            mTimerTask1 = null;
        }
    }
    @OnClick({R.id.iv_back, R.id.iv_wifi_setting, R.id.iv_video_game, R.id.iv_video_game_off, R.id.tv_stop})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_wifi_setting:
                break;
            case R.id.iv_video_game:
                break;
            case R.id.iv_video_game_off:
                break;
            case R.id.tv_stop:
            case R.id.iv_back:
                RxDialogSureCancel dialog = new RxDialogSureCancel(this);
                dialog.setContent("是否退出训练？");
                dialog.setSureListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startFeedback();
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
        }
    }

    private void startFeedback(){
        Bundle b = new Bundle();
        if (warningcount == 0 && count >= 20) {
            b.putInt(AppKeyManager.EXTRA_SCORE, 5);
        } else if (count == 0) {
            b.putInt(AppKeyManager.EXTRA_SCORE, 0);
        } else {
            b.putInt(AppKeyManager.EXTRA_SCORE, ((count / 20)) * 5);
        }
        b.putInt(AppKeyManager.EXTRA_LEVEL, level);
        b.putInt(AppKeyManager.EXTRA_TRAIN_TIME, trainingTime);
        b.putInt(AppKeyManager.EXTRA_EFFECTIVE_TIME, count);
        b.putInt(AppKeyManager.EXTRA_NOTE_ERRORNUMBER, warningcount);
        b.putInt(AppKeyManager.EXTRA_WEIGHT, (int) weight);
        startTargetActivity(b,FeedbackActivity.class,true);
        SpeechUtil.getInstance(this).speak("疼痛反馈");
    }
    @Override
    protected void onStop() {
        Global.isReconnectBle = false;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        OneShotUtil.getInstance(this).stopOneShot();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        clearTimerTask();
        super.onDestroy();
    }
}
