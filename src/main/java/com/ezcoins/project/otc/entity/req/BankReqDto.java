package com.ezcoins.project.otc.entity.req;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/15 16:27
 * @Version:1.0
 */
@Data
public class BankReqDto {
    @ApiModelProperty(value = "银行信息id编号")
    private String id;

    @ApiModelProperty(value = "卖家姓名")
    private String realName;


    @ApiModelProperty(value = "银行名")
    private String bankName;

    @ApiModelProperty(value = "账户名")
    private String accountName;

    @ApiModelProperty(value = "账户号")
    private String number;

    @ApiModelProperty(value = "状态(0:已激活 1：未激活)")
    private String status;

}
