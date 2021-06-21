package com.ezcoins.project.otc.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/21 10:39
 * @Version:1.0
 */
@Data
public class ChatMsgReqDto {
    @ApiModelProperty(value = "匹配订单号")
    private String orderMatchNo;

    @ApiModelProperty(value = "发送者id")
    private String sendUserId;

    @ApiModelProperty(value = "接收者id")
    private String receiveUserId;

    @ApiModelProperty(value = "发送时间")
    private Date createTime;

    @ApiModelProperty(value = "文字内容")
    private String sendText;

    @ApiModelProperty(value = "上传图片内容")
    private MultipartFile file;

    @ApiModelProperty(value = "内容类型(0:图片 1：文字)")
    private String type;

}
