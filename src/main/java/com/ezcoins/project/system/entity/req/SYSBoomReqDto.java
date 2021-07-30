package com.ezcoins.project.system.entity.req;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/30 13:42
 * @Version:1.0
 */
@Data
public class SYSBoomReqDto {

    @ApiModelProperty(value = "id   null：添加     notNull：修改")
    private Integer id;

    @ApiModelProperty(value = "轮播图名称")
    @NotBlank(message = "请输入轮播图名称")
    private String name;

    @ApiModelProperty(value = "轮播图地址")
    @NotBlank(message = "请先上传轮播图")
    private String url;

}
