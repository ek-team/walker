package com.pharosmed.walker.constants;

/**
 * Created by zhanglun on 2021/4/9
 * Describe:
 */
public class Global {
    public static final String Header = ":";
    public static final String Header_S = "FFFF";
    public static volatile boolean isReconnectBle = false;
    public static volatile boolean isConnected = false;
    public static volatile String ConnectStatus = "unconnected";
    public static volatile String ConnectedAddress = null;
    public static String ConnectedName = null;
    public static boolean isSendHeart = true;
    public static int VOICE_SWITCH = 0;          //语音开关	开0 关1
    public static volatile int BLE_BATTERY = 0;          //蓝牙鞋电量
    public static volatile boolean USER_MODE = true;
    public static volatile int ConnectMainMode = 0;          //训练模式
    public static volatile int ConnectEvaluateMode = 1;          //评估模式
    public static volatile int ConnectUserMode = 2;          //用户模式
    public static volatile int ConnectSetMode = 3;          //设置模式
}
