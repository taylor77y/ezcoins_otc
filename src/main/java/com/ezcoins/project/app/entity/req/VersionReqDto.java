package com.ezcoins.project.app.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/23 14:10
 * @Version:1.0
 */
@Data
public class VersionReqDto {
    @ApiModelProperty(value = "平台(0:Android 1:IOS)")
    private String platform;

    @ApiModelProperty(value = "当前版本")
    private String thisVersion;

}
