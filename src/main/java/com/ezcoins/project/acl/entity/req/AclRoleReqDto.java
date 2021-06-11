package com.ezcoins.project.acl.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/7 13:58
 * @Version:1.0
 */
@Data
public class AclRoleReqDto {
    @ApiModelProperty(value = "角色id",required = false)
    private String roleId;


    @ApiModelProperty(value = "角色名称",required = true)
    @NotBlank(message = "{角色名称不能为空}")
    private String name;


    @ApiModelProperty(value = "路由集合",required = true)
    @NotNull(message = "{路由集合不能为空}")
    private List<Integer> menuIds;


    @ApiModelProperty(value = "子路由数组（修改角色时使用）",required = false)
    private List<Integer> childrenMenuId;

}
