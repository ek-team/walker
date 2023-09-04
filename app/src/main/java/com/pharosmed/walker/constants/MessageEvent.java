package com.pharosmed.walker.constants;

public class MessageEvent<T> {

    public final static String UPDATE_TOP_TIME = "0";                    // 更新标题栏的时间
    public final static String BATTERY_REFRESH = "1";                       // 电池电量刷新
    public final static String BLE_SCAN_RESULT = "3001";                    // 蓝牙扫描结果
    public final static String ACTION_GATT_CONNECTED = "3002";              // 蓝牙连接成功（更改界面手的显示状态）
    public final static String ACTION_GATT_DISCONNECTED = "3003";         // 蓝牙连接失败(连接中断)
    public final static String ACTION_READ_DATA = "2001";         // 读取重量
    public final static String ACTION_READ_DEVICE = "2002";         // 读取设备
    public final static String DISCONNECT_DEVICE = "2003";         // 断开设备
    public final static String GATT_TRANSPORT_OPEN = "2004";         // 传输通道打开


    public final static String ACTION_IAT = "6001";         // 语音识别返回结果
    public final static String ACTION_COUNTDOWN = "7001";         // 更新倒计时时间

    public final static String ACTION_SAVE_TIP = "8001";         // 保存文件提示



    private String action;
    private T data;

    public MessageEvent(String action, T data) {
        this.action = action;
        this.data = data;
    }

    public MessageEvent(String message) {
        this.action = message;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
