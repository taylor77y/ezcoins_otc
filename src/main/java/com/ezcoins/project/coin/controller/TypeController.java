package com.ezcoins.project.coin.controller;


import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.project.coin.entity.Type;
import com.ezcoins.project.coin.entity.WithdrawOrder;
import com.ezcoins.project.coin.service.TypeService;
import com.ezcoins.project.coin.service.WithdrawOrderService;
import com.ezcoins.project.common.service.mapper.SearchModel;
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
 * 币种类型表 前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-06-17
 */
@RestController
@Api(tags = "Admin-资产账户模块")
@RequestMapping("/coin/type")
public class TypeController {

    @Autowired
    private TypeService typeService;


    @ApiOperation(value = "币种列表")
    @PostMapping("/coinTypeList")
    @AuthToken
    public ResponsePageList<Type> coinTypeList(@RequestBody SearchModel<Type> searchModel){
        return ResponsePageList.success(typeService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }










}

