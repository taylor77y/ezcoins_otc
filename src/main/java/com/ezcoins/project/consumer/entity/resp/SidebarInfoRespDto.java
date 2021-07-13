package com.ezcoins.project.consumer.entity.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/9 18:18
 * @Version:1.0
 */
@Data
public class SidebarInfoRespDto {

    @ApiModelProperty(value = "UID")
    private String userId;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "区号")
    private String phoneArea;

}
