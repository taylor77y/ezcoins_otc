package com.ezcoins.project.otc.controller;


import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.consumer.entity.EzUser;
import com.ezcoins.project.consumer.service.EzUserService;
import com.ezcoins.project.otc.entity.EzUserAlipay;
import com.ezcoins.project.otc.entity.EzUserBank;
import com.ezcoins.project.otc.entity.EzUserWechat;
import com.ezcoins.project.otc.service.EzUserAlipayService;
import com.ezcoins.project.otc.service.EzUserBankService;
import com.ezcoins.project.otc.service.EzUserWechatService;
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
 * 用户 支付宝信息 前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-06-15
 */
@RestController
@Api(tags = "Admin-收款方式模块")
@RequestMapping("/admin/otc/paymentMethod")
public class PaymentMethodController {

    @Autowired
    private EzUserAlipayService alipayService;

    @Autowired
    private EzUserBankService bankService;

    @Autowired
    private EzUserWechatService wechatService;


    @ApiOperation(value = "微信收款方式信息列表")
    @PostMapping("wechatList")
    @AuthToken
    public ResponsePageList<EzUserWechat> wechatList(@RequestBody SearchModel<EzUserWechat> searchModel) {
        return ResponsePageList.success(wechatService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }

    @ApiOperation(value = "支付宝收款方式信息列表")
    @PostMapping("alipayList")
    @AuthToken
    public ResponsePageList<EzUserAlipay> alipayList(@RequestBody SearchModel<EzUserAlipay> searchModel) {
        return ResponsePageList.success(alipayService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }

    @ApiOperation(value = "银行收款方式信息列表")
    @PostMapping("bankList")
    @AuthToken
    public ResponsePageList<EzUserBank> bankList(@RequestBody SearchModel<EzUserBank> searchModel) {
        return ResponsePageList.success(bankService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }







}

