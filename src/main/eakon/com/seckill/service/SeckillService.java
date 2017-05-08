package com.seckill.service;

import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExecution;
import com.seckill.entity.SeckillInventory;
import com.seckill.exception.RepeatkillException;
import com.seckill.exception.SeckillCloseException;
import com.seckill.exception.SeckillException;

import java.util.List;

/**
 * 业务接口:站在“使用者”角度设计接口
 * 三个方面：方法定义粒度，参数，返回类型(return 类型/异常)
 * Created by Eakon on 2017/5/5.
 */
public interface SeckillService {
    /**
     * 查询所有秒杀商品记录
     * @return
     */
    public List<SeckillInventory> getSeckillList();

    /**
     * 查询单条秒杀商品表中的记录
     * @param seckillInventoryId
     * @return
     */
    public SeckillInventory getById(long seckillInventoryId);

    /**
     * 若秒杀开始时输出秒杀接口地址
     * 否则输出系统时间和秒杀开始时间
     * @param seckillId
     */
    public Exposer exportSeckillUrl(long seckillId);

    /**
     *执行秒杀
     * @param seckillId
     * @param userPhone
     * @param md5
     */
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException,SeckillCloseException,RepeatkillException;

    /**
     * 通过存储过程执行秒杀操作
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     * @throws SeckillException
     * @throws SeckillCloseException
     * @throws RepeatkillException
     */
    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5);
}
