package com.seckill.controller;

import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExecution;
import com.seckill.dto.SeckillResult;
import com.seckill.entity.SeckillInventory;
import com.seckill.exception.RepeatkillException;
import com.seckill.exception.SeckillCloseException;
import com.seckill.service.SeckillService;
import com.seckill.util.LoggerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created by Eakon on 2017/5/6.
 */
@Controller
@RequestMapping("/seckill")// url:/模块/资源/{id}/细分 /seckill/list
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    @Resource(name = "log")
    private LoggerUtil log;
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public String list(Model model){
        //获取列表页
        List<SeckillInventory> seckillInventoryList = seckillService.getSeckillList();
        model.addAttribute("seckillInventoryList",seckillInventoryList);
        return "list";//  /WEB-INF/jsp/list.jsp
    }

    @RequestMapping(value = "/{seckillId}/detail",method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId")Long seckillId,Model model){
        if(seckillId == null){
            return "redirect:/seckill/list";
        }
        SeckillInventory seckillInventory = seckillService.getById(seckillId);
        if(seckillInventory == null){
            return "forward:/seckill/list";
        }
        model.addAttribute("seckillInventory",seckillInventory);
        return "detail";
    }

    /**
     * ajax json
     * @param seckillId
     */
    @ResponseBody
    @RequestMapping(value = "/{seckillId}/exposer",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    public SeckillResult<Exposer> exposer(Long seckillId){
        SeckillResult<Exposer> result;
        try{
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true,exposer);
        }catch(Exception e){
            log.logger.error(e.getMessage(),e);
            result = new SeckillResult<Exposer>(false,e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/{seckillId}/{md5}/execution",
                    method = RequestMethod.POST,
                    produces = {"application/json;charset=UTF-8"})
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId, @PathVariable("md5") String md5, @CookieValue(value = "killPhone",required=false) Long phone){
        //Spring MVC valid
        if(phone == null){
            return new SeckillResult<SeckillExecution>(false,"未注册");
        }
        SeckillResult<SeckillExecution> result=null;
        try{
            SeckillExecution execution = seckillService.executeSeckill(seckillId,phone,md5);
            return new SeckillResult<SeckillExecution>(true,execution);
        }catch(RepeatkillException e){

        }catch(SeckillCloseException e){

        }catch (Exception e){
            log.logger.error(e.getMessage(),e);
        }
        return result;
    }

    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
    public SeckillResult<Long> currentTime(){
        Date date = new Date();
        return new SeckillResult(true,date.getTime());
    }
}
