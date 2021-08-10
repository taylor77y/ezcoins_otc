package com.ezcoins.project.otc.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 一键卖币配置
 * </p>
 *
 * @author wanglei
 * @since 2021-06-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="EzOneSellConfig对象", description="一键卖币配置")
public class EzOneSellConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "配置Id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "手续费【固定】")
    private BigDecimal fee;

    @ApiModelProperty(value = "手续费【比例】")
    private BigDecimal feeRatio;

    @ApiModelProperty(value = "单价【人民币】")
    private BigDecimal price;

    @ApiModelProperty(value = "最少数量")
    private BigDecimal minAmount;

    @ApiModelProperty(value = "最大数量")
    private BigDecimal maxAmount;

    @ApiModelProperty(value = "币种")
    private String coinName;

    @ApiModelProperty(value = "0:开启  1：关闭")
    private String status;


    @ApiModelProperty(value = "付款期限(分钟)")
    private Integer prompt;


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
