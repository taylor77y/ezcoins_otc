package com.ezcoins.project.otc.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

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
@TableName("ez_payment_info")
@ApiModel(value="EzPaymentInfo", description="支付方式详情")
public class EzPaymentInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "支付方式详情编号")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "收款二维码类型id：1：银行  2：支付宝 3：微信")
    @NotBlank(message = "请先选择收款方式")
    private Integer paymentMethodId;

    @ApiModelProperty(value = "卖家真实姓名")
    private String realName;

    @ApiModelProperty(value = "开户银行名")
    private String bankName;

    @ApiModelProperty(value = "银行地址")
    private String optional;

    @ApiModelProperty(value = "账号")
    private String accountNumber;

    @ApiModelProperty(value = "付款二维码")
    private String paymentQrCode;

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
