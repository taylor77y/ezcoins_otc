package com.ezcoins.project.config.entity.req;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/11 17:31
 * @Version:1.0
 */
@Data
public class CountryReqDto {
    @ApiModelProperty(value = "国家中文名",required = true)
    @NotBlank(message = "国家中文名不能为空")
    private String countryName;

    @ApiModelProperty(value = "国旗地址",required = true)
    @NotBlank(message = "请先上传国旗图片")
    private String nationalFlagAddr;

    @ApiModelProperty(value = "国家英文名",required = true)
    @NotBlank(message = "国家英文名不能为空")
    private String countryNameEn;

    @ApiModelProperty(value = "国家编号",required = true)
    @NotBlank(message = "国家编号不能为空")
    private String countryCode;

    @ApiModelProperty(value = "货币代码")
    private String currencyCode;

    @ApiModelProperty(value = "国家电话区号",required = true)
    @NotBlank(message = "国家电话区号不能为空")
    private String countryTelCode;
}
