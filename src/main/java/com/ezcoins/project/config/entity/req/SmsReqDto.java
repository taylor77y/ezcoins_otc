package com.ezcoins.project.config.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/8 16:59
 * @Version:1.0
 */
@Data
public class SmsReqDto {

    @ApiModelProperty(value = "id",required = true)
    @NotBlank(message = "{id不能为空}")
    private Integer id;

    @ApiModelProperty(value = "短信名",required = true)
    @NotBlank(message = "{用户名不能为空}")
    private String un;

    @ApiModelProperty(value = "密码",required = true)
    @NotBlank(message = "{密码不能为空}")
    private String pwd;

    @ApiModelProperty(value = "发送信息",required = true)
    @NotBlank(message = "{发送信息模板不能为空}")
    private String msg;

    @ApiModelProperty(value = "类型（1 - ASCII（英语、马来语等） 153 个字符 2 - Unicode（中文、日语等）63 个字符）",required = true)
    @NotBlank(message = "{类型不能为空}")
    private String type;

    @ApiModelProperty(value = "要显示在收件人手机上的发件人 ID")
    private String sendid;

    @ApiModelProperty(value = "商定期限",required = true)
    @NotBlank(message = "{商定期限不能为空}")
    private String agreedterm;
}
