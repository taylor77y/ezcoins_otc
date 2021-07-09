package com.ezcoins.project.otc.entity.req;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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

    @ApiModelProperty(value = "平均放行时间 分钟")
    private Double averagePass;

    @ApiModelProperty(value = "卖家昵称")
    private String advertisingName;

}
