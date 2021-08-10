package com.ezcoins.project.consumer.entity;

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

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "详细")
    private String detailed;

    @ApiModelProperty(value = "封禁类别（0：登录封禁 1：提现封禁 2：发布广告封禁 3：买卖封禁）")
    private String type;

    @ApiModelProperty(value = "是否过期 1（true）已过期 ，0（false）未过期")
    private String isExpire;

    @ApiModelProperty(value = "封号操作者")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "封号到期时间（null：永久封号）")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date banTime;

}
