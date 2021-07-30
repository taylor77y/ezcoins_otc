package com.ezcoins.project.otc.entity.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/29 11:48
 * @Version:1.0
 */
@Data
public class Info {
    @ApiModelProperty(value = "未读消息数量")
    private Integer sysTipsNum;

    @ApiModelProperty(value = "未处理订单数量")
    private Integer noHandleNum;

    @ApiModelProperty(value = "轮播图集合")
    List<String> boomAddrList;

}
