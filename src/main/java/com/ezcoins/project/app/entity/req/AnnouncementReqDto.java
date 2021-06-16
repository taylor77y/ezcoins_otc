package com.ezcoins.project.app.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/10 16:24
 * @Version:1.0
 */
@Data
public class AnnouncementReqDto implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "标题")
    @NotBlank(message = "{标题不能为空}")
    private String title;

    @ApiModelProperty(value = "英文标题")
    @NotBlank(message = "{英文标题不能为空}")
    private String titleEn;

    @ApiModelProperty(value = "内容")
    @NotBlank(message = "{内容不能为空}")
    private String content;

    @ApiModelProperty(value = "英文内容")
    @NotBlank(message = "{英文内容不能为空}")
    private String contentEn;

    @ApiModelProperty(value = "通知用户类型 (0: 全部用户) 目前只有 0：全部用户")
    @NotBlank(message = "{通知用户类型不能为空}")
    private String userType;

    @ApiModelProperty(value = "优先级（1：紧急:2：高, 3：普通.）")
    @NotNull(message = "{优先级不能为空}")
    private Integer priority;

    @ApiModelProperty(value = "公告类型（0：正常公告 1:  2:）目前只有 0：正常公告")
    @NotBlank(message = "{公告类型不能为空}")
    private String type;
}
