package com.ezcoins.project.acl.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

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
@TableName("acl_user_role")
@ApiModel(value="UserRole对象", description="")
public class UserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "管理员id")
    private String adminId;

    @ApiModelProperty(value = "角色id")
    private String roleId;


}
