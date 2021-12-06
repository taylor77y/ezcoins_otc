package com.ezcoins.project.otc.controller.app;

import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.base.BaseController;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.project.otc.entity.req.UserBankCardAddrReqDto;
import com.ezcoins.project.otc.entity.resp.PaymentBankRespDto;
import com.ezcoins.project.otc.service.EzPaymentBankService;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponseList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Api(tags = "APP-OTC 用户银行卡模块")
@RequestMapping("/otc/app/bankcard")
public class OtcBankCardController extends BaseController {

    @Autowired
    private EzPaymentBankService ezPaymentBankService;

    @ApiOperation(value = "用户银行卡列表")
    @AuthToken
    @GetMapping("userBankCardList")
    public ResponseList<PaymentBankRespDto> userBankCardList(){
        return ResponseList.success(ezPaymentBankService.userBankCardList(getUserId()));
    }


    @ApiOperation(value = "添加/修改 用户银行卡信息")
    @AuthToken
    @PostMapping("addOrUpdateUserBankCard")
    @Log(title = "添加/修改 用户银行卡信息", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public Response addOrUpdateUserBankCard(@RequestBody UserBankCardAddrReqDto bankCardReqDto){
        return ezPaymentBankService.addOrUpdateUserBankCard(bankCardReqDto);
    }
}
