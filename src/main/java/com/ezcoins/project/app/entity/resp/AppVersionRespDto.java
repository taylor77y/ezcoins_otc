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

    @ApiModelProperty(value = "当前版本")
    private String thisVersion;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "下载地址")
    private String addr;

    @ApiModelProperty(value = "app更新内容")
    private String content;

}
