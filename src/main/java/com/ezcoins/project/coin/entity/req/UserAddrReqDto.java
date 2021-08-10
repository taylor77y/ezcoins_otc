package com.ezcoins.project.coin.entity.req;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/8 16:28
 * @Version:1.0
 */
@Data
public class UserAddrReqDto {
    @ApiModelProperty(value = "id null：增加  不为null：修改")
    private String id;

    @ApiModelProperty(value = "提现配置id")
    private String withdrawalConfigId;

    @ApiModelProperty(value = "提币地址")
    @NotBlank(message = "{addr.not}")
    private String addr;

    @ApiModelProperty(value = "备注")
    private String remark;
}
