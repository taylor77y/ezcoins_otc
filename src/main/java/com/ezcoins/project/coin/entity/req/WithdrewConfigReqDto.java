package com.ezcoins.project.coin.entity.req;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/17 11:37
 * @Version:1.0
 */
@Data
public class WithdrewConfigReqDto {
    @ApiModelProperty(value = "提币币种配置ID  id为null就是添加")
    private Integer id;

    @ApiModelProperty(value = "币种名")
    @NotBlank(message = "币种名不能为空")
    private String coinName;

    @ApiModelProperty(value = "币种全称")
    @NotBlank(message = "币种全称不能为空")
    private String coinFullName;

    @ApiModelProperty(value = "主币种类型")
    @NotBlank(message = "主币种类型不能为空")
    private String mainCoinType;

    @ApiModelProperty(value = "子币种类型")
    @NotBlank(message = "子币种类型不能为空")
    private String coinType;

    @ApiModelProperty(value = "提现手续费[固定]")
    @NotNull(message = "固定提现手续费不能为空")
    private BigDecimal fee;

    @ApiModelProperty(value = "提现手续费率[比例]")
    @NotNull(message = "提现手续费率比例不能为空")
    private BigDecimal feeRate;

    @ApiModelProperty(value = "最大提币数量")
    @NotNull(message = "最大提币数量不能为空")
    private BigDecimal maxWithdraw;

    @ApiModelProperty(value = "最小提币数量")
    @NotNull(message = "最小提币数量不能为空")
    private BigDecimal minWithdraw;

    @ApiModelProperty(value = "单日限额0就不限制")
    @NotNull(message = "单日限额不能为空")
    private BigDecimal singleDayLimit;

}
