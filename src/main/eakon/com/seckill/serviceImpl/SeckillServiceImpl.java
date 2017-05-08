package com.seckill.serviceImpl;

import com.seckill.dao.SeckillInventoryDao;
import com.seckill.dao.SuccessKilledDao;
import com.seckill.dao.cache.RedisDao;
import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExecution;
import com.seckill.entity.SeckillInventory;
import com.seckill.entity.SuccessKilled;
import com.seckill.exception.RepeatkillException;
import com.seckill.exception.SeckillCloseException;
import com.seckill.exception.SeckillException;
import com.seckill.service.SeckillService;
import com.seckill.util.Constant;
import com.seckill.util.SeckillStateEnum;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Eakon on 2017/5/5.
 */
@Service(value = "seckillService")
public class SeckillServiceImpl implements SeckillService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "seckillInventoryDao")
    private SeckillInventoryDao seckillInventoryDao;

    @Resource(name = "successKilledDao")
    private SuccessKilledDao successKilledDao;

    @Autowired
    private RedisDao redisDao;

    public List<SeckillInventory> getSeckillList() {
        return seckillInventoryDao.queryAll(0,4);
    }

    public SeckillInventory getById(long seckillInventoryId) {
        return seckillInventoryDao.queryById(seckillInventoryId);
    }


    public Exposer exportSeckillUrl(long seckillId) {
        //优化点：缓存优化：超时的基础上维护一致性
        //1.访问redis
        SeckillInventory seckillInventory = redisDao.getSeckillInventory(seckillId);
        if(seckillInventory==null){
            //2.如果redis中不存在，访问数据库
            seckillInventory = seckillInventoryDao.queryById(seckillId);
            if(seckillInventory == null){
                return new Exposer(false,seckillId);
            }else{
                //3.将数据放入redis
                redisDao.putSeckillInventory(seckillInventory);
            }
        }
        Date startTime = seckillInventory.getStartTime();
        Date endTime = seckillInventory.getEndTime();
        //系统当前时间
        Date currentTime = new Date();
        //如果当前时间小于秒杀开始时间或者当前时间大于秒杀结束时间，意味着不能参加秒杀
        if(currentTime.getTime() < startTime.getTime() || currentTime.getTime() > endTime.getTime()){
            return new Exposer(false,currentTime.getTime(),startTime.getTime(),endTime.getTime(),seckillId);
        }
        //转化特定字符串的过程,不可逆
        String md5 = getMd5(seckillId);
        return new Exposer(true,md5,seckillId);
    }

    private String getMd5(long seckillId){
        String base = seckillId+"/"+ Constant.salt;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }
    /**
     *使用注解控制事务方法的优点：
     * 1.开发团队达成一致约定，明确标注事务方法的编程风格；
     * 2.保证事务方法的执行时间尽可能短，不要穿插其他网络操作，RPC/HTTP请求可以剥离到事务方法外部；
     * 3.不是所有的方法都需要事务，如果只有一条修改操作或者进行只读操作，那就不需要事务控制
     */
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException {
        if(md5 == null || !md5.equals(getMd5(seckillId))){
            throw new SeckillException("seckill data rewrite");
        }
        //执行秒杀逻辑：减库存 + 记录购买行为
        Date currentTime = new Date();
        try{
            //记录购买行为
            int insertCount = successKilledDao.insertSuccessKilled(seckillId,userPhone);
            //唯一：seckillId,userPhone
            if(insertCount <=0){
                //重复秒杀
                throw new RepeatkillException("seckill repeate");
            }else {
                SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
                //减少库存
                int updateCount = seckillInventoryDao.reduceSeckillInventoryMount(seckillId,currentTime);
                if(updateCount <= 0){
                    //没有更新到记录，秒杀结束，roolback
                    throw new SeckillCloseException("seckill is closed");
                }else{
                    //秒杀成功 commit
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS,successKilled);
                }
            }
        } catch(SeckillCloseException e){
            throw e;
        } catch(RepeatkillException e){
            throw e;
        }catch(SeckillException e){
            throw e;
        } catch(Exception e){
            logger.error(e.getMessage(),e);
            //所有编译器异常转化为运行期异常
            throw new SeckillException("seckill inner error:"+e.getMessage());
        }
    }

    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) throws SeckillException, SeckillCloseException, RepeatkillException {
        if(md5 == null || !md5.equals(getMd5(seckillId))){
            return new SeckillExecution(seckillId,SeckillStateEnum.DATA_REWRITE);
        }
        Date killTime = new Date();
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("seckillId",seckillId);
        map.put("phone",userPhone);
        map.put("killTime",killTime);
        map.put("result",null);
        //执行存储过程，result被复制
        try{
            successKilledDao.KillByProcedure(map);
            //获取result
            int result = MapUtils.getInteger(map,"result",-2);
            if(result==1){
                SuccessKilled sk = successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
                return new SeckillExecution(seckillId,SeckillStateEnum.SUCCESS,sk);
            }else {
                return new SeckillExecution(seckillId,SeckillStateEnum.stateOf(result));
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            return new SeckillExecution(seckillId,SeckillStateEnum.INNER_ERROR);
        }
    }


}
