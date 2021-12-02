package com.ezcoins.project.acl.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.ezcoins.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
@TableName("acl_menu")
@ApiModel(value="Menu对象", description="")
public class Menu extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "菜单id")
    @TableId(value = "menu_id", type = IdType.AUTO)
    private Integer menuId;

    @ApiModelProperty(value = "菜单code码")
    private String code;

    @ApiModelProperty(value = "路由")
    private String router;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "名称")
    private String title;

    @ApiModelProperty(value = "英文名称")
    private String titleEn;

    @ApiModelProperty(value = "1菜单 2 权限 3 按钮")
    private Integer type;

    @ApiModelProperty(value = "路由深度")
    private Integer depth;

    @ApiModelProperty(value = "父级id")
    private Integer parentId;

    /*@ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "创建者")
    private String createBy;

    @ApiModelProperty(value = "更新者")
    private String updateBy;*/

    @TableField(exist = false)
    @ApiModelProperty(value = "子节点")
    private List<Menu> children;

}
