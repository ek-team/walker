package com.pharosmed.walker.ui;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.anastr.speedviewlib.SpeedView;
import com.github.anastr.speedviewlib.components.Indicators.Indicator;
import com.pharosmed.walker.MainActivity;
import com.pharosmed.walker.R;
import com.pharosmed.walker.beans.Battery;
import com.pharosmed.walker.beans.BleBean;
import com.pharosmed.walker.beans.UserBean;
import com.pharosmed.walker.bluetooth.BluetoothController;
import com.pharosmed.walker.constants.Global;
import com.pharosmed.walker.constants.MessageEvent;
import com.pharosmed.walker.customview.DoubleSlideSeekBar;
import com.pharosmed.walker.customview.WaveLoadingView;
import com.pharosmed.walker.customview.rxdialog.RxDialogSureCancel;
import com.pharosmed.walker.customview.rxdialog.RxRadioButtonDialog;
import com.pharosmed.walker.database.EvaluateManager;
import com.pharosmed.walker.database.UserManager;
import com.pharosmed.walker.utils.DataTransformUtil;
import com.pharosmed.walker.utils.MyUtil;
import com.pharosmed.walker.utils.SPHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglun on 2021/4/27
 * Describe:
 */
public class EvaluateActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.double_slide)
    DoubleSlideSeekBar doubleSlide;
    @BindView(R.id.waveLoadingView)
    WaveLoadingView waveLoadingView;
    @BindView(R.id.speedview)
    SpeedView speedview;
    @BindView(R.id.tv_vas)
    TextView tvVas;
    @BindView(R.id.tv_study_setting)
    TextView tvStudySetting;
    @BindView(R.id.img_reduce)
    ImageView imgReduce;
    @BindView(R.id.tv_result)
    TextView tvResult;
    @BindView(R.id.img_increase)
    ImageView imgIncrease;
    @BindView(R.id.btn_clear)
    TextView btnClear;
    @BindView(R.id.btn_commit)
    TextView btnCommit;
    @BindView(R.id.algorithm_spinner)
    Spinner algorithmSpinner;
    @BindView(R.id.ed_remote_calibration)
    EditText edRemoteCalibration;
    @BindView(R.id.tv_remote_calibration)
    TextView tvRemoteCalibration;
    private float weightValue = 0;
    private float saveResult = 0;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private int selectPosition = 1;
    private int vasValue = 1;
    private int mode = 0;
    private static int ClickCount = 0;
    private static long ClickTime = 0;
    List<Float> floatList = new ArrayList<>();
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                int[] list = (int[]) msg.obj;
                updateSpeedView(list[0], list[1]);
            }
        }
    };

    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initData();
        initView();
        Global.isReconnectBle = true;
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mode = bundle.getInt("TrainParam", 0);
        }
    }

    private void initView() {
        speedview.setIndicator(Indicator.Indicators.KiteIndicator);
        speedview.setIndicatorColor(Color.WHITE);
        speedview.setWithTremble(false);
        speedview.setMaxSpeed(100);
        speedview.setMinSpeed(0);
        speedview.setTickNumber(11);
        speedview.setTickPadding(36);
        doubleSlide.setOnRangeListener((low, big) -> {
            int[] list = {0, 0};
            Message msg = new Message();
            msg.what = 0;
            list[0] = Math.round(low);
            list[1] = Math.round(big);
            msg.obj = list;
            mHandler.sendMessage(msg);
        });
        tvStudySetting.setOnLongClickListener(v -> {
            RxRadioButtonDialog dialog = new RxRadioButtonDialog(EvaluateActivity.this);
            dialog.setSureListener(v1 -> {
                tvVas.setText(MessageFormat.format("VAS {0}分", dialog.selectValue));
                vasValue = dialog.selectValue;
                dialog.dismiss();
            });
            dialog.show();
            return false;
        });
        algorithmSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectPosition = 1;
                } else {
                    selectPosition = position;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void updateSpeedView(float min, float max) {
        speedview.setIndicator(Indicator.Indicators.KiteIndicator);
        speedview.setIndicatorColor(Color.WHITE);
        speedview.setWithTremble(false);
        speedview.setMaxSpeed(max);
        speedview.setMinSpeed(min);
        speedview.setTickNumber(11);
        speedview.setTickPadding(36);
        speedview.speedTo(min, 10);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        BleBean bleBean;
        switch (event.getAction()) {
            case MessageEvent.ACTION_GATT_CONNECTED:
                bleBean = (BleBean) event.getData();
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
                if (selectPosition == 2) {
                    refreshView(Float.valueOf(value));
                } else {
                    calculMinValue(Float.valueOf(value));
                }
//                refreshView(Float.valueOf(value));
                break;
            case MessageEvent.GATT_TRANSPORT_OPEN:
                startTask();
                break;
            case MessageEvent.BATTERY_REFRESH:
                Battery battery = (Battery) event.getData();
                setBattery(battery.getBatteryVolume(), battery.getBatteryStatus());
                break;
            case MessageEvent.ACTION_READ_DEVICE:
                int bleBattery = (int) event.getData();
                setTvBleBattery(bleBattery);
                break;
            default:
                break;
        }
    }

    private void refreshView(Float valueOf) {
        speedview.speedTo(valueOf, 150);
        if (valueOf < weightValue) {
            return;
        }
        floatList.add(valueOf);
        weightValue = Collections.max(floatList);
        tvResult.setText(MessageFormat.format("{0}kg", (int) weightValue));
    }

    private List<Float> valueList = new ArrayList<>();
    private List<Float> minValueList = new ArrayList<>();
    private int indexMax = 10;

    private void calculMinValue(float value) {
        speedview.speedTo(value, 150);
        if (valueList.size() < indexMax) {
            valueList.add(value);
        } else if (valueList.size() == indexMax) {
            minValueList.add(Collections.min(valueList));
            valueList.clear();
        }
        if (minValueList.size() > 0) {
            weightValue = Collections.max(minValueList).intValue();
            tvResult.setText(MessageFormat.format("{0}kg", weightValue));
        }
    }

    private void deleteMax(float value) {
        for (int i = 0; i < minValueList.size(); i++) {
            if (minValueList.get(i) >= value) {
                minValueList.set(i, value);
            }
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_evaluate;
    }

    @Override
    protected void onResume() {
        startTask();
        super.onResume();
    }

    @OnClick({R.id.iv_back, R.id.tv_study_setting, R.id.img_reduce, R.id.img_increase, R.id.btn_clear, R.id.btn_commit,R.id.tv_remote_calibration})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_study_setting:
                //设置在五秒内点击七次版本号会显示标定功能
                ClickCount ++;
                if (ClickCount == 1){
                    ClickTime = System.currentTimeMillis();
                }else if (ClickCount >= 7 && (System.currentTimeMillis() - ClickTime < 5000)){
                    tvRemoteCalibration.setVisibility(View.VISIBLE);
                    edRemoteCalibration.setVisibility(View.VISIBLE);
                    ClickTime = 0;
                    ClickCount = 0;
                }else if (ClickCount >= 7 && (System.currentTimeMillis() - ClickTime > 5000)){
                    ClickTime = 0;
                    ClickCount = 0;
                }
                break;
            case R.id.img_reduce:
                if (weightValue > 0) {
                    weightValue = weightValue - 1;
                    deleteMax(weightValue);
//                    DecimalFormat decimalFormat = new DecimalFormat("0.0");
//                    tvResult.setText(MessageFormat.format("{0}kg", decimalFormat.format(weightValue)));
                    tvResult.setText(MessageFormat.format("{0}kg", (int) weightValue));
                }
                break;
            case R.id.img_increase:
                if (weightValue < 100) {
                    weightValue = weightValue + 1;
                    if (selectPosition == 1) {
                        minValueList.add(weightValue);
                    }
                    tvResult.setText(MessageFormat.format("{0}kg", (int) weightValue));
                }
                break;
            case R.id.btn_clear:
                weightValue = 0;
                floatList.clear();
                valueList.clear();
                minValueList.clear();
                tvResult.setText(MessageFormat.format("{0}kg", (int) weightValue));
                break;
            case R.id.btn_commit:
                saveResult = weightValue;
                if (saveResult < 1){
                    Toast.makeText(this, "评估值不能为0", Toast.LENGTH_SHORT).show();
                    return;
                }
                UserBean userBean = SPHelper.getUser();
                userBean.setEvaluateWeight(saveResult);
                SPHelper.saveUser(userBean);
                UserManager.getInstance().insert(userBean, 1);
                MyUtil.insertTemplate((int) saveResult);
                if (Global.USER_MODE)
                    EvaluateManager.getInstance().insert((int) saveResult, vasValue);

                RxDialogSureCancel dialog = new RxDialogSureCancel(this);
                dialog.setContent("是否进入训练");
                dialog.setCancel("回到主界面");
                dialog.setSure("进入训练");
                dialog.setSureListener(v -> {
                    dialog.dismiss();
                    if (Global.isConnected) {
                        startTargetActivity(TrainParamActivity.class, true);
                    } else {
                        startTargetActivity(ConnectDeviceActivity.class, true);
                    }
                });
                dialog.setCancelListener(v -> {
                    floatList.clear();
                    valueList.clear();
                    minValueList.clear();
                    weightValue = 0;
                    dialog.dismiss();
                    startTargetActivity(MainActivity.class, true);
                });
                dialog.show();
                break;
            case R.id.tv_remote_calibration:
                String message;
                if (TextUtils.isEmpty(edRemoteCalibration.getText().toString())){
                    message = "set0";
                }else {
                    message = "set" + edRemoteCalibration.getText().toString();
                }
                BluetoothController.getInstance().writeRXCharacteristic(Global.ConnectedAddress,message.getBytes(StandardCharsets.UTF_8));
                Toast.makeText(this, "命令已发送", Toast.LENGTH_SHORT).show();
                tvRemoteCalibration.setVisibility(View.GONE);
                edRemoteCalibration.setVisibility(View.GONE);
                break;
        }
    }

    private void startTask() {
        if (mTimer == null && mTimerTask == null) {
            mTimer = new Timer();
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    int a = 0x1A;
                    int b = 0x04 | 0x10;
                    int c = 0x00;
                    int d = 0xFF - (a + b + c) + 1;
                    String message = ":1A" + DataTransformUtil.toHexString((byte) b) + "00" + DataTransformUtil.toHexString((byte) d);
                    BluetoothController.getInstance().writeRXCharacteristic(Global.ConnectedAddress, message.getBytes(StandardCharsets.UTF_8));
                }
            };
            mTimer.schedule(mTimerTask, 0, 1000);
        }
    }

    private void clearTimerTask() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimerTask.cancel();
            mTimer = null;
            mTimerTask = null;
        }
    }

    @Override
    protected void onDestroy() {
        Global.isReconnectBle = false;
        EventBus.getDefault().unregister(this);
        clearTimerTask();
        super.onDestroy();
    }

}
