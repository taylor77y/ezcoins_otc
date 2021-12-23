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
 * 订单申诉
 * </p>
 *
 * @author wanglei
 * @since 2021-07-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ez_otc_order_appeal")
@ApiModel(value="EzOtcOrderAppeal对象", description="订单申诉")
public class EzOtcOrderAppeal extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "申诉id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "申诉类型 1:对方未付款 2：对方未放行 3:其他")
    private String type;

    @ApiModelProperty(value = "申诉理由")
    private String reason;

    @ApiModelProperty(value = "凭证地址")
    private String voucher;

    @ApiModelProperty(value = "匹配订单号")
    private String orderMatchNo;

    @ApiModelProperty(value = "审核状态(1:等待处理 2：取消申诉 3：申诉失败 4：申诉成功)")
    private String status;

    @ApiModelProperty(value = "审核人")
    private String examineBy;

    @ApiModelProperty(value = "处理结果")
    private String memo;

/*    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "创建z")
    private String createBy;*/


}
