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
 * 资产记录表
 * </p>
 *
 * @author wanglei
 * @since 2021-06-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("coin_record")
@ApiModel(value="Record对象", description="资产记录表")
public class Record implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "币种名")
    private String coinName;

    @ApiModelProperty(value = "收支类型（1收入 2支出）")
    private String incomeType;

    @ApiModelProperty(value = "主类型")
    private String mainType;

    @ApiModelProperty(value = "子类型  1")
    private String sonType;

    @ApiModelProperty(value = "数量")
    private BigDecimal amount;

    @ApiModelProperty(value = "来自地址")
    private String fromAddress;

    @ApiModelProperty(value = "到达地址")
    private String toAddress;

    @ApiModelProperty(value = "交易ID")
    private String txid;

    @ApiModelProperty(value = "备注")
    private String memo;

    @ApiModelProperty(value = "手续费")
    private BigDecimal fee;

    @ApiModelProperty(value = "状态（1成功） 2 待审核  3审核通过 4审核拒绝")
    private String status;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "创建者")
    private String createBy;




}
