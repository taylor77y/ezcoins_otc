package com.ezcoins.project.otc.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * otc配置
 * </p>
 *
 * @author wanglei
 * @since 2021-06-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="EzOtcConfig对象", description="otc配置")
public class EzOtcConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "每日最大取消数量")
    private Integer maxCancelNum;

    @ApiModelProperty(value = "付款最大期限（分钟）")
    private Integer maxPayTime;

    @ApiModelProperty(value = "付款最小期限（分钟）")
    private Integer minPayTime;

    @ApiModelProperty(value = "商户保证金")
    private Integer advertisingBusinessMargin;

    @ApiModelProperty(value = "接单时间限制（分钟)")
    private Integer orderTime;
}
