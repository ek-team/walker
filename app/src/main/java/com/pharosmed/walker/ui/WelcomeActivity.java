package com.pharosmed.walker.ui;

import android.os.Bundle;
import android.os.Handler;

import com.pharosmed.walker.MainActivity;
import com.pharosmed.walker.R;
import com.pharosmed.walker.bluetooth.BluetoothController;
import com.pharosmed.walker.constants.Global;
import com.pharosmed.walker.database.UserManager;
import com.pharosmed.walker.services.ForegroundWorkService;
import com.pharosmed.walker.utils.OneShotUtil;
import com.pharosmed.walker.utils.SPHelper;


/**
 * Created by zhanglun on 2020/6/4
 * Describe:
 */
public class WelcomeActivity extends BaseActivity {

    private static final long DELAY_TIME = 1000;
    private Handler mHandler = new Handler();
    @Override
    protected void initialize(Bundle savedInstanceState) {
        ForegroundWorkService.launch();
        BluetoothController.getInstance().initBle();
        UserManager mUserManager = UserManager.getInstance();
        if (SPHelper.getUser().getId() <= 0){
            SPHelper.saveUser(mUserManager.initUser(0L));//创建初始用户，并保存到本地
            Global.USER_MODE = false;
        }
        mHandler.postDelayed(() -> startTargetActivity(MainActivity.class,true), DELAY_TIME);
        OneShotUtil.getInstance(this);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_welcome;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(null);
    }
}
