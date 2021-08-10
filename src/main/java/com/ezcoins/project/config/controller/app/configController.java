package com.ezcoins.project.config.controller.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.project.config.entity.EzCountryConfig;
import com.ezcoins.project.config.service.EzCountryConfigService;
import com.ezcoins.project.otc.entity.req.OtcOrderQueryReqDto;
import com.ezcoins.project.otc.entity.resp.OtcOrderRespDto;
import com.ezcoins.response.ResponseList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/23 16:20
 * @Version:1.0
 */
@RestController
@Api(tags = "APP-国家货币代码")
@RequestMapping("/config/app")
public class configController {

    @Autowired
    private EzCountryConfigService countryConfigService;

    @ApiOperation(value = "国家货币列表")
    @GetMapping("currencyCodeList")
    public ResponseList<String> currencyCodeList(){
        LambdaQueryWrapper<EzCountryConfig> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(EzCountryConfig::getCurrencyCode);
        lambdaQueryWrapper.isNotNull(EzCountryConfig::getCurrencyCode);
        lambdaQueryWrapper.orderByAsc(EzCountryConfig::getSort);
        return ResponseList.success(countryConfigService.listObjs(lambdaQueryWrapper).stream().map(o ->  (String) o).collect(Collectors.toList()));
    }

}
