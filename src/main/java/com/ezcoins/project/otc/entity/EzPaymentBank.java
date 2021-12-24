package com.ezcoins.project.otc.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *       支付、微信、银行卡都在此表
 * </p>
 *
 * @author wanglei
 * @since 2021-06-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ez_payment_bank")
@ApiModel(value="EzPaymentBank对象", description="")
public class EzPaymentBank implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "银行信息id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "卖家姓名")
    private String realName;

    @ApiModelProperty(value = "银行名")
    private String bankName;

    @ApiModelProperty(value = "账户名")
    private String accountName;

    @ApiModelProperty(value = "账户号")
    private String number;

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
