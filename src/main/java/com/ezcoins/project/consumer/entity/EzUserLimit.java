package com.ezcoins.project.consumer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 封号列表
 * </p>
 *
 * @author wanglei
 * @since 2021-07-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="EzUserLimit对象", description="封号列表")
public class EzUserLimit implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String userId;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "登录状态  0：未封号 1：已封号")
    private String login;

    @ApiModelProperty(value = "提现状态  0：未封号 1：已封号")
    private String withdraw;

    @ApiModelProperty(value = "发布广告封禁 提现状态  0：未封号 1：已封号")
    private String order;

    @ApiModelProperty(value = "买卖封禁 提现状态  0：未封号 1：已封号")
    private String business;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;


}
