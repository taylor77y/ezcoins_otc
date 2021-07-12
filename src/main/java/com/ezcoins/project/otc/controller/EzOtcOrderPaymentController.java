package com.ezcoins.project.otc.controller;


import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.otc.entity.EzOtcOrderMatch;
import com.ezcoins.project.otc.entity.EzOtcOrderPayment;
import com.ezcoins.project.otc.service.EzOtcOrderMatchService;
import com.ezcoins.project.otc.service.EzOtcOrderPaymentService;
import com.ezcoins.response.ResponsePageList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-07-02
 */
@RestController
@Api(tags = "Admin-订单支付信息")
@RequestMapping("/admin/otc/ezOtcOrderPayment")
public class EzOtcOrderPaymentController {

    @Autowired
    private EzOtcOrderPaymentService paymentService;

    @AuthToken
    @ApiOperation(value = "订单支付信息列表")
    @PostMapping("otcOrderList")
    public ResponsePageList<EzOtcOrderPayment> otcOrderList(@RequestBody SearchModel<EzOtcOrderPayment> searchModel){
        return ResponsePageList.success(paymentService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }





}

