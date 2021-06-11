package com.ezcoins.project.app.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/9 10:50
 * @Version:1.0
 */
@Data
public class UploadVersionReqDto {
    @ApiModelProperty(value = "平台(0:Android 1:IOS)",required = true)
    private String platform;

    @ApiModelProperty(value = "版本编号",required = false)
    private String id;
}
