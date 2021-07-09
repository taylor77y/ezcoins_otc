package com.ezcoins.project.coin.controller;


import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.project.coin.entity.UserWalletAddr;
import com.ezcoins.project.coin.entity.Wallet;
import com.ezcoins.project.coin.service.WalletService;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.response.ResponsePageList;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 钱包地址表 前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-06-25
 */
@RestController
@RequestMapping("/admin/coin/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @ApiOperation(value = "钱包地址表列表")
    @AuthToken
    @PostMapping("walletList")
    public ResponsePageList<Wallet> withdrawalAddressList(@RequestBody SearchModel<Wallet> searchModel) {
        return ResponsePageList.success(walletService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }


}

