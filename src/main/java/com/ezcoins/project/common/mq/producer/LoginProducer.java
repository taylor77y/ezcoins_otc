package com.ezcoins.project.common.mq.producer;


import com.ezcoins.mq.RabbitMQConfig;
import com.ezcoins.utils.AddressUtils;
import com.ezcoins.utils.IpUtils;
import com.ezcoins.utils.LogUtils;
import com.ezcoins.utils.ServletUtils;
import com.ezcoins.project.system.entity.EzSysLogininfor;
import com.ezcoins.utils.AddressUtils;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class LoginProducer {

    private static final Logger sys_user_logger = LoggerFactory.getLogger("sys-user");
    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 登录之后 后续异步处理的代码
     */
    public void sendMsgLoginFollowUp(String userName,String userId , String phone,String userType) {
        final UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        final String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        String address = AddressUtils.getRealAddressByIP(ip);
        String os = userAgent.getOperatingSystem().getName();
        String browser = userAgent.getBrowser().getName();

        StringBuilder s = new StringBuilder();
        s.append(LogUtils.getBlock(ip));
        s.append(LogUtils.getBlock(address));
        s.append(LogUtils.getBlock(userName));
        s.append(LogUtils.getBlock(userType));
        s.append(LogUtils.getBlock(phone));
        sys_user_logger.info(s.toString());

        EzSysLogininfor ezSysLogininfor = new EzSysLogininfor();
        ezSysLogininfor.setLoginLocation(address);
        ezSysLogininfor.setBrowser(os);
        ezSysLogininfor.setUserId(userId);
        ezSysLogininfor.setIpaddr(ip);
        ezSysLogininfor.setOs(os);
        ezSysLogininfor.setUserName(userName);
        ezSysLogininfor.setUserType(userType);
        ezSysLogininfor.setBrowser(browser);
        ezSysLogininfor.setCreateTime(new Date());
        // 使用rabbitmq投递消息
        amqpTemplate.convertAndSend(RabbitMQConfig.ezcoins_USERLOGIN_QUEUE, "", ezSysLogininfor);
        log.info(">>>会员服务登录后续，投递消息到mq成功.<<<");
    }
}
