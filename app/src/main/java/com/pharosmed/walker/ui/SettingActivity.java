package com.pharosmed.walker.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.ListPopupWindow;
import androidx.appcompat.widget.SwitchCompat;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.iflytek.cloud.msc.util.AppInfoUtil;
import com.pharosmed.walker.R;
import com.pharosmed.walker.beans.Battery;
import com.pharosmed.walker.beans.BleBean;
import com.pharosmed.walker.bluetooth.BluetoothController;
import com.pharosmed.walker.constants.AppKeyManager;
import com.pharosmed.walker.constants.Global;
import com.pharosmed.walker.constants.MessageEvent;
import com.pharosmed.walker.customview.popupdialog.PopupSheet;
import com.pharosmed.walker.customview.popupdialog.PopupSheetCallback;
import com.pharosmed.walker.utils.AppUtils;
import com.pharosmed.walker.utils.DateFormatUtil;
import com.pharosmed.walker.utils.DimensUtil;
import com.pharosmed.walker.utils.SPHelper;
import com.pharosmed.walker.utils.SPUtils;
import com.pharosmed.walker.utils.SpeechUtil;
import com.pharosmed.walker.utils.SqlToExcleUtil;
import com.pharosmed.walker.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglun on 2021/5/12
 * Describe:
 */
