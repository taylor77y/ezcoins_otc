package com.ezcoins.project.otc.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.otc.entity.EzAdvertisingBusiness;
import com.ezcoins.project.otc.entity.EzOtcChatMsg;
import com.ezcoins.project.otc.entity.EzOtcOrderAppeal;
import com.ezcoins.project.otc.entity.req.DoAppealReqDto;
import com.ezcoins.project.otc.entity.req.DoOrderReqDto;
import com.ezcoins.project.otc.service.EzOtcOrderAppealService;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponseList;
import com.ezcoins.response.ResponsePageList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 订单申诉 前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-07-08
 */
@RestController
@Api(tags = "Admin-订单申诉")
@RequestMapping("/admin/otc/ezOtcOrderAppeal")
public class EzOtcOrderAppealController {
    @Autowired
    private EzOtcOrderAppealService appealService;

    @ApiOperation(value = "根据匹配订单id查询投诉")
    @PostMapping("appealBy/{orderMatchNo}")
    @AuthToken
    public ResponseList<EzOtcOrderAppeal> appealBy(@PathVariable String orderMatchNo) {
        LambdaQueryWrapper<EzOtcOrderAppeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(EzOtcOrderAppeal::getOrderMatchNo,orderMatchNo);
        queryWrapper.eq(EzOtcOrderAppeal::getStatus,"1");
        return ResponseList.success(appealService.list(queryWrapper));
    }

    @ApiOperation(value = "查询投诉列表")
    @PostMapping("appealList")
    @AuthToken
    public ResponsePageList<EzOtcOrderAppeal> appealList(@RequestBody SearchModel<EzOtcOrderAppeal> searchModel) {
        return ResponsePageList.success(appealService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }


    @ApiOperation(value = "处理投诉")
    @PostMapping("doAppeal")
    @AuthToken
    @Log(title = "处理投诉", businessType = BusinessType.UPDATE, operatorType = OperatorType.MANAGE)
    public Response doAppeal(@RequestBody @Validated DoAppealReqDto doAppealReqDto) {
        return appealService.doAppeal(doAppealReqDto);
    }

    @ApiOperation(value = "处理投诉后修改订单状态")
    @PutMapping("doOrder")
    @AuthToken
    @Log(title = "处理投诉后修改订单状态", businessType = BusinessType.UPDATE, operatorType = OperatorType.MANAGE)
    public Response doOrder(@RequestBody @Validated DoOrderReqDto orderReqDto) {
        return appealService.doOrder(orderReqDto);
    }
}

