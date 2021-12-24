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
//@Entity
//@Table(name = "ez_user_limit_log")
public class EzUserLimitLog implements Serializable {

    private static final long serialVersionUID = 1L;

    // Regex for acceptable usernames
    public static final String USERNAME_REGEX = "^[_'.@A-Za-z0-9-]*$";

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
//    @SequenceGenerator(name = "sequenceGenerator")
//    @Column(name = "id")
    private Integer id;

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "user_id", unique = true, nullable = false)
    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "用户名")
//    @NotNull
//    @Pattern(regexp = USERNAME_REGEX)
//    @Size(min = 1, max = 50)
//    @Column(name = "user_name", length = 50, unique = true, nullable = false)
    private String userName;

    @ApiModelProperty(value = "详细")
//    @Column(name = "detailed", nullable = false)
    private String detailed;

    @ApiModelProperty(value = "封禁类别（0：登录封禁 1：提现封禁 2：发布广告封禁 3：买卖封禁）")
//    @Column(name = "type", nullable = false)
    private String type;

    @ApiModelProperty(value = "是否过期 1（true）已过期 ，0（false）未过期")
//    @Column(name = "is_expire", nullable = false)
    private String isExpire;

    @ApiModelProperty(value = "封号操作者")
//    @Column(name = "create_by", nullable = false)
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @ApiModelProperty(value = "封号到期时间（null：永久封号）")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @Column(name = "ban_time", nullable = false)
    private Date banTime;

}
