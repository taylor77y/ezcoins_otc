package com.ezcoins.project.coin.entity.resp;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/17 15:50
 * @Version:1.0
 */
@Data
public class AccountRespDto {
    @ApiModelProperty(value = "ID")
    private String id;

    @ApiModelProperty(value = "币种ID")
    private Integer coinId;

    @ApiModelProperty(value = "币种名")
    private String coinName;

    @ApiModelProperty(value = "余额")
    private BigDecimal available;

    @ApiModelProperty(value = "冻结")
    private BigDecimal frozen;

    @ApiModelProperty(value = "锁仓")
    private BigDecimal lockup;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
