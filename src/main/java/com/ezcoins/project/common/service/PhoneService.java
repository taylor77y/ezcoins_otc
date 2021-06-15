package com.ezcoins.project.common.service;

import com.ezcoins.base.BaseException;
import com.ezcoins.project.config.entity.EzSmsConfig;
import com.ezcoins.project.config.service.EzSmsConfigService;
import com.ezcoins.redis.RedisCache;
import com.ezcoins.utils.HttpUtils;
import com.ezcoins.utils.MessageUtils;
import com.ezcoins.utils.RandomUtil;
import com.ezcoins.utils.StringUtils;
import com.ezcoins.constant.interf.RedisConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * @Author:
 * @Email:
 * @Description:
 * @Date:2021/5/19 16:48
 * @Version:1.0
 */
@Service
@Slf4j
public class PhoneService {
    @Autowired
    private EzSmsConfigService ezSmsConfigService;

    @Autowired
    private RedisCache redisCache;

    private static final Integer TIMEOUT=5;


    private static final String SMSAPI = "https://www.isms.com.my/isms_send_all_id.php";


    private static final String BALANCE = "https://www.isms.com.my/isms_balance.php";


    private static final String SUCCESS = "2000";


    public BigDecimal getBalance() {
        EzSmsConfig config = redisCache.getCacheObject(RedisConstants.SMS_CONFIG_KEY);
        if (null == config) {
            config = ezSmsConfigService.getById("1");
            redisCache.setCacheObject(RedisConstants.SMS_CONFIG_KEY,config,TIMEOUT, TimeUnit.MINUTES);
        }
        String un = config.getUn();
        String pwd = config.getPwd();
        String s1 = HttpUtils.sendGet(BALANCE, "un=" + un + "&pwd=" + pwd);
        if (StringUtils.isNumeric(s1)){
            log.info("查询余额失败");
            return null;
        }
        return new BigDecimal(s1);
    }

    public void send(String key,String code,String phone) {
        EzSmsConfig config = redisCache.getCacheObject(RedisConstants.SMS_CONFIG_KEY);
        if (null == config) {
            config = ezSmsConfigService.getById("1");
            redisCache.setCacheObject(RedisConstants.SMS_CONFIG_KEY,config,TIMEOUT, TimeUnit.MINUTES);
        }
        String un = config.getUn();
        String pwd = config.getPwd();
        String msg = config.getMsg()+code;
        String sendid = config.getSendid();
        String agreedterm = config.getAgreedterm();
        String s = HttpUtils.sendGet(SMSAPI, "un=" + un + "&pwd=" + pwd + "&dstno=" + phone + "&msg=" + msg + "&type=" + "1" + "&sendid=" + sendid + "&agreedterm=" + agreedterm);
        if (!s.contains(SUCCESS)){
            log.info("发送邮件失败-异常 {}", s);
            throw new BaseException(MessageUtils.message("短信发送失败"));
        }
        redisCache.setCacheObject(key+phone,code,TIMEOUT, TimeUnit.MINUTES);
    }
}
