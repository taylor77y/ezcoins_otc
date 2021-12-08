package com.ezcoins.project.coin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 钱包地址表
 * </p>
 *
 * @author wanglei
 * @since 2021-06-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("coin_wallet")
@ApiModel(value="Wallet对象", description="钱包地址表")
public class Wallet implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "钱包类型（online=在线签名 offline=离线签名 thirdparty=第三方）")
    private String walletType;

    @ApiModelProperty(value = "主币种类型")
    private String mainCoinType;

    @ApiModelProperty(value = "钱包地址")
    private String address;

    @ApiModelProperty(value = "创建者")
    private String createBy;

    @ApiModelProperty(value = "更新者")
    private String updateBy;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


}
