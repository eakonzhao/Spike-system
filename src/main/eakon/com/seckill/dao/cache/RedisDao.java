package com.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.seckill.entity.SeckillInventory;
import com.seckill.util.LoggerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by Eakon on 2017/5/7.
 */
public class RedisDao {

    private  JedisPool jedisPool;

    @Autowired
    private LoggerUtil log;

    //protostuff
    private RuntimeSchema<SeckillInventory> schema = RuntimeSchema.createFrom(SeckillInventory.class);

    public RedisDao(String ip, int port){
        jedisPool = new JedisPool(ip,port);
    }

    public SeckillInventory getSeckillInventory(long seckillId){
        //redis操作逻辑
        try{
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill"+seckillId;
                //并没有实现内部序列化缓存
                //get->byte[] -> 反序列化 ->Object(Seckill)
                //采用自定义序列化
                //protostuff: pojo.
                byte[] bytes = jedis.get(key.getBytes());
                //缓存获取到
                if(bytes != null){
                    //空对象
                    SeckillInventory seckillInventory = schema.newMessage();
                    ProtostuffIOUtil.mergeFrom(bytes,seckillInventory,schema);
                    //seckillInventory 被反序列化
                    return seckillInventory;
                }
            }finally {
                jedis.close();
            }
        }catch (Exception e){
            log.logger.error(e.getMessage(),e);
        }
        return null;
    }

    public String putSeckillInventory(SeckillInventory seckillInventory){
        //set Object{seckillInventory} ->序列化 ->byte[]
        try{
            Jedis jedis = jedisPool.getResource();
            try{
                String key = "seckill"+seckillInventory.getSeckillId();
                byte[] bytes = ProtostuffIOUtil.toByteArray(seckillInventory,schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                //超时缓存
                int timeout = 60*60;//1 hour
                String result = jedis.setex(key.getBytes(),timeout,bytes);
                return result;
            }finally {
                jedis.close();
            }
        }catch (Exception e){
            log.logger.error(e.getMessage(),e);
        }
        return null;
    }
}
