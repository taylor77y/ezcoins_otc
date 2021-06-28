package com.ezcoins.project.otc.entity.req;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/28 16:10
 * @Version:1.0
 */
@Data
public class SellConfigReqDto {
    @ApiModelProperty(value = "配置Id")
    private Integer id;

    @ApiModelProperty(value = "手续费【固定】")
    private BigDecimal fee;

    @ApiModelProperty(value = "手续费【比例】")
    private BigDecimal feeRatio;

    @ApiModelProperty(value = "最少数量")
    private BigDecimal minAmount;

    @ApiModelProperty(value = "最大数量")
    private BigDecimal maxAmount;

    @ApiModelProperty(value = "单价【人民币】")
    private BigDecimal price;

    @ApiModelProperty(value = "币种")
    private String coinName;


    @ApiModelProperty(value = "0:关闭  1：开启")
    private String status;
}
