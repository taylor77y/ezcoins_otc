package com.ezcoins.project.consumer.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

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
//@Entity
//@Table(name = "ez_user_limit")
public class EzUserLimit implements Serializable {

    private static final long serialVersionUID = 1L;

    // Regex for acceptable usernames
    public static final String USERNAME_REGEX = "^[_'.@A-Za-z0-9-]*$";

    @ApiModelProperty(value = "用户id")
    @TableId(value = "user_id", type = IdType.ASSIGN_ID)
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;

    @ApiModelProperty(value = "用户名")
//    @NotNull
//    @Pattern(regexp = USERNAME_REGEX)
//    @Size(min = 1, max = 50)
//    @Column(name = "user_name", length = 50, unique = true, nullable = false)
    private String userName;

    @ApiModelProperty(value = "登录状态  0：未封号 1：已封号")
//    @Column(name = "login", nullable = false)
    private String login;

    @ApiModelProperty(value = "提现状态  0：未封号 1：已封号")
//    @Column(name = "withdraw", nullable = false)
    private String withdraw;

    @ApiModelProperty(value = "发布广告封禁 提现状态  0：未封号 1：已封号")
//    @Column(name = "orders", nullable = false)
    private String orders;

    @ApiModelProperty(value = "买卖封禁 提现状态  0：未封号 1：已封号")
//    @Column(name = "business", nullable = false)
    private String business;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @Column(name = "update_time", nullable = false)
    private Date updateTime;



}
