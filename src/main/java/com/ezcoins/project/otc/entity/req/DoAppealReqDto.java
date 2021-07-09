package com.ezcoins.project.otc.entity.req;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/9 14:10
 * @Version:1.0
 */
@Data
public class DoAppealReqDto {
    @ApiModelProperty(value = "申诉id")
    @NotBlank(message = "申诉编号不能为空")
    private String id;

    @ApiModelProperty(value = "申诉操作： 3：申诉失败 4：申诉成功)")
    @NotBlank(message = "申诉状态不能为空")
    private String status;

    @ApiModelProperty(value = "处理结果")
    @NotBlank(message = "处理结果不能为空")
    private String memo;

}
