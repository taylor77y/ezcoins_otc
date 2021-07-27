package com.ezcoins.project.coin.controller.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Limit;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.base.BaseController;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.LimitType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.coin.entity.*;
import com.ezcoins.project.coin.entity.query.RecordQuery;
import com.ezcoins.project.coin.entity.req.UserAddrReqDto;
import com.ezcoins.project.coin.entity.req.WithdrawReqDto;
import com.ezcoins.project.coin.entity.resp.AccountRespDto;
import com.ezcoins.project.coin.service.*;
import com.ezcoins.project.common.service.mapper.Field;
import com.ezcoins.project.common.service.mapper.QueryMethod;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.response.Response;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponseList;
import com.ezcoins.response.ResponsePageList;
import com.ezcoins.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
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

    @Autowired
    private TypeService typeService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private WithdrawConfigService withdrawConfigService;

    @Autowired
    private WithdrawOrderService withdrawOrderService;

    @Autowired
    private RecordService recordService;

    @Autowired
    private UserWalletAddrService walletAddrService;

    @Autowired
    private RechargeConfigService rechargeConfigService;

    @ApiOperation(value = "添加/修改 提币地址")
    @AuthToken
    @PostMapping("addOrUpdateWithdrawalAddr")
    @Log(title = "添加/修改 提币地址", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public Response addOrUpdateWithdrawalAddr(@RequestBody UserAddrReqDto addrReqDto){
        return walletAddrService.addOrUpdateWithdrawalAddr(addrReqDto);
    }

    @ApiOperation(value = "删除提币地址")
    @AuthToken
    @DeleteMapping("deleteWithdrawalAddr/{id}")
    @Log(title = "添加/修改 提币地址", businessType = BusinessType.DELETE, operatorType = OperatorType.MOBILE)
    public Response deleteWithdrawalAddr(@PathVariable String id){
        walletAddrService.removeById(id);
        return Response.success();
    }
    @ApiOperation(value = "根据币种名查询余额")
    @AuthToken
    @GetMapping("coinAccount/{coinName}")
    public Response coinAccount(@PathVariable String coinName) {
        HashMap map=new HashMap();
        map.put("balance",accountService.getAccountByUserIdAndCoinId(ContextHandler.getUserId(),coinName,ContextHandler.getUserName())
                .getAvailable());
        return Response.success(map);
    }

    @ApiOperation(value = "提币地址列表")
    @AuthToken
    @GetMapping("withdrawalAddrList/{withdrawalConfigId}")
    public ResponseList<UserWalletAddr> withdrawalAddrList(@PathVariable String withdrawalConfigId){
        return walletAddrService.withdrawalAddrList(withdrawalConfigId);
    }

    @ApiOperation(value = "资产列表")
    @AuthToken
    @GetMapping("coinAccountList")
    public ResponseList<AccountRespDto> coinAccountList() {
        return ResponseList.success(accountService.coinAccountListByUserId(getUserId()));
    }
    @ApiOperation(value = "币种列表")
    @AuthToken
    @GetMapping("coinTypeList")
    public ResponseList<Type> coinTypeList() {
        return ResponseList.success(typeService.list());
    }


    @ApiOperation(value = "冲币种列表")
    @AuthToken
    @GetMapping("rechargeCoinList")
    public ResponseList<RechargeConfig> rechargeCoinList(){
        return ResponseList.success(rechargeConfigService.list());
    }

    @ApiOperation(value = "提币币种列表")
    @AuthToken
    @GetMapping("withdrawCoinList")
    public ResponseList<WithdrawConfig> withdrawCoinList(){
        return ResponseList.success(withdrawConfigService.list());
    }

    @ApiOperation(value = "提币配置")
    @AuthToken
    @GetMapping("withdrawConfigs/{coinName}")
    public Response<WithdrawConfig> withdrawConfigs(@PathVariable String coinName) {
        LambdaQueryWrapper<WithdrawConfig> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(WithdrawConfig::getCoinName,coinName);
        return Response.success(withdrawConfigService.getOne(lambdaQueryWrapper));
    }
    @ApiOperation(value = "充值地址二维码")
    @AuthToken
    @GetMapping("rechargeAddress/{id}")
    public Response rechargeAddress(@PathVariable String id) {
        return walletService.rechargeAddress(getUserId(),id);
    }




    @ApiOperation(value = "发起提现")
    @AuthToken
    @PostMapping("withdraw")
    @Limit(LIMIT_TYPE = LimitType.WITHDRAWLIMIT)
    @Log(title = "发起提现", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public Response withdraw(@RequestBody WithdrawReqDto withdrawReqDto) {
        return withdrawOrderService.withdraw(withdrawReqDto);
    }

    @ApiOperation(value = "提现/充值 记录")
    @AuthToken
    @PostMapping("withdrawOrRechargeRecord")
    public ResponsePageList<Record> withdrawOrRechargeRecord(@RequestBody RecordQuery record) {
        Page<Record> page=new Page<>(record.getPage(),record.getLimit());
        LambdaQueryWrapper<Record> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        Date statTime = record.getStatTime();
        Date endTime = record.getEndTime();
        String coinName = record.getCoinName();
        String type = record.getType();
        if (StringUtils.isNotNull(statTime)){
            lambdaQueryWrapper.ge(Record::getCreateTime,statTime);
        }
        if (StringUtils.isNotNull(endTime)){
            lambdaQueryWrapper.le(Record::getCreateTime,endTime);
        }
        if (StringUtils.isNotEmpty(coinName)){
            lambdaQueryWrapper.eq(Record::getCoinName,coinName);
        }
        lambdaQueryWrapper.eq(Record::getSonType,type);
        lambdaQueryWrapper.eq(Record::getUserId,ContextHandler.getUserId());
        lambdaQueryWrapper.orderByDesc(Record::getCreateTime);
        Page<Record> p = recordService.page(page, lambdaQueryWrapper);
        return ResponsePageList.success(p);
    }



    @ApiOperation(value = "账户记录")
    @AuthToken
    @PostMapping("accountRecord")
    public ResponsePageList<Record> accountRecord(@RequestBody SearchModel<Record> searchModel) {
        Field field=new Field();
        field.setName("user_id");
        field.setValue(ContextHandler.getUserId());
        field.setQueryMethod(QueryMethod.eq);
        searchModel.getFields().add(field);
        return ResponsePageList.success(recordService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }
}
