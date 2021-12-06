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
 *       支付、微信、银行卡都在此表
 * </p>
 *
 * @author wanglei
 * @since 2021-06-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ez_internet_account")
@ApiModel(value="EzInternetAccount对象", description="")
public class EzInternetAccount extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "银行信息id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "网络账号姓名")
    private String internetAccountName;

    @ApiModelProperty(value = "网络账号号码")
    private String internetAccountNumber;

    @ApiModelProperty(value = "网络账号类型")
    private String internetAccountType;

    @ApiModelProperty(value = "状态(0:已激活 1：未激活)")
    private String status;
}
