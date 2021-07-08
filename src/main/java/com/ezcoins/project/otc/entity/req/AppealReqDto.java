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
//
//    @ApiModelProperty(value = "匹配订单号")
//    private String reason;
//
//
//    @ApiModelProperty(value = "匹配订单号")
//    private String reason;
//

}
