package com.ezcoins.project.acl.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/7 13:37
 * @Version:1.0
 */
@Data
public class AclMenuReqDto {
    /**
     * 菜单id
     */
    @ApiModelProperty(value = "菜单id",required = false)
    private Integer menuId;

    @ApiModelProperty(value = "路由",required = true)
    @NotBlank(message = "{路由不能为空}")
    private String router;

    @ApiModelProperty(value = "图标",required = true)
    @NotBlank(message = "{图标不能为空}")
    private String icon;


    @ApiModelProperty(value = "名称",required = true)
    @NotBlank(message = "{名称不能为空}")
    private String title;

    @ApiModelProperty(value = "英文名称",required = true)
    @NotBlank(message = "{英文名称不能为空}")
    private String titleEn;


    @ApiModelProperty(value = "权限类型 1菜单 2 权限 3 按钮",required = true)
    @NotNull(message = "{权限类型不能为空}")
    private Integer type;


    @ApiModelProperty(value = "权限标识",required = true)
    @NotBlank(message = "{权限标识不能为空}")
    private String code;


    @ApiModelProperty(value = "父级id",required = true)
    @NotNull(message = "{父级id不能为空}")
    private Integer parentId;
}
