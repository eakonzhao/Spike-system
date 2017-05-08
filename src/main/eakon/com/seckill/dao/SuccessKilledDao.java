package com.seckill.dao;

import com.seckill.entity.SuccessKilled;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * Created by Eakon on 2017/5/5.
 */
@Repository(value = "successKilledDao")
public interface SuccessKilledDao {

    /**
     * 插入购买明细，即往秒杀成功表里面插入数据，我们需要在这里进行判重操作，防止同一用户重复秒杀
     * @param seckillId
     * @param userPhone
     * @return 插入的行数,0代表插入失败
     */
    public int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);

    /**
     * 根据id查询SuccessKilled并携带秒杀产品对象实体
     * @param seckillId
     * @return 返回对应的秒杀记录以及该id对应的秒杀商品基本信息
     */
    public SuccessKilled queryByIdWithSeckill(@Param("seckillId") long seckillId,@Param("userPhone") long userPhone);

    /**
     * 使用存储过程执行秒杀
     * @param paramMap
     */
    public void KillByProcedure(Map<String,Object> paramMap);
}

