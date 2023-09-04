package com.pharosmed.walker.ui;

import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.ListPopupWindow;

import com.google.android.exoplayer2.ui.PlayerView;
import com.pharosmed.walker.MainActivity;
import com.pharosmed.walker.R;
import com.pharosmed.walker.constants.AppKeyManager;
import com.pharosmed.walker.constants.Global;
import com.pharosmed.walker.customview.popupdialog.PopupSheet;
import com.pharosmed.walker.customview.popupdialog.PopupSheetCallback;
import com.pharosmed.walker.database.SubPlanManager;
import com.pharosmed.walker.database.TrainPlanManager;
import com.pharosmed.walker.utils.DateFormatUtil;
import com.pharosmed.walker.utils.DimensUtil;
import com.pharosmed.walker.utils.SPHelper;
import com.pharosmed.walker.utils.SPUtils;
import com.pharosmed.walker.utils.ToastUtils;
import com.pharosmed.walker.utils.VideoPlayUtil;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglun on 2021/4/15
 * Describe:
 */
public class TrainParamActivity extends BaseActivity {
    @BindView(R.id.player_view)
    PlayerView playerView;
    @BindView(R.id.iv_close)
    ImageView ivClose;
    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.tv_tread_num)
    TextView tvTreadNum;
    @BindView(R.id.layout_tread_num)
    RelativeLayout layoutTreadNum;
    @BindView(R.id.tv_weight)
    TextView tvWeight;
    @BindView(R.id.layout_weight)
    RelativeLayout layoutWeight;
    @BindView(R.id.tv_music)
    TextView tvMusic;
    @BindView(R.id.layout_music)
    RelativeLayout layoutMusic;
    @BindView(R.id.tv_previous)
    TextView tvPrevious;
    @BindView(R.id.tv_start)
    TextView tvStart;
    @BindView(R.id.layout_main)
    RelativeLayout layoutMain;
    @BindView(R.id.seekbar_volume)
    SeekBar seekbarVolume;
    private String fileName = "file:///android_asset/video_connect_2.mp4";
    private List<String> musicData;
    private PopupSheet popupSheet;
    private int weight = 30;
    private int planid;
    private int level;
    private int trainTime = 5;
    private int musicPosition = 0;
    private String selectMusic = "卡农";
    private AudioManager mAudioManager;
    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
    }

    private void initData() {
        if (Global.USER_MODE) {
            weight = SubPlanManager.getInstance().getThisWeekLoad(SPHelper.getUserId());
            trainTime = TrainPlanManager.getInstance().refreshPlanStatus(SPHelper.getUserId());
        }
        weight = (int) SPHelper.getUserEvaluateWeight();
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        initVolume();
    }

    private void initView() {
        String[] musics = getResources().getStringArray(R.array.music_list);
        musicData = Arrays.asList(musics);
        tvMusic.setText(musicData.get(SPHelper.getMusicPosition()));
        tvTreadNum.setText(MessageFormat.format("{0}", trainTime));
        tvWeight.setText(MessageFormat.format("{0}", weight));
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
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_train_param;
    }

    @Override
    protected void onResume() {
        VideoPlayUtil.getInstance().setVideoPlayer(this, fileName, playerView);
        VideoPlayUtil.getInstance().startPlayer();
        initData();
        initView();
        super.onResume();
    }

    @OnClick({R.id.iv_close, R.id.tv_previous, R.id.tv_start, R.id.layout_tread_num, R.id.layout_weight, R.id.layout_music, R.id.btn_go_evaluate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                startTargetActivity(MainActivity.class, true);
                break;
            case R.id.tv_previous:
                if (DateFormatUtil.avoidFastClick(1000)) {
                    startTargetActivity(ConnectDeviceActivity.class, false);
                }
                break;
            case R.id.tv_start:
                if (weight <= 0) {
                    ToastUtils.showShort("训练重量必须大于0kg");
                    return;
                }
                if (trainTime <= 0) {
                    ToastUtils.showShort("训练时间必须大于0分钟");
                    return;
                }
                if (DateFormatUtil.avoidFastClick2(1000)) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(AppKeyManager.EXTRA_WEIGHT, weight);
                    bundle.putInt(AppKeyManager.EXTRA_TIMENUM, trainTime);
                    bundle.putInt(AppKeyManager.EXTRA_MUSIC_FILE, musicPosition);
                    bundle.putString(AppKeyManager.EXTRA_MUSIC_NAME, selectMusic);
                    bundle.putInt(AppKeyManager.EXTRA_CONNECT_MODE, Global.ConnectMainMode);
                    startTargetActivity(bundle, StartActivity.class, true);
                }
                break;
            case R.id.layout_tread_num:
                trainTimesPop();
                break;
            case R.id.layout_weight:
                trainWeightPop();
                break;
            case R.id.layout_music:
                trainMusicPop();
                break;
            case R.id.btn_go_evaluate:
                Bundle bundle = new Bundle();
                bundle.putInt("TrainParam", 1);
                bundle.putInt(AppKeyManager.EXTRA_CONNECT_MODE, Global.ConnectEvaluateMode);
                startTargetActivity(bundle, StartActivity.class, false);
                break;
        }
    }

    private void trainTimesPop() {
        final List<Integer> timeData = new ArrayList<>();
        int minTrainingTime = 5;
        int maxTrainingTime = 60;
        for (int i = minTrainingTime; i <= maxTrainingTime; i++) {
            timeData.add(i);
        }
        popupSheet = new PopupSheet(this, layoutTreadNum, timeData, new PopupSheetCallback() {
            @Override
            public View setupItemView(int position) {
                View itemV = LayoutInflater.from(TrainParamActivity.this).inflate(R.layout.item_music_dropdown, null);
                TextView titleTV = itemV.findViewById(R.id.tv_music);
                titleTV.setText(MessageFormat.format("{0}", timeData.get(position)));
                return itemV;
            }

            @Override
            public void itemClicked(ListPopupWindow popupWindow, int position) {
                popupWindow.dismiss();
                int data = timeData.get(position);
                trainTime = data;
                tvTreadNum.setText(MessageFormat.format("{0}", data));
            }
        }, DimensUtil.dp2px(260));
        popupSheet.show();
    }

    private void trainWeightPop() {
        final List<Integer> weightData = new ArrayList<>();
        int minWeight = 2;
        int maxminWeight = 100;
        for (int i = minWeight; i <= maxminWeight; i++) {
            weightData.add(i);
        }
        popupSheet = new PopupSheet(this, layoutTreadNum, weightData, new PopupSheetCallback() {
            @Override
            public View setupItemView(int position) {
                View itemV = LayoutInflater.from(TrainParamActivity.this).inflate(R.layout.item_music_dropdown, null);
                TextView titleTV = itemV.findViewById(R.id.tv_music);
                titleTV.setText(MessageFormat.format("{0}", weightData.get(position)));
                return itemV;
            }

            @Override
            public void itemClicked(ListPopupWindow popupWindow, int position) {
                popupWindow.dismiss();
                int data = weightData.get(position);
                weight = data;
                tvWeight.setText(MessageFormat.format("{0}", data));
            }
        }, DimensUtil.dp2px(260));
        popupSheet.show();
    }

    private void trainMusicPop() {
        popupSheet = new PopupSheet(this, layoutTreadNum, musicData, new PopupSheetCallback() {
            @Override
            public View setupItemView(int position) {
                View itemV = LayoutInflater.from(TrainParamActivity.this).inflate(R.layout.item_music_dropdown, null);
                TextView titleTV = itemV.findViewById(R.id.tv_music);
                titleTV.setText(MessageFormat.format("{0}", musicData.get(position)));
                return itemV;
            }

            @Override
            public void itemClicked(ListPopupWindow popupWindow, int position) {
                popupWindow.dismiss();
                String musicName = musicData.get(position);
                selectMusic = musicName;
                musicPosition = position;
                tvMusic.setText(MessageFormat.format("{0}", musicName));
            }
        }, DimensUtil.dp2px(260));
        popupSheet.show();
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

    @Override
    protected void onPause() {
        VideoPlayUtil.getInstance().stopPlayer();
        VideoPlayUtil.getInstance().destroyPlayer();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
