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
 * 资产余额表
 * </p>
 *
 * @author wanglei
 * @since 2021-06-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("coin_account")
@ApiModel(value="Account对象", description="资产余额表")
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "币种ID")
    private Integer coinId;

    @ApiModelProperty(value = "币种名")
    private String coinName;

    @ApiModelProperty(value = "余额")
    private BigDecimal available;

    @ApiModelProperty(value = "冻结")
    private BigDecimal frozen;

    @ApiModelProperty(value = "锁仓")
    private BigDecimal lockup;

    @ApiModelProperty(value = "版本号")
    private Integer version;

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

    @ApiModelProperty(value = "更新者")
    private String updateBy;


}
