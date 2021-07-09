package com.ezcoins.project.coin.controller;


import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.project.coin.entity.Record;
import com.ezcoins.project.coin.entity.UserWalletAddr;
import com.ezcoins.project.coin.service.RecordService;
import com.ezcoins.project.coin.service.UserWalletAddrService;
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
 *  前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-07-08
 */
@RestController
@RequestMapping("/admin/coin/userWalletAddr")
public class UserWalletAddrController {

    @Autowired
    private UserWalletAddrService walletAddrService;

    @ApiOperation(value = "用户提现地址")
    @AuthToken
    @PostMapping("withdrawalAddressList")
    public ResponsePageList<UserWalletAddr> withdrawalAddressList(@RequestBody SearchModel<UserWalletAddr> searchModel) {
        return ResponsePageList.success(walletAddrService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }


}

