package com.ezcoins.project.consumer.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 封号记录表
 * </p>
 *
 * @author wanglei
 * @since 2021-06-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ez_user_limit_log")
@ApiModel(value="EzUserLimitLog对象", description="封号记录表")
public class EzUserLimitLog implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "详细")
    private String detailed;

    @ApiModelProperty(value = "封禁类型( 登录封禁：1)")
    private String type;

    @ApiModelProperty(value = "帐号状态（0正常 1停用）")
    private String status;

    @ApiModelProperty(value = "操作人")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

}
