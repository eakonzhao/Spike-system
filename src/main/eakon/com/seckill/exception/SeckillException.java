package com.seckill.exception;

/**
 * Created by Eakon on 2017/5/5.
 */
public class SeckillException extends RuntimeException {
    public SeckillException() {

    }

    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
