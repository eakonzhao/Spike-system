package com.seckill.exception;

/**
 * 重复秒杀异常(RuntimException)
 * Created by Eakon on 2017/5/5.
 */
public class RepeatkillException extends SeckillException {

    public RepeatkillException(String message) {
        super(message);
    }

    public RepeatkillException(String message, Throwable cause) {
        super(message, cause);
    }
}
