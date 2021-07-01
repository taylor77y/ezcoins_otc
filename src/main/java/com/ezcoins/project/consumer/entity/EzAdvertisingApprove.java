package com.ezcoins.project.consumer.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 高级认证表
 * </p>
 *
 * @author wanglei
 * @since 2021-06-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="EzAdvertisingApprove对象", description="高级认证表")
public class EzAdvertisingApprove implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "保证金")
    private BigDecimal margin;

    @ApiModelProperty(value = "审核失败 备注")
    private String memo;

    @ApiModelProperty(value = "乐观锁（申请次数）")
    private Integer version;

    @ApiModelProperty(value = "2拒绝 1通过 0待审核")
    private String status;

    @ApiModelProperty(value = "审核人")
    private String examineBy;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "审核时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date examineTime;

}
