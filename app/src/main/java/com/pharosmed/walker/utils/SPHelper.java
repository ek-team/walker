package com.pharosmed.walker.utils;

import com.pharosmed.walker.beans.UserBean;
import com.pharosmed.walker.constants.AppKeyManager;
import com.pharosmed.walker.constants.SPConstant;

/**
 * Created by zhanglun on 2021/4/26
 * Describe:
 */
public class SPHelper {
    //**********************************************  用户管理 START  **********************************************

    public static void saveUser(UserBean userBean) {
        if (userBean == null)
            return;
        SPUtils userSP = SPUtils.getInstance(SPConstant.USER);
        userSP.put(SPConstant.ID, userBean.getId());
        userSP.put(SPConstant.USER_ID, userBean.getUserId());
        userSP.put(SPConstant.NAME, userBean.getName());
        userSP.put(SPConstant.SEX, userBean.getSex());
        userSP.put(SPConstant.AGE, userBean.getAge());
        userSP.put(SPConstant.CASE_HISTORY_NO, userBean.getCaseHistoryNo());
        userSP.put(SPConstant.DOCTOR, userBean.getDoctor());
        userSP.put(SPConstant.DATE, userBean.getDate());
        userSP.put(SPConstant.TEL, userBean.getTelephone());
        userSP.put(SPConstant.DIAGNOSIS, userBean.getDiagnosis());
        userSP.put(SPConstant.WEIGHT, userBean.getWeight());
        userSP.put(SPConstant.EVALUATE_WEIGHT, userBean.getEvaluateWeight());
        userSP.put(SPConstant.ACCOUNT, userBean.getAccount());
        userSP.put(SPConstant.PASSWORD, userBean.getPassword());
    }

    public static UserBean getUser() {
        UserBean userBean = new UserBean();
        SPUtils userSP = SPUtils.getInstance(SPConstant.USER);
        userBean.setId(userSP.getLong(SPConstant.ID));
        userBean.setUserId(userSP.getLong(SPConstant.USER_ID));
        userBean.setName(userSP.getString(SPConstant.NAME));
        userBean.setSex(userSP.getInt(SPConstant.SEX));
        userBean.setAge(userSP.getInt(SPConstant.AGE));
        userBean.setCaseHistoryNo(userSP.getString(SPConstant.CASE_HISTORY_NO));
        userBean.setDoctor(userSP.getString(SPConstant.DOCTOR));
        userBean.setDate(userSP.getString(SPConstant.DATE));
        userBean.setTelephone(userSP.getString(SPConstant.TEL));
        userBean.setDiagnosis(userSP.getString(SPConstant.DIAGNOSIS));
        userBean.setWeight(userSP.getString(SPConstant.WEIGHT));
        userBean.setEvaluateWeight(userSP.getFloat(SPConstant.EVALUATE_WEIGHT));
        userBean.setAccount(userSP.getString(SPConstant.ACCOUNT));
        userBean.setPassword(userSP.getString(SPConstant.PASSWORD));
        return userBean;
    }

    public static String getUserName() {
        SPUtils userSP = SPUtils.getInstance(SPConstant.USER);
        return userSP.getString(SPConstant.NAME);
    }

    public static long getUserId() {
        SPUtils userSP = SPUtils.getInstance(SPConstant.USER);
        return userSP.getLong(SPConstant.USER_ID);
    }
    public static float getUserEvaluateWeight() {
        SPUtils userSP = SPUtils.getInstance(SPConstant.USER);
        return userSP.getFloat(SPConstant.EVALUATE_WEIGHT,0);
    }

    public static void clearUser() {
        SPUtils userSP = SPUtils.getInstance(SPConstant.USER);
        if (userSP == null) {
            return;
        }
        userSP.clear();
    }
    public static void saveVoiceState( boolean state) {
        SPUtils userSP = SPUtils.getInstance(SPConstant.USER);
        userSP.put("voiceSwitch", state);
    }
    /**
     * 是否开启语音
     */
    public static boolean getVoiceState() {
        SPUtils userSP = SPUtils.getInstance(SPConstant.USER);
        return userSP.getBoolean("voiceSwitch", true);
    }
    public static void saveMusicPosition(int position) {
        SPUtils userSP = SPUtils.getInstance(SPConstant.USER);
        userSP.put(AppKeyManager.EXTRA_MUSIC_FILE, position);
    }
    public static int getMusicPosition() {
        SPUtils userSP = SPUtils.getInstance(SPConstant.USER);
        return userSP.getInt(AppKeyManager.EXTRA_MUSIC_FILE, 0);
    }
    public static void saveRebootTime(int time) {
        SPUtils userSP = SPUtils.getInstance(SPConstant.USER);
        userSP.put(AppKeyManager.EXTRA_REBOOT_TIME, time);
    }
    public static int getRebootTime() {
        SPUtils userSP = SPUtils.getInstance(SPConstant.USER);
        return userSP.getInt(AppKeyManager.EXTRA_REBOOT_TIME, 0);
    }
}
