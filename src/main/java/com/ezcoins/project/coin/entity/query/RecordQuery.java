package com.ezcoins.project.coin.entity.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/19 16:13
 * @Version:1.0
 */
@Data
public class RecordQuery {

    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endTime;

    @ApiModelProperty(value = "币种名")
    private String coinName;

    @ApiModelProperty(value = "子类型")
    private String sonType;

    @ApiModelProperty(value = "页码")
    private Long page;

    @ApiModelProperty(value = "页容量")
    private Long limit;
}
