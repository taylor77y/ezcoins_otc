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
 * 资产记录表
 * </p>
 *
 * @author wanglei
 * @since 2021-06-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("coin_record")
@ApiModel(value="Record对象", description="资产记录表")
public class Record implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "到达用户ID（内转使用）")
    private Integer toId;

    @ApiModelProperty(value = "从")
    private String fromUser;

    @ApiModelProperty(value = "币种ID")
    private Integer coinId;

    @ApiModelProperty(value = "币种名")
    private String coinName;

    @ApiModelProperty(value = "收支类型（1收入 2支出）")
    private String incomeType;

    @ApiModelProperty(value = "主类型")
    private String mainType;

    @ApiModelProperty(value = "子类型")
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

    @ApiModelProperty(value = "状态（1成功）")
    private String status;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;


}
