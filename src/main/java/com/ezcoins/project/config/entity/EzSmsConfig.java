package com.ezcoins.project.config.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author wanglei
 * @since 2021-06-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="ezcoinsSmsConfig对象", description="邮件设置")
@TableName("ez_sms_Config")
public class EzSmsConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "短信名")
    private String un;

    @ApiModelProperty(value = "密码")
    private String pwd;

    @ApiModelProperty(value = "发送信息")
    private String msg;

    @ApiModelProperty(value = "类型（1 - ASCII（英语、马来语等） 153 个字符 2 - Unicode（中文、日语等）63 个字符）")
    private String type;

    @ApiModelProperty(value = "要显示在收件人手机上的发件人 ID")
    private String sendid;

    @ApiModelProperty(value = "商定期限")
    private String agreedterm;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "创建者")
    private String createBy;

    @ApiModelProperty(value = "更新者")
    private String updateBy;

    @TableField(exist = false)
    @ApiModelProperty(value = "余额")
    private BigDecimal balance;


}
