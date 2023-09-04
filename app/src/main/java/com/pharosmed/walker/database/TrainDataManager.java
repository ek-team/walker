package com.pharosmed.walker.database;

import com.pharosmed.walker.beans.TrainDataEntity;
import com.pharosmed.walker.beans.UserTrainRecordEntity;
import com.pharosmed.walker.constants.AppKeyManager;
import com.pharosmed.walker.greendao.TrainDataEntityDao;
import com.pharosmed.walker.utils.DateFormatUtil;
import com.pharosmed.walker.utils.GreenDaoHelper;
import com.pharosmed.walker.utils.SPHelper;

import java.util.List;

/**
 * Created by zhanglun on 2021/5/20
 * Describe:
 */
public class TrainDataManager {
    private static volatile TrainDataManager instance = null;

    private TrainDataEntityDao trainDataDao;

    private TrainDataManager() {
        trainDataDao = GreenDaoHelper.getDaoSession().getTrainDataEntityDao();
    }

    public static TrainDataManager getInstance() {
        if (instance == null) {
            synchronized (TrainDataManager.class) {
                if (instance == null) {
                    instance = new TrainDataManager();
                }
            }
        }
        return instance;
    }
    public void insert(TrainDataEntity trainDataEntity){
        long userId = SPHelper.getUserId();
        long date = System.currentTimeMillis();
        trainDataEntity.setCreateDate(date);
        trainDataEntity.setDateStr(DateFormatUtil.getDate2String(date, AppKeyManager.DATE_YMD));
        trainDataEntity.setPlanId(TrainPlanManager.getInstance().getCurrentPlanId(userId));
        trainDataEntity.setClassId(TrainPlanManager.getInstance().getCurrentClassId(userId));
        trainDataEntity.setIsUpload(0);
        trainDataEntity.setUserId(userId);
        trainDataEntity.setFrequency(UserTrainRecordManager.getInstance().getLastTimeFrequency(userId));
        trainDataDao.insert(trainDataEntity);
    }
    public List<TrainDataEntity> getTrainDataByDate(long userId,String date){
        return trainDataDao.queryBuilder().where(TrainDataEntityDao.Properties.UserId.eq(userId),TrainDataEntityDao.Properties.DateStr.eq(date)).list();
    }
    public List<TrainDataEntity> getTrainDataByDateAndFrequency(long userId,String date,int frequency){
        return trainDataDao.queryBuilder().where(TrainDataEntityDao.Properties.UserId.eq(userId),TrainDataEntityDao.Properties.DateStr.eq(date),
                TrainDataEntityDao.Properties.Frequency.eq(frequency)).orderAsc(TrainDataEntityDao.Properties.CreateDate).list();
    }
}
