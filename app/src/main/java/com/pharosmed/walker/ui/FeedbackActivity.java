package com.pharosmed.walker.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatRatingBar;

import com.pharosmed.walker.beans.Battery;
import com.pharosmed.walker.beans.BleBean;
import com.pharosmed.walker.MainActivity;
import com.pharosmed.walker.R;
import com.pharosmed.walker.beans.UserBean;
import com.pharosmed.walker.beans.UserTrainRecordEntity;
import com.pharosmed.walker.constants.AppKeyManager;
import com.pharosmed.walker.constants.Global;
import com.pharosmed.walker.constants.MessageEvent;
import com.pharosmed.walker.customview.bubble.BubbleSeekBar;
import com.pharosmed.walker.database.UserTrainRecordManager;
import com.pharosmed.walker.utils.DateFormatUtil;
import com.pharosmed.walker.utils.SPHelper;
import com.pharosmed.walker.utils.SqlToExcleUtil;
import com.pharosmed.walker.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglun on 2021/4/21
 * Describe:
 */
public class FeedbackActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.ratingbar_score)
    AppCompatRatingBar ratingbarScore;
    @BindView(R.id.seek_bar)
    BubbleSeekBar seekBar;
    @BindView(R.id.chk_adverse_reactions1)
    CheckBox chkAdverseReactions1;
    @BindView(R.id.chk_adverse_reactions2)
    CheckBox chkAdverseReactions2;
    @BindView(R.id.chk_adverse_reactions3)
    CheckBox chkAdverseReactions3;
    @BindView(R.id.chk_adverse_reactions4)
    CheckBox chkAdverseReactions4;
    @BindView(R.id.tv_warning)
    TextView tvWarning;
    @BindView(R.id.tv_commit)
    TextView tvCommit;
    // 疼痛反馈（0到10,0到3为轻度疼痛，4到7为中度疼痛，8到10为重度疼痛）
    private int painFeedbackLevel = 0;
    private int trainScore;
    private int trainTime;
    private int effectiveTime;
    private int warningTime;
    private int loadWeight;
    private String adverseReactions = "";
    private String adverseReactions1 = "";
    private String adverseReactions2 = "";
    private String adverseReactions3 = "";
    private UserBean userBean;
    private boolean isSave = false;
    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initData();
        initView();

    }

    private void initData() {
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            trainScore = bundle.getInt(AppKeyManager.EXTRA_SCORE, 1);
            // 0分，也显示1颗星
            if (trainScore == 0) {
                trainScore = 1;
            }
            if (trainScore > 5){
                trainScore = 5;
            }
            ratingbarScore.setRating(trainScore);
            trainTime = bundle.getInt(AppKeyManager.EXTRA_TRAIN_TIME,0);
            loadWeight = bundle.getInt(AppKeyManager.EXTRA_WEIGHT,0);
            effectiveTime = bundle.getInt(AppKeyManager.EXTRA_EFFECTIVE_TIME,0);
            warningTime = bundle.getInt(AppKeyManager.EXTRA_NOTE_ERRORNUMBER,0);
        }
        userBean = SPHelper.getUser();
    }

    private void initView() {
        chkAdverseReactions1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                adverseReactions1 =  chkAdverseReactions1.getText().toString() + ",";
                tvWarning.setText(getString(R.string.text_feedback_tips));
                chkAdverseReactions4.setChecked(false);
            }else {
                adverseReactions1 = "";
            }
        });

        chkAdverseReactions2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                adverseReactions2 = chkAdverseReactions2.getText().toString()+ ",";
                tvWarning.setText(getString(R.string.text_feedback_tips));
                chkAdverseReactions4.setChecked(false);
            }else {
                adverseReactions2 = "";
            }
        });

        chkAdverseReactions3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                adverseReactions3 = chkAdverseReactions3.getText().toString()+ ",";
                tvWarning.setText(getString(R.string.text_feedback_tips));
                chkAdverseReactions4.setChecked(false);
            }else {
                adverseReactions3 = "";
            }
        });

        chkAdverseReactions4.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                chkAdverseReactions1.setChecked(false);
                chkAdverseReactions2.setChecked(false);
                chkAdverseReactions3.setChecked(false);
                tvWarning.setText(getString(R.string.feedback_tip_1));
            }
        });
        seekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                painFeedbackLevel = progress;
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        BleBean bleBean;
        switch (event.getAction()) {
            case MessageEvent.ACTION_GATT_CONNECTED:
                setPoint(true);
                break;
            case MessageEvent.ACTION_GATT_DISCONNECTED:
                bleBean = (BleBean) event.getData();
                setPoint(false);
                Toast.makeText(this, getString(R.string.ble_disconnect), Toast.LENGTH_SHORT).show();
                break;
            case MessageEvent.BATTERY_REFRESH:
                Battery battery = (Battery) event.getData();
                setBattery(battery.getBatteryVolume(), battery.getBatteryStatus());
                break;
            case MessageEvent.ACTION_SAVE_TIP:
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_feedback;
    }


    @OnClick({R.id.iv_back, R.id.tv_commit, R.id.btn_save_record})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                startTargetActivity(MainActivity.class,true);
                break;
            case R.id.tv_commit:
                if (!Global.USER_MODE){//访客模式下不记录数据
                    startTargetActivity(MainActivity.class,true);
                    return;
                }
                if (!isSave){
                    saveData();
                }
                startTargetActivity(MainActivity.class,true);
                break;
            case R.id.btn_save_record:
                if (DateFormatUtil.avoidFastClick(2000)){
                    showWaiting();
                    if (!isSave){
                        saveData();
                    }
                    String path = Environment.getExternalStorageDirectory() + File.separator  + SPHelper.getUserName() +"_"+ SPHelper.getUserId() + "训练记录.xls";
                    new Thread(){
                        @Override
                        public void run() {
                            new SqlToExcleUtil().onUserTrainRecord(path);
                            EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SAVE_TIP));
                        }
                    }.start();


                }
                break;
        }
    }
    private void saveData(){
        UserTrainRecordEntity trainRecordEntity = new UserTrainRecordEntity();
        trainRecordEntity.setScore(trainScore);
        trainRecordEntity.setWarningTime(warningTime);
        trainRecordEntity.setDiagnostic(userBean.getDiagnosis());
        trainRecordEntity.setTargetLoad(loadWeight);
        trainRecordEntity.setSuccessTime(effectiveTime);
        trainRecordEntity.setPainLevel(painFeedbackLevel);
//                if(painFeedbackLevel >= 0 && painFeedbackLevel <= 3){
//                    trainRecordEntity.setPainLevel("轻度疼痛");
//                }else if(painFeedbackLevel >= 4 && painFeedbackLevel <= 7){
//                    trainRecordEntity.setPainLevel("中度疼痛");
//                }else if(painFeedbackLevel >= 8 && painFeedbackLevel <= 10){
//                    trainRecordEntity.setPainLevel("重度疼痛");
//                }else {
//                    trainRecordEntity.setPainLevel("无反馈");
//                }
        adverseReactions = adverseReactions1 + adverseReactions2 + adverseReactions3;
        trainRecordEntity.setAdverseReactions(adverseReactions);
        trainRecordEntity.setTrainTime(trainTime);
        trainRecordEntity.setUserId(SPHelper.getUserId());
        UserTrainRecordManager.getInstance().insert(trainRecordEntity);
        isSave = true;
    }
    private ProgressDialog progressDialog;
    /**
     * 圆圈加载进度的 dialog
     */
    private void showWaiting() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.mipmap.ic_launcher);
        progressDialog.setTitle("加载dialog");
        progressDialog.setMessage("正在保存...");
        progressDialog.setIndeterminate(true);// 是否形成一个加载动画  true表示不明确加载进度形成转圈动画  false 表示明确加载进度
        progressDialog.setCancelable(false);//点击返回键或者dialog四周是否关闭dialog  true表示可以关闭 false表示不可关闭
        progressDialog.show();
    }
    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
