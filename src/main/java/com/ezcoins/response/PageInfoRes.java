package com.ezcoins.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/9 16:13
 * @Version:1.0
 */
@Data
public class PageInfoRes<T> {
    @ApiModelProperty(value = "查询列表")
    private List<T> list;
    @ApiModelProperty(value = "总数据")
    private Long total;
}
