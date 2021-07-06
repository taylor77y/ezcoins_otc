package com.ezcoins.project.coin.entity.req;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/5 19:07
 * @Version:1.0
 */
@Data
public class ReviseAccountReqDto {
    @NotBlank(message = "用户名不能为空")
    private String userId;

    private String  type;//增加：1  减少：0

    @NotBlank(message = "请输入原因")
    private String memo;

    private BigDecimal amount;

    private String coinName;

}
