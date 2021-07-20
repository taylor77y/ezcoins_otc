package com.ezcoins.project.consumer.entity.resp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/30 14:40
 * @Version:1.0
 */
@Data
public class VerifiedInfoRespDto {
    @ApiModelProperty(value = "kyc编号")
    private String id;

    @ApiModelProperty(value = "实名认证状态 状态：0:已认证 1:待审核 2：审核失败 3：未认证 ")
    private String kycStatus;

    @ApiModelProperty(value = "高级认证状态 状态：0:已认证 1:待审核 2：审核失败 3：未认证 ")
    private String advertisingStatus;

    @ApiModelProperty(value = "名字")
    private String firstName;

    @ApiModelProperty(value = "姓氏")
    private String lastName;

    @ApiModelProperty(value = "身份证/护照号码")
    private String identityCard;

    @ApiModelProperty(value = "国家地区")
    private String country;

    @ApiModelProperty(value = "UID")
    private String userId;

    @ApiModelProperty(value = "保证金")
    private BigDecimal margin;

}
