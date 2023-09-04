package com.pharosmed.walker.beans;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by zhanglun on 2021/5/8
 * Describe:
 */
@Entity(nameInDb = "EVALUATE_VALUE")
public class EvaluateEntity {
    @Id(autoincrement = true)
    private Long id;                 //ID
    private long userId;//用户唯一id 静态方法获取唯一id编号
    private int evaluateResult;
    private long createDate;
    private int vas;//耐受等级
    @Generated(hash = 1632377612)
    public EvaluateEntity(Long id, long userId, int evaluateResult, long createDate, int vas) {
        this.id = id;
        this.userId = userId;
        this.evaluateResult = evaluateResult;
        this.createDate = createDate;
        this.vas = vas;
    }
    @Generated(hash = 375544307)
    public EvaluateEntity() {
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
    public int getEvaluateResult() {
        return this.evaluateResult;
    }
    public void setEvaluateResult(int evaluateResult) {
        this.evaluateResult = evaluateResult;
    }
    public long getCreateDate() {
        return this.createDate;
    }
    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }
    public int getVas() {
        return this.vas;
    }
    public void setVas(int vas) {
        this.vas = vas;
    }
}
