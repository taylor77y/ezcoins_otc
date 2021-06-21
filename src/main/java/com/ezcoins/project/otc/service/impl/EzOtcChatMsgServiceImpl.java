package com.ezcoins.project.otc.service.impl;

import com.ezcoins.project.common.service.OssService;
import com.ezcoins.project.otc.entity.EzOtcChatMsg;
import com.ezcoins.project.otc.entity.req.ChatMsgReqDto;
import com.ezcoins.project.otc.mapper.EzOtcChatMsgMapper;
import com.ezcoins.project.otc.service.EzOtcChatMsgService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.response.BaseResponse;
import com.ezcoins.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private OssService ossService;

    /**
     * 发送 聊天信息 文字/图片 内容类型(0:图片 1：文字)
     * @param msgReqDto
     * @return
     */
    @Override
    public BaseResponse sendChat(ChatMsgReqDto msgReqDto) {
        EzOtcChatMsg ezOtcChatMsg = new EzOtcChatMsg();
        BeanUtils.copyBeanProp(ezOtcChatMsg,msgReqDto);

        //判断聊天信息是否图片
        String type = msgReqDto.getType();
        if ("0".equals(type)){
            String url = ossService.uploadFileAvatar(msgReqDto.getFile(), "chatMsg");
            ezOtcChatMsg.setSendText(url);
        }
        return BaseResponse.success();
    }
}
