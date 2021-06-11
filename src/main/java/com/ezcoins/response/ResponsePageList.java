package com.ezcoins.response;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ezcoins.constant.interf.HttpStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Administrator
 */
@Data
public class ResponsePageList<T> {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "是否成功")
    private Boolean success;

    @ApiModelProperty(value = "返回码")
    private Integer code;

    @ApiModelProperty(value = "返回消息")
    private String message;

    private PageInfoRes<T> data=new PageInfoRes<>();

    private ResponsePageList(){}

    private ResponsePageList(Page<T> page) {
        this.code=HttpStatus.HTTP_RES_CODE_200;
        this.message=HttpStatus.HTTP_RES_CODE_200_VALUE;
        this.success=true;
        this.data.setList(page.getRecords());
        this.data.setTotal(page.getTotal());
    }

    private ResponsePageList( long total, List<T> dataList) {
        this.code=HttpStatus.HTTP_RES_CODE_200;
        this.message=HttpStatus.HTTP_RES_CODE_200_VALUE;
        this.success=true;
        this.data.setList(dataList);
        this.data.setTotal(total);
    }

    public static <T> ResponsePageList<T> success(Page<T> page) {
        return new ResponsePageList<>(page);
    }

    public static <T> ResponsePageList<T> success( long total, List<T> dataList) {
        return new ResponsePageList<>( total, dataList);
    }
}
