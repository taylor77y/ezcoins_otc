package com.ezcoins.project.config.entity;

import java.util.Date;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author wanglei
 * @since 2021-06-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ez_country_config")
@ApiModel(value="EzCountryConfig对象", description="国家电话区号配置 表")
public class EzCountryConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "国家中文名")
    private String countryName;

    @ApiModelProperty(value = "国家英文名")
    private String countryNameEn;

    @ApiModelProperty(value = "国家编号")
    private String countryCode;

    @ApiModelProperty(value = "货币代码")
    private String currencyCode;

    @ApiModelProperty(value = "国家电话区号")
    private String countryTelCode;

    @ApiModelProperty(value = "国旗地址")
    private String nationalFlagAddr;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "逻辑删除 1（true）已删除，0（false）未删除")
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


}
