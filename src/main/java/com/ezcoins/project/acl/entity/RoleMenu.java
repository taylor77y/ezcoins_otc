package com.ezcoins.project.acl.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
@TableName("acl_role_menu")
@ApiModel(value="RoleMenu对象", description="")
public class RoleMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    private String roleId;

    private String menuId;


}
