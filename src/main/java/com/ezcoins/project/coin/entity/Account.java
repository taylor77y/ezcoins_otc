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
 * 资产余额表
 * </p>
 *
 * @author wanglei
 * @since 2021-06-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("coin_account")
@ApiModel(value="Account对象", description="资产余额表")
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

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
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;


}
