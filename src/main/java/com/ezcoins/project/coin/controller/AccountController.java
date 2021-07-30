package com.ezcoins.project.coin.controller;


import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.aspectj.lang.annotation.NoRepeatSubmit;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.project.coin.entity.Account;
import com.ezcoins.project.coin.entity.WithdrawOrder;
import com.ezcoins.project.coin.entity.req.ReviseAccountReqDto;
import com.ezcoins.project.coin.service.AccountService;
import com.ezcoins.project.coin.service.WithdrawOrderService;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponsePageList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 资产余额表 前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-06-17
 */
@RestController
@Api(tags = "Admin-资产账户模块")
@RequestMapping("/admin/coin/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @ApiOperation(value = "资产列表")
    @PostMapping("/accountList")
    @AuthToken
    public ResponsePageList<Account> accountList(@RequestBody SearchModel<Account> searchModel){
        return ResponsePageList.success(accountService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }

    @ApiOperation(value = "修改资产")
    @PutMapping("revise")
    @NoRepeatSubmit
    @AuthToken
    @Log(title = "资产币种模块", logInfo ="修改资产", operatorType = OperatorType.MANAGE)
    public Response revise(@RequestBody ReviseAccountReqDto reviseAccountReqDto) {
        return accountService.revise(reviseAccountReqDto);
    }


}

