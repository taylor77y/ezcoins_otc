package com.ezcoins.project.otc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.Version;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 广告订单号自增表
 * </p>
 *
 * @author wanglei
 * @since 2021-06-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="EzOtcOrderIndex对象", description="广告订单号自增表")
public class EzOtcOrderIndex implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "标志自增长的信息名字")
    @TableId
    private String name;

    @ApiModelProperty(value = "乐观锁")
    @Version
    private Integer version;

    @ApiModelProperty(value = "信息增长值")
    private Integer step;

    @ApiModelProperty(value = "所属订单模块")
    private Integer other;

    @ApiModelProperty(value = "我们要获取的信息值")
    private Integer currentValue;

}
