package com.ezcoins.project.coin.controller.app;

import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.base.BaseController;
import com.ezcoins.project.coin.entity.resp.AccountRespDto;
import com.ezcoins.project.coin.service.AccountService;
import com.ezcoins.response.ResponseList;
import com.ezcoins.response.ResponsePageList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/17 14:47
 * @Version:1.0
 */
@RestController
@Api(tags = "APP- 资产 币模块")
@RequestMapping("/coin/app")
public class CoinController extends BaseController {

    @Autowired
    private AccountService accountService;

    @ApiOperation(value = "资产列表")
    @AuthToken
    @GetMapping("coinAccountList")
    public ResponseList<AccountRespDto> coinAccountList() {
        return ResponseList.success(accountService.coinAccountListByUserId(getUserId()));
    }

//    @ApiOperation(value = "充值 地址二维码")
//    @AuthToken
//    @GetMapping("rechargeAddress")
//    public TableDataInfo coinFromAddressList(@PathVariable Long id) {
//        startPage();
//        List<CoinWallet> list = coinWalletService.selectCoinWalletByCoinIdListToUdun(id,getUserId());
//        return getDataTable(list);
//    }


}
