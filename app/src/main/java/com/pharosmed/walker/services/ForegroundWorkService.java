package com.pharosmed.walker.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.pharosmed.walker.beans.Battery;
import com.pharosmed.walker.R;
import com.pharosmed.walker.application.MyApplication;
import com.pharosmed.walker.bluetooth.BluetoothController;
import com.pharosmed.walker.constants.Global;
import com.pharosmed.walker.constants.MessageEvent;
import com.pharosmed.walker.utils.DataTransformUtil;

import org.greenrobot.eventbus.EventBus;

import java.nio.charset.StandardCharsets;


/**
 * @Description: 前台工作服务（设置为前台优先级最高，软件启动就开始了）
 * @Author: zf
 * @Time 2019/4/25
 */
public class ForegroundWorkService extends Service {

    public static final String CMD = "cmd";
    /**
     * id不可设置为0,否则不能设置为前台service
     */
    private static final int NOTIFICATION_DOWNLOAD_PROGRESS_ID = 0x0001;

    private boolean isTimeFlag = true;
    private TimeThread mTimeThread;
    private HeartThread mHeartThread;
    private BatteryRefreshThread batteryRefreshThread;
    private IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public ForegroundWorkService getService() {
            return ForegroundWorkService.this;
        }
    }


    public static void launch() {
        launch(null);
    }

    public static void launch(String cmd) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            MyApplication.getInstance().startForegroundService(new Intent(MyApplication.getInstance(), ForegroundWorkService.class)
                    .putExtra(CMD, cmd));
        }else {
            MyApplication.getInstance().startService(new Intent(MyApplication.getInstance(), ForegroundWorkService.class)
                    .putExtra(CMD, cmd));
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotification();
        mTimeThread = new TimeThread();
        mTimeThread.start();
        mHeartThread = new HeartThread();
        mHeartThread.start();
        batteryRefreshThread = new BatteryRefreshThread();
        batteryRefreshThread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        String cmd = intent.getStringExtra(CMD);
//        Log.d("Service", "onStartCommand()" + cmd);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("Service", "onBind()");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        if (mTimeThread != null) {
            mTimeThread.interrupt();
            isTimeFlag = false;
            mTimeThread = null;
        }
        if (mHeartThread != null){
            mHeartThread.interrupt();
            Global.isSendHeart = false;
            mHeartThread = null;
        }
        if (batteryRefreshThread != null){
            batteryRefreshThread.interrupt();
            batteryRefreshThread = null;
        }
        stopForeground(true);
        Log.e("ForegroundWorkService", "onDestroy: "+"死掉了" );
        super.onDestroy();
    }

    /**
     * Notification
     */
    public void createNotification() {
        String channel_id = "com.pharosmed.walker";
        String channelName = "MyWorkService";

        //使用兼容版本
        Notification.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel chan = new NotificationChannel(channel_id, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);
            builder = new Notification.Builder(this,channel_id);
        }else {
            builder = new Notification.Builder(this);
        }
        //设置状态栏的通知图标
        builder.setSmallIcon(R.mipmap.ic_launcher);
        //禁止用户点击删除按钮删除
        builder.setAutoCancel(true);
        //禁止滑动删除
        builder.setOngoing(true);
        //右上角的时间显示
        builder.setShowWhen(true);
        //设置通知栏的标题内容
        builder.setContentTitle("助行");
//        builder.setContentText("连接你我");
        //创建通知
        Notification notification = builder.build();
        //设置为前台服务
        startForeground(NOTIFICATION_DOWNLOAD_PROGRESS_ID, notification);
    }

    //动态更新时间的线程
    private class TimeThread extends Thread {
        @Override
        public void run() {
            super.run();
            do {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.UPDATE_TOP_TIME));
                SystemClock.sleep(1000);
            } while (isTimeFlag);
        }
    }
    //动态更新时间的线程
    private class BatteryRefreshThread extends Thread {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            super.run();
            do {
                BatteryManager manager = (BatteryManager) getSystemService(BATTERY_SERVICE);
                int batteryVolume = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);///当前电量百分比
                int batteryStatus = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS);
                if (batteryVolume > 100)
                    batteryVolume = 100;
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.BATTERY_REFRESH, new Battery(batteryStatus,batteryVolume)));
                SystemClock.sleep(1000);
            } while (isTimeFlag);
        }
    }
    //维持心跳线程 一分钟和下位机通信一次
    private class HeartThread extends Thread {
        @Override
        public void run() {
            super.run();
            do {
                if (Global.isSendHeart && Global.isConnected){
                    int a = 0x1A;
                    int b = 0x04 | 0x30;
                    int c = 0x00;
                    int d = 0xFF - (a + b + c) + 1;
                    String message = ":1A" + DataTransformUtil.toHexString((byte) b) + "00" + DataTransformUtil.toHexString((byte) d);
                    BluetoothController.getInstance().writeRXCharacteristic(Global.ConnectedAddress,message.getBytes(StandardCharsets.UTF_8));
//                HermesEventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_HEART_STATUS, heartStatus));
                    SystemClock.sleep(60 * 1000);
                }
            }while (isTimeFlag);
        }
    }

}
