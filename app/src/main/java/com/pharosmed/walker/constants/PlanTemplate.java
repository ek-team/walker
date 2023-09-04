package com.pharosmed.walker.constants;

import com.pharosmed.walker.beans.PlanEntity;
import com.pharosmed.walker.utils.SPHelper;

/**
 * Created by zhanglun on 2021/4/29
 * Describe:
 */
public class PlanTemplate {
    public static PlanEntity setPlan(long planId,int classId){
        PlanEntity planEntity = new PlanEntity();
        planEntity.setUserId(SPHelper.getUserId());
        planEntity.setPlanId(planId);
        planEntity.setClassId(classId);
        planEntity.setLoad(-1);
        planEntity.setPlanStatus(0);
        return planEntity;
    }
}
