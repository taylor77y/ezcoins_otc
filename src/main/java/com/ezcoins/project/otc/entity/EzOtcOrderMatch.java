package com.ezcoins.project.otc.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.Version;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 匹配日OTC订单
 * </p>
 *
 * @author wanglei
 * @since 2021-06-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="EzOtcOrderMatch对象", description="匹配日OTC订单")
public class EzOtcOrderMatch implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "匹配订单号")
    @TableId(value = "orderMatchNo")
    private String orderMatchNo;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "匹配到的发布订单号")
    private String orderNo;

    @ApiModelProperty(value = "广告发布订单用户id")
    private String otcOrderUserId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "支付时间")
    private Date paymentTime;

    @ApiModelProperty(value = "完成时间")
    private Date finishTime;

    @ApiModelProperty(value = "卖家昵称")
    private String advertisingName;

    @ApiModelProperty(value = "数量")
    private BigDecimal amount;

    @ApiModelProperty(value = "总价")
    private BigDecimal totalPrice;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    @ApiModelProperty(value = "订单状态(1:接单已取消 2:待接单 3：已取消 4：等待支付 5：已支付 6：已完成)")
    private String status;

    @ApiModelProperty(value = "币种类型")
    private String coinName;

    @ApiModelProperty(value = "订单到期时间")
    private Date dueTime;

    @ApiModelProperty(value = "乐观锁")
    @Version
    private Integer version;

}
