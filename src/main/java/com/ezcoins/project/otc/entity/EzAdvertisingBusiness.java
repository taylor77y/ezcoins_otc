package com.ezcoins.project.otc.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
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
 * @since 2021-06-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="EzAdvertisingBusiness对象", description="otc交易账号信息")
@TableName("ez_advertising_business")
public class EzAdvertisingBusiness implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商户号")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "卖家昵称")
    private String advertisingName;

    @ApiModelProperty(value = "安全密码")
    private String securityPassword;

    @ApiModelProperty(value = "卖单数量")
    private Integer sellCount;

    @ApiModelProperty(value = "买单数量")
    private Integer buyCount;

    @ApiModelProperty(value = "是否加V/广告权限 （0:已加V  1:未加V）")
    private String plusV;

    @ApiModelProperty(value = "总完成率")
    private Double finishRate;

    @ApiModelProperty(value = "买总完成率")
    private Double finishBuyRate;

    @ApiModelProperty(value = "平均放行时间 分钟")
    private Double averagePass;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "创建z")
    private String createBy;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "更新z")
    private String updateBy;

    @ApiModelProperty(value = "乐观锁 请忽略它的存在")
    @Version
    private Integer version;

    @ApiModelProperty(value = "保证金")
    private BigDecimal margin;
}
