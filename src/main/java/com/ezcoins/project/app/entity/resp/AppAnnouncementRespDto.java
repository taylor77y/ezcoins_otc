package com.ezcoins.project.app.entity.resp;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/10 19:46
 * @Version:1.0
 */
@Data
public class AppAnnouncementRespDto {
    @ApiModelProperty(value = "公告id")
    private String id;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "英文标题")
    private String titleEn;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "英文内容")
    private String contentEn;

    @ApiModelProperty(value = "是否撤销(1:已撤销 0：正常)")
    private String ifCancel;

    @ApiModelProperty(value = "撤销时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date cancelTime;

    @ApiModelProperty(value = "通过用户类型 (0: 全部用户)")
    private String userType;

    @ApiModelProperty(value = "优先级（1：紧急:2：高, 3：普通.）")
    private Integer priority;

    @ApiModelProperty(value = "公告类型（0：正常公告）")
    private String type;

    @ApiModelProperty(value = "逻辑删除 1（true）已删除， 0（false）未删除")
    @TableLogic(value = "0",delval = "1")
    private String isDeleted;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "创建者")
    private String createBy;
}
