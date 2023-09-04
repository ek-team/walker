package com.pharosmed.walker.ui;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pharosmed.walker.R;
import com.pharosmed.walker.bluetooth.BluetoothController;
import com.pharosmed.walker.utils.AppUtils;
import com.pharosmed.walker.utils.DateFormatUtil;
import com.pharosmed.walker.utils.SPHelper;
import com.pharosmed.walker.utils.SqlToExcleUtil;
import com.pharosmed.walker.utils.ToastUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglun on 2021/6/1
 * Describe:
 */
public class EngineerActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_version_build_name)
    TextView tvVersionBuildName;
    @BindView(R.id.btn_wifi)
    TextView btnWifi;
    @BindView(R.id.btn_mode_select)
    TextView btnModeSelect;

    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        tvVersionBuildName.setText(AppUtils.getAppVersionName());
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_engineer;
    }


    @OnClick({R.id.iv_back, R.id.tv_version_build_name, R.id.btn_wifi, R.id.btn_mode_select, R.id.btn_save_record})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_version_build_name:
                break;
            case R.id.btn_wifi:
                if (DateFormatUtil.avoidFastClick(2000)) {
                    BluetoothController.getInstance().initBle();
                    Toast.makeText(this, "初始化成功", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_mode_select:
//                startTargetActivity(ModeSelectActivity.class, true);
                break;
            case R.id.btn_save_record:
                if (DateFormatUtil.avoidFastClick(2000)){
                    String path = Environment.getExternalStorageDirectory() + File.separator  + SPHelper.getUserName() +"_"+ SPHelper.getUserId() + "训练记录.xls";
                    new SqlToExcleUtil().onUserTrainRecord(path);
                    ToastUtils.showShort("生成成功");
                }
                break;
        }
    }
}
