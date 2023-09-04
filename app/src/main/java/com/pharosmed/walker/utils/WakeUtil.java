package com.pharosmed.walker.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;
import com.pharosmed.walker.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhanglun on 2021/4/23
 * Describe:
 */
public class WakeUtil {
    private static String TAG = WakeUtil.class.getSimpleName();
    // 语音唤醒对象
    private VoiceWakeuper mIvw;
    // 设置门限值 ： 门限值越低越容易被唤醒
    private int curThresh = 1450;
    private Context context;
    private String keep_alive = "1";//持续唤醒；0唤醒一次
    private String ivwNetMode = "0";//模式0：关闭闭环优化功能 模式1：开启闭环优化功能，允许上传优化数据。

    private static volatile WakeUtil instance = null;
    public static WakeUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (WakeUtil.class) {
                if (instance == null) {
                    instance = new WakeUtil(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public WakeUtil(Context context) {
        this.context = context;
        initConfig();
    }

    private void initConfig() {
        // 初始化唤醒对象
        mIvw = VoiceWakeuper.createWakeuper(context, null);
    }
    private void setParam() {
        // 清空参数
        mIvw.setParameter(SpeechConstant.PARAMS, null);
        // 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
        mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "0:"+ curThresh);
        // 设置唤醒模式
        mIvw.setParameter(SpeechConstant.IVW_SST, "wakeup");
        // 设置持续进行唤醒
        mIvw.setParameter(SpeechConstant.KEEP_ALIVE, keep_alive);
        // 设置闭环优化网络模式
        mIvw.setParameter(SpeechConstant.IVW_NET_MODE, ivwNetMode);
        // 设置唤醒资源路径
        mIvw.setParameter(SpeechConstant.IVW_RES_PATH, getResource());
        // 设置唤醒录音保存路径，保存最近一分钟的音频
        mIvw.setParameter( SpeechConstant.IVW_AUDIO_PATH, Environment.getExternalStorageDirectory().getPath()+"/msc/ivw.wav" );
        mIvw.setParameter( SpeechConstant.AUDIO_FORMAT, "wav" );
        // 如有需要，设置 NOTIFY_RECORD_DATA 以实时通过 onEvent 返回录音音频流字节
        //mIvw.setParameter( SpeechConstant.NOTIFY_RECORD_DATA, "1" );
        // 启动唤醒
        /*	mIvw.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");*/
        mIvw.startListening(mWakeuperListener);
    }
    public void startWake(){
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null){
            setParam();
        }
    }
    private String getResource() {
        final String resPath = ResourceUtil.generateResourcePath(context, ResourceUtil.RESOURCE_TYPE.assets, "ivw/"+context.getString(R.string.app_id)+".jet");
        Log.d( TAG, "resPath: "+resPath );
        return resPath;
    }
    private WakeuperListener mWakeuperListener = new WakeuperListener() {

        @Override
        public void onResult(WakeuperResult result) {
            Log.d(TAG, "onResult");
            String resultString;
            try {
                String text = result.getResultString();
                JSONObject object;
                object = new JSONObject(text);
                StringBuffer buffer = new StringBuffer();
                buffer.append("【RAW】 "+text);
                buffer.append("\n");
                buffer.append("【操作类型】"+ object.optString("sst"));
                buffer.append("\n");
                buffer.append("【唤醒词id】"+ object.optString("id"));
                buffer.append("\n");
                buffer.append("【得分】" + object.optString("score"));
                buffer.append("\n");
                buffer.append("【前端点】" + object.optString("bos"));
                buffer.append("\n");
                buffer.append("【尾端点】" + object.optString("eos"));
                resultString =buffer.toString();
            } catch (JSONException e) {
                resultString = "结果解析出错";
                e.printStackTrace();
            }
            Log.e(TAG,resultString);
//            textView.setText(resultString);
        }

        @Override
        public void onError(SpeechError error) {
//            showTip(error.getPlainDescription(true));
        }

        @Override
        public void onBeginOfSpeech() {
        }

        @Override
        public void onEvent(int eventType, int isLast, int arg2, Bundle obj) {
            switch( eventType ){
                // EVENT_RECORD_DATA 事件仅在 NOTIFY_RECORD_DATA 参数值为 真 时返回
                case SpeechEvent.EVENT_RECORD_DATA:
                    final byte[] audio = obj.getByteArray( SpeechEvent.KEY_EVENT_RECORD_DATA );
                    Log.i( TAG, "ivw audio length: "+audio.length );
                    break;
            }
        }

        @Override
        public void onVolumeChanged(int volume) {

        }
    };
    public void destoryWake(){
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            mIvw.destroy();
        }
    }
}
