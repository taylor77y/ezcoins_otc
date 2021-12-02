package com.ezcoins.project.coin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.ezcoins.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 币种类型表
 * </p>
 *
 * @author wanglei
 * @since 2021-06-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("coin_type")
@ApiModel(value="Type对象", description="币种类型表")
public class Type extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "币种ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "币种名")
    private String coinName;

    @ApiModelProperty(value = "币种全称")
    private String coinFullName;

    @ApiModelProperty(value = "otc交易手续费比例")
    private BigDecimal otcFeeRatio;

    @ApiModelProperty(value = "最大发布订单数量")
    private BigDecimal maxAmount;

    @ApiModelProperty(value = "最小发布订单数量")
    private BigDecimal minAmount;

    @ApiModelProperty(value = "otc状态（0启用 1禁用 ）")
    private String otcStatus;

    @ApiModelProperty(value = "币种状态（0启用 1禁用 ）")
    private String status;

    /*@ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "创建者")
    private String createBy;

    @ApiModelProperty(value = "更新者")
    private String updateBy;*/


}
