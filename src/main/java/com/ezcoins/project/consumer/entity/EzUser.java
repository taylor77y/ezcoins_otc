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
 * 
 * </p>
 *
 * @author wanglei
 * @since 2021-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="登录用户", description="用户表")
@TableName("ez_user")
//@Entity
//@Table(name = "ez_user")
public class EzUser implements Serializable {

    private static final long serialVersionUID = 1L;

    // Regex for acceptable usernames
    public static final String USERNAME_REGEX = "^[_'.@A-Za-z0-9-]*$";

    @ApiModelProperty(value = "用户编号")
    @TableId(value = "user_id", type = IdType.ASSIGN_ID)
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
//    @SequenceGenerator(name = "sequenceGenerator")
//    @Column(name = "user_id")
    private String userId;

    @ApiModelProperty(value = "父级编号")
//    @Column(name = "parent_id", unique = true, nullable = false)
    private String parentId;

    @ApiModelProperty(value = "用户名")
//    @NotNull
//    @Pattern(regexp = USERNAME_REGEX)
//    @Size(min = 1, max = 50)
//    @Column(name = "user_name", length = 50, unique = true, nullable = false)
    private String userName;

    @ApiModelProperty(value = "用户昵称")
//    @Column(name = "nick_name", nullable = false)
    private String nickName;

    @ApiModelProperty(value = "密码")
//    @Column(name = "password", nullable = false)
    private String password;

    @ApiModelProperty(value = "电话")
//    @Column(name = "phone", nullable = false)
    private String phone;

    @ApiModelProperty(value = "电话国际区号")
//    @Column(name = "phone_area", nullable = false)
    private String phoneArea;

    @ApiModelProperty(value = "国家编号")
//    @Column(name = "country_code", nullable = false)
    private String countryCode;

    @ApiModelProperty(value = "邮箱")
//    @Column(name = "email", nullable = false)
    private String email;

    @ApiModelProperty(value = "邀请码")
//    @Column(name = "invite_code", nullable = false)
    private String inviteCode;//----------------------

    @ApiModelProperty(value = "高级认证：0：已认证 1：未认证")
//    @Column(name = "level", nullable = false)
    private String level;

    @ApiModelProperty(value = "状态：0已认证 1未认证")
//    @Column(name = "kyc_status", nullable = false)
    private String kycStatus;

    @ApiModelProperty(value = "状态 0 正常 1禁止")
//    @Column(name = "status", nullable = false)
    private String status;

    @ApiModelProperty(value = "逻辑删除 1（true）已删除， 0（false）未删除")
    @TableLogic(value = "0",delval = "1")
//    @Column(name = "is_deleted", nullable = false)
    private String isDeleted;

    @ApiModelProperty(value = "最后登录IP")
//    @NotNull
//    @Size(max = 50)
//    @Column(name = "login_ip", nullable = false)
    private String loginIp;

    @ApiModelProperty(value = "最后登录时间")
//    @Column(name = "login_date", nullable = false)
    private Date loginDate;

    @ApiModelProperty(value = "创建者")
//    @Column(name = "create_by", nullable = false)
    private String createBy;

    @ApiModelProperty(value = "更新者")
//    @Column(name = "update_by", nullable = false)
    private String updateBy;

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

    @ApiModelProperty(value = "备注")
//    @Column(name = "remark", nullable = false)
    private String remark;

    @ApiModelProperty(value = "登录状态  0：未封号 1：已封号")
    @TableField(exist = false)
//    @Column(name = "login", nullable = false)
    private String login;

    @ApiModelProperty(value = "提现状态  0：未封号 1：已封号")
    @TableField(exist = false)
//    @Column(name = "withdraw", nullable = false)
    private String withdraw;

    @ApiModelProperty(value = "发布广告封禁 提现状态  0：未封号 1：已封号")
    @TableField(exist = false)
//    @Column(name = "orders", nullable = false)
    private String orders;

    @ApiModelProperty(value = "买卖封禁 提现状态  0：未封号 1：已封号")
    @TableField(exist = false)
//    @Column(name = "business", nullable = false)
    private String business;


}
