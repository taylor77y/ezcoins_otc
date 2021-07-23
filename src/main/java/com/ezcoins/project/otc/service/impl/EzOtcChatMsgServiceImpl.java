package com.ezcoins.project.otc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.config.EzCoinsConfig;
import com.ezcoins.config.ServerConfig;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.common.service.OssService;
import com.ezcoins.project.otc.entity.EzAdvertisingBusiness;
import com.ezcoins.project.otc.entity.EzOtcChatMsg;
import com.ezcoins.project.otc.entity.EzOtcOrderMatch;
import com.ezcoins.project.otc.entity.req.ChatMsgReqDto;
import com.ezcoins.project.otc.entity.resp.ChatMsgRespDto;
import com.ezcoins.project.otc.mapper.EzOtcChatMsgMapper;
import com.ezcoins.project.otc.service.EzAdvertisingBusinessService;
import com.ezcoins.project.otc.service.EzOtcChatMsgService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.project.otc.service.EzOtcOrderMatchService;
import com.ezcoins.response.Response;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponseList;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.utils.FileUploadUtils;
import com.ezcoins.utils.StringUtils;
import com.ezcoins.websocket.WebSocketHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * otc聊天信息表 服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-19
 */
@Service
public class EzOtcChatMsgServiceImpl extends ServiceImpl<EzOtcChatMsgMapper, EzOtcChatMsg> implements EzOtcChatMsgService {


    @Autowired
    private EzAdvertisingBusinessService businessService;

    @Autowired
    private EzOtcOrderMatchService orderMatchService;

    /**
     * 发送 聊天信息 文字/图片 内容类型(0:图片 1：文字)
     *
     * @param chatMsgList
     * @return
     */
    @Override
    public Response sendChat(List<EzOtcChatMsg> chatMsgList,String sendId) {
        for (EzOtcChatMsg ezOtcChatMsg:chatMsgList){
            if (StringUtils.isNotEmpty(sendId)){
                ezOtcChatMsg.setSendUserId(sendId);
            }
            baseMapper.insert(ezOtcChatMsg);
            //给用户一个信号
            WebSocketHandle.toChatWith(ezOtcChatMsg.getReceiveUserId(), ezOtcChatMsg.getOrderMatchNo());
        }
        return Response.success();
    }

    /**
     * 存入系统提示消息
     *
     * @param chatMsgList
     * @param status
     */
    @Override
    public void sendSysChat(List<EzOtcChatMsg> chatMsgList, String status) {
        for (EzOtcChatMsg ezOtcChatMsg:chatMsgList){
            baseMapper.insert(ezOtcChatMsg);
            String receiveUserId = ezOtcChatMsg.getReceiveUserId();
            //给用户一个信号
            WebSocketHandle.orderStatusChange(receiveUserId, status);
            //给用户一个信号
            WebSocketHandle.toChatWith(receiveUserId, ezOtcChatMsg.getOrderMatchNo());
        }
    }

    /**
     * 根据匹配订单id查询聊天记录
     *
     * @param orderMatchNo
     * @return
     */
    @Override
    public ResponseList<ChatMsgRespDto> chatMsg(String orderMatchNo) {
        //查询订单
        EzOtcOrderMatch match = orderMatchService.getById(orderMatchNo);
        String userId = match.getUserId();
        String otcOrderUserId = match.getOtcOrderUserId();
        String userId1 = ContextHandler.getUserId();
        String u = null;
        if (otcOrderUserId.equals(userId1)) {
            u = userId;
        } else {
            u = otcOrderUserId;
        }
        LambdaQueryWrapper<EzAdvertisingBusiness> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EzAdvertisingBusiness::getUserId, userId1);
        String sendName = businessService.getOne(lambdaQueryWrapper).getAdvertisingName();//自己的名字

        LambdaQueryWrapper<EzAdvertisingBusiness> lambdaQueryWrapper2 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper2.eq(EzAdvertisingBusiness::getUserId, u);
        String receiveName = businessService.getOne(lambdaQueryWrapper2).getAdvertisingName();//对面的名字
        //查询双方的
        LambdaQueryWrapper<EzOtcChatMsg> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EzOtcChatMsg::getOrderMatchNo, orderMatchNo);
        queryWrapper.orderByDesc(EzOtcChatMsg::getCreateTime);
        List<EzOtcChatMsg> list = baseMapper.selectList(queryWrapper);

        List<ChatMsgRespDto> list1 = new ArrayList<>();
        String finalU = u;
        list.forEach(e -> {
            if (!e.getReceiveUserId().equals(userId1) && "0".equals(e.getIsSystem())) {
            } else {
                ChatMsgRespDto chatMsgReqDto = new ChatMsgRespDto();
                BeanUtils.copyBeanProp(chatMsgReqDto, e);
                if (chatMsgReqDto.getReceiveUserId().equals(userId1)) {
                    chatMsgReqDto.setReceiveUserId(finalU);
                    chatMsgReqDto.setSendUserId(userId1);
                } else {
                    chatMsgReqDto.setSendUserId(finalU);
                    chatMsgReqDto.setReceiveUserId(chatMsgReqDto.getSendUserId());
                }
                chatMsgReqDto.setSendName(sendName);//我的名
                chatMsgReqDto.setReceiveName(receiveName);//对面的名
                list1.add(chatMsgReqDto);
            }
        });

        return ResponseList.success(list1);
    }
}