public class SettingActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.btn_mode_select)
    TextView btnModeSelect;
    @BindView(R.id.tab)
    SegmentTabLayout tab;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.btn_activation_code)
    TextView btnActivationCode;
    @BindView(R.id.switch_voice)
    SwitchCompat switchVoice;
    @BindView(R.id.tv_music)
    TextView tvMusic;
    @BindView(R.id.layout_music)
    RelativeLayout layoutMusic;
    @BindView(R.id.seekbar_volume)
    SeekBar seekbarVolume;
    @BindView(R.id.seekbar_brightness)
    SeekBar seekbarBrightness;
    @BindView(R.id.tv_version_name)
    TextView tvVersionName;
    @BindView(R.id.tv_update)
    TextView tvUpdate;
    @BindView(R.id.layout_system_setting)
    LinearLayout layoutSystemSetting;
    @BindView(R.id.tv_device_name)
    TextView tvDeviceName;
    @BindView(R.id.btn_clear)
    TextView btnClear;
    @BindView(R.id.tv_connect_status)
    TextView tvConnectStatus;
    @BindView(R.id.iv_disconnect)
    ImageView ivDisconnect;
    @BindView(R.id.tv_current_battery)
    TextView tvCurrentBattery;
    @BindView(R.id.tv_device_mac)
    TextView tvDeviceMac;
    @BindView(R.id.layout_rehabilitation_shoes)
    LinearLayout layoutRehabilitationShoes;
    private List<String> musicDatas;
    private AudioManager mAudioManager;
    private static String TAG = "SettingActivity";
    private static final int REQUEST_CODE_WRITE_SETTINGS = 2;
    private int musicPosition = 0;
    private String selectMusic = "卡农";
    private static int ClickCount = 0;
    private static long ClickTime = 0;
    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        initVolume();
        initLight();
        initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);
            }
        }

    }

    private void initView() {
        String[] musics = getResources().getStringArray(R.array.music_list);
        musicDatas = Arrays.asList(musics);
        tvMusic.setText(musicDatas.get(SPHelper.getMusicPosition()));
        tvVersionName.setText(MessageFormat.format("V{0}", AppUtils.getAppVersionName()));
        String[] mTitles = {getResources().getString(R.string.rehabilitation_shoes),getResources().getString(R.string.system_setting)};
        tab.setTabData(mTitles);
        tab.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                switch (position) {
                    case 1:
                        layoutRehabilitationShoes.setVisibility(View.GONE);
                        layoutSystemSetting.setVisibility(View.VISIBLE);
                        break;
                    case 0:
                        layoutRehabilitationShoes.setVisibility(View.VISIBLE);
                        layoutSystemSetting.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        switchVoice.setChecked(SPHelper.getVoiceState());
        switchVoice.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SPHelper.saveVoiceState(isChecked);
            if (isChecked){
                SpeechUtil.getInstance(SettingActivity.this).speak(getString(R.string.voice_text_function_on));
            }else {
                Toast.makeText(getApplicationContext(), getString(R.string.voice_text_function_off), Toast.LENGTH_SHORT).show();
                SpeechUtil.getInstance(SettingActivity.this).closeSpeak();
            }

        });
        seekbarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 设置音量
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                SPUtils.getInstance().put("system_voice",progress);
            }
        });
        // 调节亮度
        seekbarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setBrightness(SettingActivity.this, progress);
            }
        });
        tvDate.setText("无");
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_setting;
    }


    @OnClick({R.id.iv_back, R.id.tv_update, R.id.btn_clear,R.id.btn_select, R.id.layout_music, R.id.tv_version_name})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_update:
                break;
            case R.id.btn_clear:
                if (Global.isConnected){
                    String message = "set0";
                    BluetoothController.getInstance().writeRXCharacteristic(Global.ConnectedAddress,message.getBytes(StandardCharsets.UTF_8));
                    Toast.makeText(this, "命令已发送", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "设备未连接", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_select:
                if (DateFormatUtil.avoidFastClick(1000)){
                    Bundle bundle = new Bundle();
                    bundle.putInt(AppKeyManager.EXTRA_CONNECT_MODE,Global.ConnectSetMode);
                    startTargetActivity(bundle,ConnectDeviceActivity.class,false);
                }
                break;

            case R.id.layout_music:
                trainMusicPop();
                break;
            case R.id.tv_version_name:
                //设置在五秒内点击七次版本号会显示标定功能
                ClickCount ++;
                if (ClickCount == 1){
                    ClickTime = System.currentTimeMillis();
                }else if (ClickCount >= 7 && (System.currentTimeMillis() - ClickTime < 5000)){
                    startTargetActivity(EngineerActivity.class,false);
                    ClickTime = 0;
                    ClickCount = 0;
                }else if (ClickCount >= 7 && (System.currentTimeMillis() - ClickTime > 5000)){
                    ClickTime = 0;
                    ClickCount = 0;
                }

                break;
        }
    }
    private void trainMusicPop(){
        PopupSheet popupSheet = new PopupSheet(this, layoutMusic, musicDatas, new PopupSheetCallback() {
            @Override
            public View setupItemView(int position) {
                View itemV = LayoutInflater.from(SettingActivity.this).inflate(R.layout.item_music_dropdown, null);
                TextView titleTV = itemV.findViewById(R.id.tv_music);
                titleTV.setText(MessageFormat.format("{0}", musicDatas.get(position)));
                return itemV;
            }

            @Override
            public void itemClicked(ListPopupWindow popupWindow, int position) {
                popupWindow.dismiss();
                String musicName = musicDatas.get(position);
                selectMusic = musicName;
                musicPosition = position;
                tvMusic.setText(MessageFormat.format("{0}", musicName));
                SPHelper.saveMusicPosition(position);
            }
        }, DimensUtil.dp2px(260));
        popupSheet.show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (Global.BLE_BATTERY == 0) {
            tvCurrentBattery.setText("未获取");
        }else if (Global.BLE_BATTERY <= 20 && Global.BLE_BATTERY > 0){
            tvCurrentBattery.setText(MessageFormat.format("{0}%（请及时充电）", Global.BLE_BATTERY));
        }else {
            tvCurrentBattery.setText(MessageFormat.format("{0}%", Global.BLE_BATTERY));
        }
        refreshBleInfo(Global.isConnected);
    }
    private void refreshBleInfo(boolean isConnected){
        if (isConnected){
            tvConnectStatus.setText("已连接");
            tvDeviceName.setText(Global.ConnectedName);
            tvDeviceMac.setText(Global.ConnectedAddress);
        }else {
            tvConnectStatus.setText("未连接");
            tvDeviceName.setText("未获取");
            tvDeviceMac.setText("未获取");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        BleBean bleBean;
        switch (event.getAction()) {
            case MessageEvent.ACTION_GATT_CONNECTED:
                setPoint(true);
                refreshBleInfo(true);
                break;
            case MessageEvent.ACTION_GATT_DISCONNECTED:
                bleBean = (BleBean) event.getData();
                setPoint(false);
                refreshBleInfo(false);
                Toast.makeText(this, getString(R.string.ble_disconnect), Toast.LENGTH_SHORT).show();
                break;
            case MessageEvent.BATTERY_REFRESH:
                Battery battery = (Battery) event.getData();
                setBattery(battery.getBatteryVolume(), battery.getBatteryStatus());
                break;
            case MessageEvent.ACTION_READ_DEVICE:
                int bleBattery  = (int) event.getData();
                if (bleBattery < 20){
                    tvCurrentBattery.setText(MessageFormat.format("{0}%（请及时充电）", bleBattery));
                }else {
                    tvCurrentBattery.setText(MessageFormat.format("{0}%", bleBattery));
                }
                setTvBleBattery(bleBattery);
                break;
            default:
                break;
        }
    }
    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    private void initVolume() {
        // 获取系统最大音量
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        // 设置voice_seekbar的最大值
        seekbarVolume.setMax(maxVolume);
        // 获取到当前 设备的音量
        int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        // 显示音量
        Log.e("当前音量百分比：", currentVolume * 100 / maxVolume + " %");
        seekbarVolume.setProgress(currentVolume);
    }
    private void initLight() {
        seekbarBrightness.setMax(255);
        float currentBright = 0.0f;
        try {
            // 系统亮度值范围：0～255，应用窗口亮度范围：0.0f～1.0f。
            currentBright = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS) * 100 / 255;
            currentBright = getScreenBrightness(this);
            Log.e("当前亮度：", currentBright+"");
        } catch (Exception e) {
            Log.e(TAG, "initLight: ", e);
        }
        seekbarBrightness.setProgress((int) currentBright);
    }
    /**
     * 获取屏幕的亮度
     */
    public static int getScreenBrightness(Activity activity) {
        if (isAutoBrightness(activity)) {
            return getAutoScreenBrightness(activity);
        } else {
            return getManualScreenBrightness(activity);
        }
    }

    /**
     * 判断是否开启了自动亮度调节
     */
    public static boolean isAutoBrightness(Activity activity) {
        boolean automicBrightness = false;
        try {
            automicBrightness = Settings.System.getInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return automicBrightness;
    }

    /**
     * 获取手动模式下的屏幕亮度
     */
    public static int getManualScreenBrightness(Activity activity) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = activity.getContentResolver();
        try {
            nowBrightnessValue = android.provider.Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }

    /**
     * 设置亮度
     */
    public void setBrightness(Activity activity, int brightness) {
        try {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.screenBrightness = (float) brightness * (1f / 255f);
            int k = 0;
            k = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            Settings.System.putInt(this.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS, brightness);
            Uri uri = Settings.System.getUriFor("screen_brightness");
            this.getContentResolver().notifyChange(uri, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 获取自动模式下的屏幕亮度
     */
    public static int getAutoScreenBrightness(Activity activity) {
        float nowBrightnessValue = 0;
        ContentResolver resolver = activity.getContentResolver();
        try {
            nowBrightnessValue = android.provider.Settings.System.getFloat(resolver, "screen_auto_brightness_adj"); //[-1,1],无法直接获取到Setting中的值，以字符串表示
            Log.d(TAG, "[ouyangyj] Original AutoBrightness Value:" + nowBrightnessValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        float tempBrightness = nowBrightnessValue + 1.0f; //[0,2]
        float fValue = (tempBrightness / 2.0f) * 255.0f;
        Log.d(TAG, "[ouyangyj] Converted Value: " + fValue);
        return (int) fValue;
    }
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_WRITE_SETTINGS) {
            if (Settings.System.canWrite(this)) {
                Log.e("ERRRRRRRRRRRRR", "onActivityResult write settings granted");
            }else {
                Log.e("ERRRRRRRRRRRRR", "onActivityResult write settings not granted");
            }
        }
    }
}
