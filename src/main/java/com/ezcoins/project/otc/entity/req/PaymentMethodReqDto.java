package com.ezcoins.project.otc.entity.req;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/16 16:09
 * @Version:1.0
 */
@Data
public class PaymentMethodReqDto {

    @ApiModelProperty(value = "支付方式id  存在：修改  不存在：添加")
    private Integer id;

    @ApiModelProperty(value = "收付款方式中文名",required = true)
    @NotBlank(message = "{收付款方式中文名不能为空}")
    private String name;

    @ApiModelProperty(value = "收付款方式英文名",required = true)
    @NotBlank(message = "{收付款方式英文名不能为空}")
    private String nameEn;
}
