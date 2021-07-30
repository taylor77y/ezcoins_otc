package com.ezcoins.manager.factory;


import com.ezcoins.constant.SysOrderConstants;
import com.ezcoins.constant.SysTipsConstants;
import com.ezcoins.constant.enums.otc.MatchOrderStatus;
import com.ezcoins.project.acl.entity.Log;
import com.ezcoins.project.acl.service.LogService;
import com.ezcoins.project.otc.entity.EzOtcChatMsg;
import com.ezcoins.project.otc.service.EzAdvertisingBusinessService;
import com.ezcoins.project.otc.service.EzOtcChatMsgService;
import com.ezcoins.project.system.entity.EzSysTips;
import com.ezcoins.project.system.service.EzSysTipsService;
import com.ezcoins.utils.*;
import eu.bitwalker.useragentutils.UserAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

/**
 * 异步工厂（产生任务用）
 * 
 */
public class AsyncFactory
{
    private static final Logger sys_user_logger = LoggerFactory.getLogger("sys-user");

    /**
     * 记录登录信息
     */
    public static TimerTask recordLogininfor(final String username, final String status, final String message,
                                             final Object... args)
    {
        final UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        final String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        return new TimerTask()
        {
            @Override
            public void run()
            {
                String address = AddressUtils.getRealAddressByIP(ip);
                StringBuilder s = new StringBuilder();
                s.append(LogUtils.getBlock(ip));
                s.append(address);
                s.append(LogUtils.getBlock(username));
                s.append(LogUtils.getBlock(status));
                s.append(LogUtils.getBlock(message));
                sys_user_logger.info(s.toString(), args);
                String os = userAgent.getOperatingSystem().getName();
                String browser = userAgent.getBrowser().getName();
//                SysLogininfor logininfor = new SysLogininfor();
//                logininfor.setUserName(username);
//                logininfor.setIpaddr(ip);
//                logininfor.setLoginLocation(address);
//                logininfor.setBrowser(browser);
//                logininfor.setOs(os);
//                logininfor.setMsg(message);
//                if (Constants.LOGIN_SUCCESS.equals(status) || Constants.LOGOUT.equals(status))
//                {
//                    logininfor.setStatus(Constants.SUCCESS);
//                }
//                else if (Constants.LOGIN_FAIL.equals(status))
//                {
//                    logininfor.setStatus(Constants.FAIL);
//                }
//                // 插入数据
//                SpringUtils.getBean(ISysLogininforService.class).insertLogininfor(logininfor);
            }
        };
    }

    /**
     * 操作日志记录
     */
//    public static TimerTask recordOper(final SysOperLog operLog)
//    {
//        return new TimerTask()
//        {
//            @Override
//            public void run()
//            {
//                operLog.setOperLocation(AddressUtils.getRealAddressByIP(operLog.getOperIp()));
////                SpringUtils.getBean(ISysOperLogService.class).insertOperlog(operLog);
//            }
//        };
//    }
    /**
     * 管理员操作日志记录
     */
    public static TimerTask recordOper(final Log operLog){
        return new TimerTask(){
            @Override
            public void run(){
                SpringUtils.getBean(LogService.class).save(operLog);
            }
        };
    }

    /**
     * 站内信
     */
    public static TimerTask StationLetter(final EzSysTips ezSysTips){
        return new TimerTask(){
            @Override
            public void run(){
                SpringUtils.getBean(EzSysTipsService.class).save(ezSysTips);
            }
        };
    }

    private static final EzSysTips ezSysTips=new EzSysTips();

    /**
     * 站内信
     */
    public static TimerTask StationLetter(String userId, SysTipsConstants.TipsType tipsType,Object ... args){
        ezSysTips.setUserId(userId);
        ezSysTips.setTitle("系统信息");
        ezSysTips.setContent(String.format(tipsType.getRemark(),args));
        return new TimerTask(){
            @Override
            public void run(){
                SpringUtils.getBean(EzSysTipsService.class).save(ezSysTips);
            }
        };
    }

    /**
     * 修改商户卖单买单
     */
    public static TimerTask updateCount(String sellUserId, String buyUserId, Date payTime, Date finishTime, boolean isAdmin, String status) {
        return new TimerTask(){
            @Override
            public void run(){
                SpringUtils.getBean(EzAdvertisingBusinessService.class)
                        .updateCount(sellUserId,buyUserId,payTime,finishTime,isAdmin,status);
            }
        };
    }

    private static final EzOtcChatMsg EZ_OTC_CHAT_MSG_SELL = new EzOtcChatMsg();
    private static final EzOtcChatMsg EZ_OTC_CHAT_MSG_BUY = new EzOtcChatMsg();
    /**
     * 发送信息
     */
    public static TimerTask sendSysChat(String sellUserId, String buyUserId,String orderMatchNo, SysOrderConstants.SysChatMsg chatMsg, MatchOrderStatus status){
        EZ_OTC_CHAT_MSG_SELL.setReceiveUserId(sellUserId);
        EZ_OTC_CHAT_MSG_SELL.setOrderMatchNo(orderMatchNo);
        EZ_OTC_CHAT_MSG_SELL.setSendText(chatMsg.getSellTips());
        EZ_OTC_CHAT_MSG_BUY.setReceiveUserId(buyUserId);
        EZ_OTC_CHAT_MSG_BUY.setOrderMatchNo(orderMatchNo);
        EZ_OTC_CHAT_MSG_BUY.setSendText(chatMsg.getBuyTips());
        List<EzOtcChatMsg> list = new ArrayList<>();
        list.add(EZ_OTC_CHAT_MSG_SELL);
        list.add(EZ_OTC_CHAT_MSG_BUY);
        return new TimerTask(){
            @Override
            public void run(){
                SpringUtils.getBean(EzOtcChatMsgService.class).sendSysChat(list,status.getCode());
            }
        };
    }





}
