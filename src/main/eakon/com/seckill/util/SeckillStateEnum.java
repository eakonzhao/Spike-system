package com.seckill.util;

/**
 * Created by Eakon on 2017/5/7.
 */
public enum SeckillStateEnum {
    SUCCESS(1,"秒杀成功"),
    END(0,"秒杀结束"),
    REPEATE_KILL(-1,"重复秒杀"),
    INNER_ERROR(-2,"系统内部错误"),
    DATA_REWRITE(-3,"数据篡改");
    private int state;

    private String stateInfo;
    SeckillStateEnum(int state, String stateInfo){
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public static SeckillStateEnum stateOf(int index){
        for(SeckillStateEnum state: values()){
            if(state.getState() == index){
                return state;
            }
        }
        return null;
    }
}
