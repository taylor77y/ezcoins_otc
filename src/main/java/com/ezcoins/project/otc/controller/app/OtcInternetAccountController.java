package com.ezcoins.project.otc.controller.app;

import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.base.BaseController;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.project.otc.entity.req.UserInternetAccountReqDto;
import com.ezcoins.project.otc.entity.resp.InternetAccountRespDto;
import com.ezcoins.project.otc.service.EzInternetAccountService;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponseList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Api(tags = "APP-OTC模块 网络账号")
@RequestMapping("/otc/app/internetaccount")
public class OtcInternetAccountController  extends BaseController {


    @Autowired
    private EzInternetAccountService ezInternetAccountService;

    @ApiOperation(value = "用户 网络账号列表")
    @AuthToken
    @GetMapping("userInternetAccountList")
    public ResponseList<InternetAccountRespDto> userInternetAccountList(){
        return ResponseList.success(ezInternetAccountService.userInternetAccountList(getUserId()));
    }


    @ApiOperation(value = "添加/修改 用户网络账号信息")
    @AuthToken
    @PostMapping("addOrUpdateUserInternetAccount")
    @Log(title = "添加/修改 用户网络账号信息", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public Response addOrUpdateUserInternetAccount(@RequestBody UserInternetAccountReqDto internetAccountReqDto){
        return ezInternetAccountService.addOrUpdateUserInternetAccount(internetAccountReqDto);
    }
}
