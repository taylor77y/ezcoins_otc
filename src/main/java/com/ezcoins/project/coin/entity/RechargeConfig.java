package com.ezcoins.project.coin.entity;

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
 * 
 * </p>
 *
 * @author wanglei
 * @since 2021-06-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("coin_recharge_config")
@ApiModel(value="RechargeConfig对象", description="充值配置表")
public class RechargeConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "充值配置id")
      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "币种名")
    private String coinName;

    @ApiModelProperty(value = "主币种类型")
    private String mainCoinType;

    @ApiModelProperty(value = "子币种类型")
    private String coinType;

    @ApiModelProperty(value = "充币类型 0开放 1 关闭")
    private String status;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "创建者")
    private String createBy;

    @ApiModelProperty(value = "更新者")
    private String updateBy;


}
