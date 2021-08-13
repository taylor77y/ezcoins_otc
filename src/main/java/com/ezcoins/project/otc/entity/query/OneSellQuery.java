package com.ezcoins.project.otc.entity.query;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/8/11 10:48
 * @Version:1.0
 */
@Data
public class OneSellQuery {
    @ApiModelProperty(value = "页码")
    private Long page;

    @ApiModelProperty(value = "页容量")
    private Long limit;

    @ApiModelProperty(value = "匹配订单号")
    private String orderMatchNo;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "用户OTC昵称")
    private String matchAdvertisingName;

    @ApiModelProperty(value = "订单状态(1:接单已取消 2:待接单 3：已取消 4：等待支付 5：已支付 6：已完成)")
    private List<String> statuss;

    @ApiModelProperty(value = "币种类型")
    private String coinName;

}
