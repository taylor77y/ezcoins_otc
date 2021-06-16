package com.ezcoins.project.coin.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
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
 * @since 2021-06-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("coin_type")
@ApiModel(value="Type对象", description="币种类型表")
public class Type implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "币种ID")
      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "币种名")
    private String coinName;

    @ApiModelProperty(value = "币种全称")
    private String coinFullName;

    @ApiModelProperty(value = "最少提现数量")
    private BigDecimal withdrawalMin;

    @ApiModelProperty(value = "提现手续费[固定]")
    private BigDecimal withdrawalFee;

    @ApiModelProperty(value = "提现手续费率[比例]")
    private BigDecimal withdrawalFeeRate;

    @ApiModelProperty(value = "币种状态（0启用 1禁用 ）")
    private String status;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;


}
