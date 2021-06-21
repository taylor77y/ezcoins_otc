package com.ezcoins.project.otc.entity;

import com.baomidou.mybatisplus.annotation.*;

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

    @ApiModelProperty(value = "商户名")
    private String name;

    @ApiModelProperty(value = "安全密码")
    private String securityPassword;

    @ApiModelProperty(value = "卖单数量")
    private Integer sellCount;

    @ApiModelProperty(value = "买单数量")
    private Integer buyCount;

    @ApiModelProperty(value = "是否加V/广告权限 （0:已加V  1:未加V）")
    private String plusV;

    @ApiModelProperty(value = "30天成交单数")
    private Integer monthCount;

    @ApiModelProperty(value = "30天总完成率")
    private Double mouthFinishRate;

    @ApiModelProperty(value = "30天买总完成率")
    private Double mouthFinishBuyRate;

    @ApiModelProperty(value = "30日平均取消时间")
    private Integer mouthAverageCancel;

    @ApiModelProperty(value = "30日平均放行时间")
    private Integer mouthAveragePass;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


    @ApiModelProperty(value = "乐观锁 请忽略它的存在")
    @Version
    private Integer version;


}
