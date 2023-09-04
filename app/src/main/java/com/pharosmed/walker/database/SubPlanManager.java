package com.pharosmed.walker.database;

import com.pharosmed.walker.beans.SubPlanEntity;
import com.pharosmed.walker.greendao.SubPlanEntityDao;
import com.pharosmed.walker.utils.DateFormatUtil;
import com.pharosmed.walker.utils.GreenDaoHelper;
import com.pharosmed.walker.utils.SPHelper;

import java.util.List;

/**
 * Created by zhanglun on 2021/5/7
 * Describe:
 */
public class SubPlanManager {
    private static volatile SubPlanManager instance = null;

    private SubPlanEntityDao subPlanEntityDao;

    private SubPlanManager() {
        subPlanEntityDao = GreenDaoHelper.getDaoSession().getSubPlanEntityDao();
    }

    public static SubPlanManager getInstance() {
        if (instance == null) {
            synchronized (SubPlanManager.class) {
                if (instance == null) {
                    instance = new SubPlanManager();
                }
            }
        }
        return instance;
    }
    void update(SubPlanEntity subPlanEntity){
        subPlanEntityDao.update(subPlanEntity);
    }
    public void insert(String startDate,int loadWeight,int classId, int weekTotal){
        int diff = Integer.parseInt(SPHelper.getUser().getWeight()) - (int)SPHelper.getUserEvaluateWeight();
        List<SubPlanEntity> subPlanEntityList = subPlanEntityDao.queryBuilder().where(SubPlanEntityDao.Properties.UserId.eq(SPHelper.getUserId()),SubPlanEntityDao.Properties.ClassId.eq(classId)).list();
        for (int i = 0; i < weekTotal; i++) {
            SubPlanEntity subPlanEntity = null;
            if (subPlanEntityList.size() > 0){
                subPlanEntity = subPlanEntityList.get(i);
            }
            if (subPlanEntity == null){
                subPlanEntity = new SubPlanEntity();
            }
            subPlanEntity.setClassId(classId);
            subPlanEntity.setUserId(SPHelper.getUserId());
            subPlanEntity.setLoad(loadWeight + diff * i/(weekTotal - 1));
            subPlanEntity.setWeekNum(i+1);
            subPlanEntity.setDayNum(0);
            subPlanEntity.setPlanStatus(0);
            subPlanEntity.setStartDate(DateFormatUtil.getBeforeOrAfterDate(i*7,startDate) );
            subPlanEntity.setEndDate(DateFormatUtil.getBeforeOrAfterDate((i+1)*7,startDate));
            subPlanEntityDao.insertOrReplace(subPlanEntity);
        }
    }
    public void insert2(String startDate,int loadWeight,int classId, int weekTotal){
        List<SubPlanEntity> subPlanEntityList = subPlanEntityDao.queryBuilder().where(SubPlanEntityDao.Properties.UserId.eq(SPHelper.getUserId()),SubPlanEntityDao.Properties.ClassId.eq(classId)).list();
        for (int i = 0; i < weekTotal; i++) {
            SubPlanEntity subPlanEntity = null;
            if (subPlanEntityList.size() > 0){
                subPlanEntity = subPlanEntityList.get(i);
            }
            if (subPlanEntity == null){
                subPlanEntity = new SubPlanEntity();
            }
            subPlanEntity.setClassId(classId);
            subPlanEntity.setUserId(SPHelper.getUserId());
            subPlanEntity.setLoad(loadWeight);
            subPlanEntity.setWeekNum(i+1);
            subPlanEntity.setDayNum(0);
            subPlanEntity.setPlanStatus(0);
            subPlanEntity.setStartDate(DateFormatUtil.getBeforeOrAfterDate(i*7,startDate) );
            subPlanEntity.setEndDate(DateFormatUtil.getBeforeOrAfterDate((i+1)*7,startDate));
            subPlanEntityDao.insertOrReplace(subPlanEntity);
        }
    }
    public void insert3(String startDate,int desWeight,int loadWeight,int classId, int weekTotal){
        List<SubPlanEntity> subPlanEntityList = subPlanEntityDao.queryBuilder().where(SubPlanEntityDao.Properties.UserId.eq(SPHelper.getUserId()),SubPlanEntityDao.Properties.ClassId.eq(classId)).list();
        for (int i = 0; i < weekTotal; i++) {
            SubPlanEntity subPlanEntity = null;
            if (subPlanEntityList.size() > 0){
                subPlanEntity = subPlanEntityList.get(i);
            }
            if (subPlanEntity == null){
                subPlanEntity = new SubPlanEntity();
            }
            subPlanEntity.setClassId(classId);
            subPlanEntity.setUserId(SPHelper.getUserId());
            subPlanEntity.setLoad(loadWeight + (desWeight - loadWeight) * i / (weekTotal - 1));
            subPlanEntity.setPlanStatus(0);
            if (classId == 1){
                subPlanEntity.setStartDate(DateFormatUtil.getBeforeOrAfterDate(i,startDate));
                subPlanEntity.setEndDate(DateFormatUtil.getBeforeOrAfterDate(i+1,startDate));
                subPlanEntity.setWeekNum(1);
                subPlanEntity.setDayNum(i+1);
            }else {
                subPlanEntity.setStartDate(DateFormatUtil.getBeforeOrAfterDate(i*7,startDate));
                subPlanEntity.setEndDate(DateFormatUtil.getBeforeOrAfterDate((i+1)*7,startDate));
                subPlanEntity.setWeekNum(i+1);
                subPlanEntity.setDayNum(0);
            }
            subPlanEntityDao.insertOrReplace(subPlanEntity);
        }
    }
    public void insert4(String startDate, int desWeight, int loadWeight,int classId, int weekTotal){
        List<SubPlanEntity> subPlanEntityList = subPlanEntityDao.queryBuilder().where(SubPlanEntityDao.Properties.UserId.eq(SPHelper.getUserId()),SubPlanEntityDao.Properties.ClassId.eq(classId)).list();
        for (int i = 0; i < weekTotal; i++) {
            SubPlanEntity subPlanEntity = null;
            if (subPlanEntityList.size() > 0){
                subPlanEntity = subPlanEntityList.get(i);
            }
            if (subPlanEntity == null){
                subPlanEntity = new SubPlanEntity();
            }
            subPlanEntity.setClassId(classId);
            subPlanEntity.setUserId(SPHelper.getUserId());
            subPlanEntity.setLoad(loadWeight + (desWeight - loadWeight) * i /(weekTotal - 1));
            subPlanEntity.setWeekNum(i+1);
            subPlanEntity.setDayNum(0);
            subPlanEntity.setPlanStatus(0);
            subPlanEntity.setStartDate(DateFormatUtil.getBeforeOrAfterDate(i*7,startDate) );
            subPlanEntity.setEndDate(DateFormatUtil.getBeforeOrAfterDate((i+1)*7,startDate));
            subPlanEntityDao.insertOrReplace(subPlanEntity);
        }
    }
    public void insert5(String startDate,int desWeight,int loadWeight,int classId, int weekTotal){
        List<SubPlanEntity> subPlanEntityList = subPlanEntityDao.queryBuilder().where(SubPlanEntityDao.Properties.UserId.eq(SPHelper.getUserId()),SubPlanEntityDao.Properties.ClassId.eq(classId)).list();
        if (weekTotal/2 == 1)
            weekTotal = weekTotal + 1;
        for (int i = 0; i < weekTotal/2; i++) {
            SubPlanEntity subPlanEntity = null;
            if (subPlanEntityList.size() > 0){
                subPlanEntity = subPlanEntityList.get(i);
            }
            if (subPlanEntity == null){
                subPlanEntity = new SubPlanEntity();
            }
            subPlanEntity.setClassId(classId);
            subPlanEntity.setUserId(SPHelper.getUserId());
            subPlanEntity.setLoad(loadWeight + (desWeight - loadWeight) * i /(weekTotal/2 - 1));
            subPlanEntity.setWeekNum(i*2+1);
            subPlanEntity.setDayNum(0);
            subPlanEntity.setPlanStatus(0);
            subPlanEntity.setStartDate(DateFormatUtil.getBeforeOrAfterDate(i*14,startDate));
            subPlanEntity.setEndDate(DateFormatUtil.getBeforeOrAfterDate(i*14 + 7,startDate));
            subPlanEntityDao.insertOrReplace(subPlanEntity);
            subPlanEntity.setStartDate(DateFormatUtil.getBeforeOrAfterDate(i*14 + 7,startDate));
            subPlanEntity.setEndDate(DateFormatUtil.getBeforeOrAfterDate((i+1)*14,startDate));
            subPlanEntity.setWeekNum(i*2+2);
            subPlanEntityDao.insertOrReplace(subPlanEntity);
        }
    }
    public int getThisWeekLoad(long userId){
        List<SubPlanEntity> subPlanEntityList = subPlanEntityDao.queryBuilder().where(SubPlanEntityDao.Properties.UserId.eq(userId)).list();
        for (SubPlanEntity subPlanEntity : subPlanEntityList){
            if (System.currentTimeMillis() >= DateFormatUtil.getString2Date(subPlanEntity.getStartDate()) &&
                    System.currentTimeMillis() < DateFormatUtil.getString2Date(subPlanEntity.getEndDate())){
                subPlanEntity.setPlanStatus(1);
                return subPlanEntity.getLoad();
            }else if (System.currentTimeMillis() < DateFormatUtil.getString2Date(subPlanEntity.getStartDate())){
                subPlanEntity.setPlanStatus(0);
            }else {
                subPlanEntity.setPlanStatus(2);
            }
            update(subPlanEntity);
        }
        return 0;
    }
}
