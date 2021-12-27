package com.ezcoins.project.otc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ezcoins.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 *       次级菜单-OTC订单
 * </p>
 *
 * @author taylor
 * @since 2021-12-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("otc_order")
@ApiModel(value="OtcOrder对象", description="次级菜单-OTC订单")
public class OtcOrder extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "otc交易id")
    @TableId(value = "order_no", type = IdType.ASSIGN_ID)
    private String orderNo;

    @ApiModelProperty(value = "otc交易id")
    private String transactionId;

    @ApiModelProperty(value = "交易数字货币币种")
    private String coinType;

    @ApiModelProperty(value = "交易数字货币数量")
    private String coinAmount;

    @ApiModelProperty(value = "汇率")
    private String exchangeRate;

    @ApiModelProperty(value = "处理费")
    private String fee;

    @ApiModelProperty(value = "发起时间")
    private String launchTime;

    @ApiModelProperty(value = "状态")
    private String status;

//    @ApiModelProperty(value = "更新时间")
//    private String updateTime;

    @ApiModelProperty(value = "对话内容")//点击弹出双方于订单内的留言
    private String dialogueContent;


}
