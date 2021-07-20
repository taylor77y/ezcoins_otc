package com.ezcoins.project.otc.controller;


import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.otc.entity.EzOtcOrder;
import com.ezcoins.project.otc.entity.EzOtcOrderMatch;
import com.ezcoins.project.otc.service.EzOtcOrderMatchService;
import com.ezcoins.response.ResponsePageList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 匹配日OTC订单 前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-06-18
 */
@RestController
@Api(tags = "Admin-OTC匹配订单模块")
@RequestMapping("/admin/otc/ezOtcOrderMatch")
public class EzOtcOrderMatchController {

    @Autowired
    private EzOtcOrderMatchService orderMatchService;

    @AuthToken
    @ApiOperation(value = "OTC 匹配订单列表")
    @PostMapping("otcOrderList")
    public ResponsePageList<EzOtcOrderMatch> otcOrderList(@RequestBody SearchModel<EzOtcOrderMatch> searchModel){
        return ResponsePageList.success(orderMatchService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }

//    @AuthToken
//    @ApiOperation(value = "后台 一键卖币确认付款")
//    @PostMapping("confirmPayment/{matchOrderNo}")
//    @Log(title = "一键卖币确认付款", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
//    public ResponsePageList<EzOtcOrderMatch> confirmPayment(@PathVariable String matchOrderNo){
//
//    }


}

