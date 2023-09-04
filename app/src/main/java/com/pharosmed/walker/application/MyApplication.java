package com.pharosmed.walker.application;

import android.app.Application;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.pharosmed.walker.R;
import com.pharosmed.walker.error.CrashHandler;
import com.pharosmed.walker.utils.GreenDaoHelper;

/**
 * Created by zhanglun on 2021/4/9
 * Describe:
 */
public class MyApplication extends Application {
    public static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        CrashHandler.getInstance().init(instance);
        String param = "appid=" + getString(R.string.app_id) +
                "," +
                SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC;
        SpeechUtility.createUtility(MyApplication.this, param);
        GreenDaoHelper.initDatabase();
    }
}
