package com.ezcoins.project.acl.entity;

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
 * @since 2021-06-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("acl_role")
@ApiModel(value="Role对象", description="")
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "角色id")
    @TableId(value = "role_id", type = IdType.ASSIGN_ID)
    private String roleId;

    @ApiModelProperty(value = "角色名称")
    private String name;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private String createBy;

    private String updateBy;

    @ApiModelProperty(value = "子路由数组（修改角色时使用）")
    private String childrenMenuId;

    @ApiModelProperty(value = "逻辑删除 1（true）已删除， 0（false）未删除")
    @TableLogic(value = "0",delval = "1")
    private Integer isDeleted;

    @TableField(exist = false)
    private String[] childrenMenuIds;

}
