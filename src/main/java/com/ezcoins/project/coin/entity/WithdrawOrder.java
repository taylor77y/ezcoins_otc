package com.ezcoins.project.coin.entity;

import java.math.BigDecimal;

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
 * 
 * </p>
 *
 * @author wanglei
 * @since 2021-06-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("coin_withdraw_order")
@ApiModel(value="WithdrawOrder对象", description="")
public class WithdrawOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "提币订单id")
     @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "提币地址")
    private String address;

    @ApiModelProperty(value = "币种名")
    private String coinName;

    @ApiModelProperty(value = "主币种类型")
    private String mainCoinType;

    @ApiModelProperty(value = "子币种类型")
    private String coinType;

    @ApiModelProperty(value = "数量")
    private BigDecimal amount;

    @ApiModelProperty(value = "手续费")
    private BigDecimal fee;

    @ApiModelProperty(value = "状态 1待审核 2审核通过 3提币拒绝 4提币失败")
    private String status;

    @ApiModelProperty(value = "失败原因")
    private String reason;

    @ApiModelProperty(value = "创建者")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "审核者")
    private String updateBy;

}
