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
 * 提现配置表
 * </p>
 *
 * @author wanglei
 * @since 2021-06-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("coin_withdraw_config")
@ApiModel(value="WithdrawConfig对象", description="提现配置表")
public class WithdrawConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "提币币种配置ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "币种名")
    private String coinName;

    @ApiModelProperty(value = "币种全称")
    private String coinFullName;

    @ApiModelProperty(value = "主币种类型")
    private String mainCoinType;

    @ApiModelProperty(value = "子币种类型")
    private String coinType;

    @ApiModelProperty(value = "提现手续费[固定]")
    private BigDecimal fee;

    @ApiModelProperty(value = "提现手续费率[比例]")
    private BigDecimal feeRate;

    @ApiModelProperty(value = "最大提币数量")
    private BigDecimal maxWithdraw;

    @ApiModelProperty(value = "最小提币数量")
    private BigDecimal minWithdraw;

    @ApiModelProperty(value = "单日限额 0就不限制")
    private BigDecimal singleDayLimit;

    @ApiModelProperty(value = "提币状态 0开放 1 关闭")
    private String status;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private String createBy;

    private String updateBy;


}
