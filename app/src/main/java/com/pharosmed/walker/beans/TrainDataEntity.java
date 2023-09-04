package com.pharosmed.walker.beans;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by zhanglun on 2021/5/20
 * Describe:
 */
@Entity(nameInDb = "TRAIN_DATA")
public class TrainDataEntity {
    @Id(autoincrement = true)
    private Long id;                 //ID
    private long userId;//用户唯一id 静态方法获取唯一id编号
    private long createDate;
    private int frequency;//当天第几次
    private int targetLoad;//目标负重
    private int realLoad;//实际负重
    private long planId;
    private int classId;
    private int isUpload;
    private String dateStr;
    @Generated(hash = 742763714)
    public TrainDataEntity(Long id, long userId, long createDate, int frequency,
            int targetLoad, int realLoad, long planId, int classId, int isUpload,
            String dateStr) {
        this.id = id;
        this.userId = userId;
        this.createDate = createDate;
        this.frequency = frequency;
        this.targetLoad = targetLoad;
        this.realLoad = realLoad;
        this.planId = planId;
        this.classId = classId;
        this.isUpload = isUpload;
        this.dateStr = dateStr;
    }
    @Generated(hash = 1535441825)
    public TrainDataEntity() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getUserId() {
        return this.userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }
    public long getCreateDate() {
        return this.createDate;
    }
    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }
    public int getFrequency() {
        return this.frequency;
    }
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
    public int getTargetLoad() {
        return this.targetLoad;
    }
    public void setTargetLoad(int targetLoad) {
        this.targetLoad = targetLoad;
    }
    public int getRealLoad() {
        return this.realLoad;
    }
    public void setRealLoad(int realLoad) {
        this.realLoad = realLoad;
    }
    public long getPlanId() {
        return this.planId;
    }
    public void setPlanId(long planId) {
        this.planId = planId;
    }
    public int getClassId() {
        return this.classId;
    }
    public void setClassId(int classId) {
        this.classId = classId;
    }
    public int getIsUpload() {
        return this.isUpload;
    }
    public void setIsUpload(int isUpload) {
        this.isUpload = isUpload;
    }
    public String getDateStr() {
        return this.dateStr;
    }
    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

}
