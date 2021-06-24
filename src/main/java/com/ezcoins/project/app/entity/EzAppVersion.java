package com.ezcoins.project.app.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author wanglei
 * @since 2021-06-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ez_app_version")
@ApiModel(value="EzAppVersion对象", description="app版本表")
public class EzAppVersion implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "版本编号")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "产品名")
    private String name;

    @ApiModelProperty(value = "平台(0:Android 1:IOS)")
    private String platform;

    @ApiModelProperty(value = "版本")
    private String thisVersion;

    @ApiModelProperty(value = "允许最低版本")
    private String minVersion;

    @ApiModelProperty(value = "默认版本（0：默认 1：非默认）用于版本回滚")
    private String isDefault;

    @ApiModelProperty(value = "是否上架(1:上架 0:下架)")
    private String isRacking;

    @ApiModelProperty(value = "下载地址")
    private String addr;

    @ApiModelProperty(value = "中文描述")
    private String content;

    @ApiModelProperty(value = "英文描述")
    private String contentEn;

    @ApiModelProperty(value = "中文标题")
    private String title;

    @ApiModelProperty(value = "英文标题")
    private String titleEn;

    @ApiModelProperty(value = "创建者")
    private String createBy;

    @ApiModelProperty(value = "更新者")
    private String updateBy;

    @ApiModelProperty(value = "逻辑删除 1（true）已删除， 0（false）未删除")
    @TableLogic(value = "0",delval = "1")
    private String isDeleted;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public void setDescription(String description) {
        String locale = LocaleContextHolder.getLocale().toString();

    }





}
