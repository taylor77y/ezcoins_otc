package com.ezcoins.project.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 登录日志
 * </p>
 *
 * @author wanglei
 * @since 2021-06-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="EzSysLogininfor对象", description="登录日志")
@TableName("ez_sys_logininfor")
public class EzSysLogininfor implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "访问ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "用户账号")
    private String userName;

    @ApiModelProperty(value = "用户类型")
    private String userType;

    @ApiModelProperty(value = "登录IP地址")
    private String ipaddr;

    @ApiModelProperty(value = "登录地点")
    private String loginLocation;

    @ApiModelProperty(value = "浏览器类型")
    private String browser;

    @ApiModelProperty(value = "操作系统")
    private String os;

    @ApiModelProperty(value = "访问时间")
    private Date createTime;


}
