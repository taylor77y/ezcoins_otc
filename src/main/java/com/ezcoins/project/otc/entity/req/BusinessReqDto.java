package com.ezcoins.project.otc.entity.req;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/8 17:37
 * @Version:1.0
 */
@Data
public class BusinessReqDto {
    @ApiModelProperty(value = "商户号")
    private String id;

    @ApiModelProperty(value = "卖单数量")
    private Integer sellCount;

    @ApiModelProperty(value = "买单数量")
    private Integer buyCount;

    @ApiModelProperty(value = "安全密码")
    private String securityPassword;

    @ApiModelProperty(value = "总完成率")
    private Double finishRate;

    @ApiModelProperty(value = "买总完成率")
    private Double finishBuyRate;

    @ApiModelProperty(value = "平均放行时间分钟")
    private Double averagePass;

    @ApiModelProperty(value = "注册时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
