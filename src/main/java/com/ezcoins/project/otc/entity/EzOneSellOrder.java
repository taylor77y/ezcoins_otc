package com.ezcoins.project.otc.entity;

import java.math.BigDecimal;
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
 * 一键卖币订单
 * </p>
 *
 * @author wanglei
 * @since 2021-06-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="EzOneSellOrder对象", description="一键卖币订单")
public class EzOneSellOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "一键买卖订单号")
      @TableId(value = "one_sell_no", type = IdType.ASSIGN_ID)
    private String oneSellNo;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "1:待接单  2:拒接订单  3：待付款 4：已付款 5：完成")
    private String status;

    @ApiModelProperty(value = "卖家昵称")
    private String advertisingName;

    @ApiModelProperty(value = "数量")
    private BigDecimal amount;

    @ApiModelProperty(value = "总价")
    private BigDecimal totalPrice;

    @ApiModelProperty(value = "单价")
    private BigDecimal price;

    @ApiModelProperty(value = "币种类型")
    private String coinName;

    @ApiModelProperty(value = "乐观锁")
    private Integer version;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "支付时间")
    private Date paymentTime;

    @ApiModelProperty(value = "完成时间")
    private Date finishTime;


}
