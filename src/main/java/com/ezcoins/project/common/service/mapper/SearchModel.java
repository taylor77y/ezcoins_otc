package com.ezcoins.project.common.service.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ezcoins.utils.StringUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class SearchModel<T> {

    @ApiModelProperty(value = "页码", required = true)
    @NotBlank(message = "{页码不能为空}")
    private Integer page;

    @ApiModelProperty(value = "每页条数", required = true)
    @NotBlank(message = "{每页条数不能为空}")
    private Integer limit;

    @ApiModelProperty(value = "条件字段")
    private List<Field> fields;

    @ApiModelProperty(value = "排序字段")
    private String orderField;

    @ApiModelProperty(value = "排序方式 true:升序 false：降序")
    private boolean isAsc;

    public Page<T> getPage() {
        Page<T> pageParam = new Page<>(page, limit);
        if (StringUtils.isNotEmpty(orderField)) {
            OrderItem orderItem = new OrderItem();
            orderItem.setAsc(isAsc);
            orderItem.setColumn(orderField);
            pageParam.orders().add(orderItem);
        }
        return pageParam;

    }

    public QueryWrapper<T> getQueryModel() {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if (null != this.fields ) {
            this.fields.forEach(e -> {
                if (StringUtils.isNotEmpty(e.getValue())){
                    switch (e.getQueryMethod()) {
                        case eq:
                            queryWrapper.eq(true, e.getName(), e.getValue());
                            break;
                        case like:
                            queryWrapper.like(true, e.getName(), e.getValue());
                    }
                }
            });
        }
        if (StringUtils.isNotEmpty(orderField)) {
            queryWrapper.orderBy(true, isAsc, orderField);
        }
        return queryWrapper;
    }
}
