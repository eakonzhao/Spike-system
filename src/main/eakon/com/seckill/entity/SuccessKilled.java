package com.seckill.entity;

import java.util.Date;

/**
 * Created by Eakon on 2017/5/5.
 */
public class SuccessKilled {
    private long seckillId;

    private long userPhone;

    private short state;

    private Date createTime;

    private SeckillInventory seckillInventory;//实体类之间的关联关系，多对一

    public SeckillInventory getSeckillInventory() {
        return seckillInventory;
    }

    public void setSeckillInventory(SeckillInventory seckillInventory) {
        this.seckillInventory = seckillInventory;
    }

    public SuccessKilled(long seckillId, long userPhone, short state, Date createTime) {
        this.seckillId = seckillId;
        this.userPhone = userPhone;
        this.state = state;
        this.createTime = createTime;
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public long getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(long userPhone) {
        this.userPhone = userPhone;
    }

    public short getState() {
        return state;
    }

    public void setState(short state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "SuccessKilled{" +
                "seckillId=" + seckillId +
                ", userPhone=" + userPhone +
                ", state=" + state +
                ", createTime=" + createTime +
                '}';
    }
}
