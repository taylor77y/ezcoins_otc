package com.ezcoins.project.consumer.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/9 18:53
 * @Version:1.0
 */
@Data
public class PasswordUpdateReqDto {
    /**
     * 安全密码
     */
    @ApiModelProperty(value = "密码")
    private String password;

    /** 新密码**/
    @ApiModelProperty(value = "新密码")
    private String newPassword;

}
