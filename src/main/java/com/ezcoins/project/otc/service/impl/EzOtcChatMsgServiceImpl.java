package com.ezcoins.project.otc.service.impl;

import com.ezcoins.config.EzCoinsConfig;
import com.ezcoins.config.ServerConfig;
import com.ezcoins.project.common.service.OssService;
import com.ezcoins.project.otc.entity.EzOtcChatMsg;
import com.ezcoins.project.otc.entity.req.ChatMsgReqDto;
import com.ezcoins.project.otc.mapper.EzOtcChatMsgMapper;
import com.ezcoins.project.otc.service.EzOtcChatMsgService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.response.BaseResponse;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.utils.FileUploadUtils;
import com.ezcoins.websocket.WebSocketHandle;
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
    private ServerConfig serverConfig;

    /**
     * 发送 聊天信息 文字/图片 内容类型(0:图片 1：文字)
     *
     * @param msgReqDto
     * @return
     */
    @Override
    public BaseResponse sendChat(ChatMsgReqDto msgReqDto) {
        EzOtcChatMsg ezOtcChatMsg = new EzOtcChatMsg();
        BeanUtils.copyBeanProp(ezOtcChatMsg, msgReqDto);
        //判断聊天信息是否图片
        String type = msgReqDto.getType();
        if ("0".equals(type)) {
            // 上传文件路径
            try {
                String filePath = EzCoinsConfig.getUploadPath();
                // 上传并返回新文件名称
                String fileName = FileUploadUtils.upload(filePath, msgReqDto.getFile());
                String url = serverConfig.getUrl() + fileName;
                ezOtcChatMsg.setSendText(url);
            } catch (Exception e) {
                return BaseResponse.error(e.getMessage());
            }
        }
        //给用户一个信号
        WebSocketHandle.toChatWith(msgReqDto.getReceiveUserId(), msgReqDto.getOrderMatchNo());
        return BaseResponse.success();
    }
}
