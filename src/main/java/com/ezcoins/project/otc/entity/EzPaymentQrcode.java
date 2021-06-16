package com.ezcoins.project.otc.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户 支付宝信息
 * </p>
 *
 * @author wanglei
 * @since 2021-06-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ez_payment_qrcode")
@ApiModel(value="EzPaymentQrcode对象", description="用户 二维码 类型的收款方式")
public class EzPaymentQrcode implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "二维码收款方式编号")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "收款二维码类型id： 2：支付宝 3：微信")
    private String paymentMethodId;

    @ApiModelProperty(value = "卖家姓名")
    private String realName;

    @ApiModelProperty(value = "付款二维码")
    private String paymentQrCode;

    @ApiModelProperty(value = "账号")
    private String accountNumber;

    @ApiModelProperty(value = "状态(0:已激活 1：未激活)")
    private String status;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
