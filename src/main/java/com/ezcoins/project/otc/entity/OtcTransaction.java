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
@TableName("otc_transaction")
@ApiModel(value="OtcTransaction对象", description="次级菜单-OTC交易")
public class OtcTransaction extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "otc交易id")
    @TableId(value = "transaction_id", type = IdType.ASSIGN_ID)
    private String transactionId;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "用户类型")
    private String userType;

    @ApiModelProperty(value = "设备来源")
    private String deviceSource;

    @ApiModelProperty(value = "发起时间")
    private String launchTime;

    @ApiModelProperty(value = "状态 0订单已发起 1已付款 2确认中 3已取消 4确认完成 5完成交易")
    private String status;

//    @ApiModelProperty(value = "更新时间")
//    private String updateTime;


}
