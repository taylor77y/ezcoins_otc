package com.ezcoins.project.app.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/5 19:45
 * @Version:1.0
 */
@Data
public class AppVersionReqDto  implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "版本编号")
    private String id;

    @ApiModelProperty(value = "产品名",required = true)
    @NotBlank(message = "{产品名不能为空}")
    private String name;

    @ApiModelProperty(value = "当前版本",required = true)
    @NotBlank(message = "{版本不能为空}")
    private String thisVersion;

    @ApiModelProperty(value = "平台(0:Android 1:IOS)",required = true)
    @NotBlank(message = "{平台不能为空}")
    private String platform;

    @ApiModelProperty(value = "允许最低版本",required = true)
    @NotBlank(message = "{允许最低版本不能为空}")
    private String minVersion;

    @ApiModelProperty(value = "下载地址",required = true)
    @NotBlank(message = "{下载地址不能为空}")
    private String addr;

    @ApiModelProperty(value = "中文描述",required = true)
    @NotBlank(message = "{中文描述不能为空}")
    private String description;

    @ApiModelProperty(value = "英文描述",required = true)
    @NotBlank(message = "{英文描述不能为空}")
    private String descriptionEn;

    @ApiModelProperty(value = "创建者",required = true)
    @NotBlank(message = "{创建者不能为空}")
    private String createBy;
}
