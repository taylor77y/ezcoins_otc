package com.ezcoins.project.otc.entity.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/1 14:43
 * @Version:1.0
 */
@Data
public class AdvertisingBusinessInfoRespDto {
    @ApiModelProperty(value = "商户OTC昵称")
    private String advertisingName;

    @ApiModelProperty(value = "卖单数量")
    private Integer sellCount;

    @ApiModelProperty(value = "买单数量")
    private Integer buyCount;

    @ApiModelProperty(value = "平均放行时间 分钟")
    private Double averagePass;

    @ApiModelProperty(value = "总完成率")
    private Double finishRate;

    @ApiModelProperty(value = "30 日成单")
    private Integer mouthCount;

    @ApiModelProperty(value = "注册时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date registrationTime;

    @ApiModelProperty(value = "保证金")
    private BigDecimal margin;

    @ApiModelProperty(value = "实名认证状态 状态：0:已认证 1：未认证 ")
    private String kycStatus;

    @ApiModelProperty(value = "高级认证状态 状态：0:已认证 1：未认证 ")
    private String advertisingStatus;

}
