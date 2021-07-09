package com.ezcoins.project.otc.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/7 10:45
 * @Version:1.0
 */
@Data
public class AppealReqDto {
    @ApiModelProperty(value = "申诉理由")
    private String reason;

    @ApiModelProperty(value = "匹配订单号")
    private String orderMatchNo;

    @ApiModelProperty(value = " 1:对方未付款 2：对方未放行 3:其他")
    private String type;

//
//    @ApiModelProperty(value = "匹配订单号")
//    private String reason;
//
//
//    @ApiModelProperty(value = "匹配订单号")
//    private String reason;
//

}
