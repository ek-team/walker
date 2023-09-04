package com.pharosmed.walker.database;

import com.pharosmed.walker.beans.EvaluateEntity;
import com.pharosmed.walker.greendao.EvaluateEntityDao;
import com.pharosmed.walker.utils.DateFormatUtil;
import com.pharosmed.walker.utils.GreenDaoHelper;
import com.pharosmed.walker.utils.SPHelper;

import java.util.List;

/**
 * Created by zhanglun on 2021/5/8
 * Describe:
 */
public class EvaluateManager {
    private static volatile EvaluateManager instance = null;

    private EvaluateEntityDao evaluateEntityDao;

    private EvaluateManager() {
        evaluateEntityDao = GreenDaoHelper.getDaoSession().getEvaluateEntityDao();
    }

    public static EvaluateManager getInstance() {
        if (instance == null) {
            synchronized (EvaluateManager.class) {
                if (instance == null) {
                    instance = new EvaluateManager();
                }
            }
        }
        return instance;
    }
    public void insert(int value,int vas){
        EvaluateEntity evaluateEntity = new EvaluateEntity();
        evaluateEntity.setCreateDate(System.currentTimeMillis());
        evaluateEntity.setEvaluateResult(value);
        evaluateEntity.setVas(vas);
        evaluateEntity.setUserId(SPHelper.getUserId());
        evaluateEntityDao.insert(evaluateEntity);
    }
    public List<EvaluateEntity> loadAll(long userId){
        return evaluateEntityDao.queryBuilder().where(EvaluateEntityDao.Properties.UserId.eq(userId)).list();
    }
}
