package com.ezcoins.project.app.entity.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/10 18:59
 * @Version:1.0
 */
@Data
public class AppVersionRespDto {
    @ApiModelProperty(value = "版本编号")
    private String id;

    @ApiModelProperty(value = "当前版本")
    private String thisVersion;

    @ApiModelProperty(value = "允许最低版本")
    private String minVersion;

    @ApiModelProperty(value = "平台(0:Android 1:IOS)")
    private String platform;

    @ApiModelProperty(value = "下载地址")
    private String addr;

    @ApiModelProperty(value = "app内容",required = true)
    private String description;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "默认版本（0：默认 1：非默认）用于版本回滚")
    private String isDefault;


}
