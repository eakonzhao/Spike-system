package com.seckill.dao;

import com.seckill.entity.SeckillInventory;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by Eakon on 2017/5/5.
 */
@Repository(value = "seckillInventoryDao")
public interface SeckillInventoryDao {
    /**
     * 对秒杀商品进行减库存
     * @param seckillId
     * @param killTime
     * @return 返回的是数据库中记录更新的行数
     */
    public int reduceSeckillInventoryMount(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    /**
     * 根据商品id查询商品信息
     * @param seckillId
     * @return id对应的商品信息实体类
     */
    public SeckillInventory queryById(long seckillId);

    /**
     * 根据偏移量查询秒杀商品列表
     * 参数列表中要用@Param("offset")以及@Param("limit")来修饰的原因是
     * @param offet
     * @param limit
     * @return
     */
    List<SeckillInventory> queryAll(@Param("offset") int offet, @Param("limit") int limit);
}
