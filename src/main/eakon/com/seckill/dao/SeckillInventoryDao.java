package com.seckill.dao;

import com.seckill.entity.SeckillInventory;

import java.util.Date;
import java.util.List;

/**
 * Created by Eakon on 2017/5/5.
 */
public interface SeckillInventoryDao {
    /**
     * 对秒杀商品进行减库存
     * @param seckillId
     * @param killTime
     * @return 返回的是数据库中记录更新的行数
     */
    public int reduceSeckillInventoryMount(long seckillId, Date killTime);

    /**
     * 根据商品id查询商品信息
     * @param seckillId
     * @return id对应的商品信息实体类
     */
    public SeckillInventory queryById(long seckillId);

    /**
     * 根据偏移量查询秒杀商品列表
     * @param offet
     * @param limit
     * @return
     */
    List<SeckillInventory> queryAll(int offet,int limit);
}
