package com.pharosmed.walker.database;

import android.text.TextUtils;

import com.pharosmed.walker.beans.ChartRecordBean;
import com.pharosmed.walker.beans.TrainDataEntity;
import com.pharosmed.walker.beans.UserTrainRecordEntity;
import com.pharosmed.walker.constants.AppKeyManager;
import com.pharosmed.walker.constants.Global;
import com.pharosmed.walker.greendao.UserTrainRecordEntityDao;
import com.pharosmed.walker.utils.DataTransformUtil;
import com.pharosmed.walker.utils.DateFormatUtil;
import com.pharosmed.walker.utils.GreenDaoHelper;
import com.pharosmed.walker.utils.SPHelper;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by zhanglun on 2021/5/11
 * Describe:
 */
public class UserTrainRecordManager {
    private static volatile UserTrainRecordManager instance = null;
    private UserTrainRecordEntityDao userTrainRecordDao;
    private UserTrainRecordManager() {
        userTrainRecordDao = GreenDaoHelper.getDaoSession().getUserTrainRecordEntityDao();
    }

    public static UserTrainRecordManager getInstance() {
        if (instance == null) {
            synchronized (UserTrainRecordManager.class) {
                if (instance == null) {
                    instance = new UserTrainRecordManager();
                }
            }
        }
        return instance;
    }
    public void insert(UserTrainRecordEntity userTrainRecord){
        long currentTime = System.currentTimeMillis();
        long userId = SPHelper.getUserId();
        userTrainRecord.setCreateDate(currentTime);
        userTrainRecord.setDateStr(DateFormatUtil.getDate2String(currentTime,AppKeyManager.DATE_YMD));
        userTrainRecord.setPlanId(TrainPlanManager.getInstance().getCurrentPlanId(userId));
        userTrainRecord.setClassId(TrainPlanManager.getInstance().getCurrentClassId(userId));
        userTrainRecord.setFrequency(getLastTimeFrequency(userId) + 1);
        userTrainRecordDao.insert(userTrainRecord);
    }
    public List<UserTrainRecordEntity> loadAll(long userId){
        return userTrainRecordDao.queryBuilder().where(UserTrainRecordEntityDao.Properties.UserId.eq(userId)).orderAsc(UserTrainRecordEntityDao.Properties.CreateDate).list();
    }
    public int getLastTimeFrequency(long userId){
        long currentDate = System.currentTimeMillis();
        int frequency = 0;
        List<UserTrainRecordEntity> userTrainRecordEntities = userTrainRecordDao.queryBuilder().where(UserTrainRecordEntityDao.Properties.UserId.eq(userId),
                UserTrainRecordEntityDao.Properties.DateStr.eq(DateFormatUtil.getDate2String(currentDate, AppKeyManager.DATE_YMD))).list();
        for (UserTrainRecordEntity recordEntity: userTrainRecordEntities){
            if (frequency <= recordEntity.getFrequency()){
                frequency = recordEntity.getFrequency();
            }
        }
        return frequency;
    }
    public List<UserTrainRecordEntity> loadByDate(long userId,long dateTemp){
        return userTrainRecordDao.queryBuilder().where(UserTrainRecordEntityDao.Properties.UserId.eq(userId), UserTrainRecordEntityDao.Properties.DateStr.eq(DateFormatUtil.getDate2String(dateTemp, AppKeyManager.DATE_YMD))).list();
    }
    public List<UserTrainRecordEntity> loadByDate(long userId,String dateStr){
        return userTrainRecordDao.queryBuilder().where(UserTrainRecordEntityDao.Properties.UserId.eq(userId), UserTrainRecordEntityDao.Properties.DateStr.eq(dateStr)).list();
    }
    public List<ChartRecordBean> getChartData(long userId){
        if (!Global.USER_MODE)
            return null;
        List<UserTrainRecordEntity> userTrainRecordEntities = loadAll(userId);
        LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>();
        for (UserTrainRecordEntity entity : userTrainRecordEntities){
            linkedHashSet.add(entity.getDateStr());
        }
        List<ChartRecordBean> chartRecordBeanList = new ArrayList<>();
        for (String dateStr: linkedHashSet){//遍历当前用户记录的日期
            ChartRecordBean chartRecordBean = new ChartRecordBean();
            chartRecordBean.setDate(dateStr);
            List<UserTrainRecordEntity> entityListByDate = loadByDate(userId,dateStr);//获取某个日期下的训练记录
            int painLevelTotal = 0;
            int targetWeightTotal = 0;
            int errorFeedbackTotal = 0;
            int classId = 0;
            List<ChartRecordBean.NumOfTimeBean> numOfTimeBeanList = new ArrayList<>();
            for (UserTrainRecordEntity entity : entityListByDate){//遍历训练记录
                painLevelTotal = painLevelTotal + entity.getPainLevel();
                targetWeightTotal = targetWeightTotal + entity.getTargetLoad();
                List<TrainDataEntity> trainDataEntities = TrainDataManager.getInstance().getTrainDataByDateAndFrequency(userId,dateStr,entity.getFrequency()-1);
                ChartRecordBean.NumOfTimeBean numOfTimeBean = new ChartRecordBean.NumOfTimeBean();
                int realLoad = 0;
                for (TrainDataEntity trainDataEntity : trainDataEntities){//遍历每踩的实际负重相加
                    realLoad = realLoad + trainDataEntity.getRealLoad();
                }
                if (trainDataEntities.size() != 0){
                    numOfTimeBean.setAverageWeight(realLoad/trainDataEntities.size());//保存每次的平均实际负重
                }
                numOfTimeBean.setDateSte(dateStr);
                numOfTimeBean.setFrequency(entity.getFrequency());
                numOfTimeBean.setTargetWeight(entity.getTargetLoad());
                numOfTimeBeanList.add(numOfTimeBean);
                if (!entity.getAdverseReactions().equals("无") && !TextUtils.isEmpty(entity.getAdverseReactions())){
                    errorFeedbackTotal++;
                }
                classId = entity.getClassId();
            }
            chartRecordBean.setPainFeedback(errorFeedbackTotal);
            chartRecordBean.setClassId(classId);
            if (entityListByDate.size() != 0){
                chartRecordBean.setPainLevel(painLevelTotal/entityListByDate.size());//获取平均疼痛等级
                chartRecordBean.setTargetWeight(targetWeightTotal/entityListByDate.size());//获取平均目标负重
            }
            int realLoadTotal = 0;
            for (ChartRecordBean.NumOfTimeBean numOfTimeBean : numOfTimeBeanList){
                realLoadTotal = realLoadTotal + numOfTimeBean.getAverageWeight();
            }
            if (numOfTimeBeanList.size() != 0){
                chartRecordBean.setAverageWeight(realLoadTotal/numOfTimeBeanList.size());
                chartRecordBean.setNumOfTimeBeanList(numOfTimeBeanList);
            }

            chartRecordBeanList.add(chartRecordBean);
        }
        return chartRecordBeanList;
    }
}
