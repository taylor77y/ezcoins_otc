package com.ezcoins.project.otc.entity.resp;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/7 14:27
 * @Version:1.0
 */
@Data
public class ChatMsgRespDto {
    @ApiModelProperty(value = "发送者id")
    private String sendName;

    @ApiModelProperty(value = "接收者id")
    private String receiveName;

    @ApiModelProperty(value = "发送者id")
    private String sendUserId;

    @ApiModelProperty(value = "接收者id")
    private String receiveUserId;

    @ApiModelProperty(value = "发送时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "发送内容")
    private String sendText;

    @ApiModelProperty(value = "内容类型(0:图片 1：文字)")
    private String type;

    @ApiModelProperty(value = "是否是系统消息(0 :是 1：否)")
    private String isSystem;
}
