package com.seckill.util;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by Eakon on 2017/5/6.
 */
@Component(value = "log")
public class LoggerUtil {
    public final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
}
