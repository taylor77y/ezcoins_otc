package com.ezcoins.project.acl.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/7 10:13
 * @Version:1.0
 */
@Data
public class AclUserReqDto implements Serializable {
    @ApiModelProperty(value = "用户名",required = true)
    @NotBlank(message = "{用户名不能为空}")
    private String userName;

    @ApiModelProperty(value = "密码",required = true)
    @NotBlank(message = "{密码不能为空}")
    private String password;

    @ApiModelProperty(value = "角色ID",required = true)
    @NotBlank(message = "{角色ID不能为空}")
    private String roleId;

}
