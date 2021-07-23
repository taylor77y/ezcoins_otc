package com.ezcoins.project.coin.controller;


import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.project.coin.entity.SysAirport;
import com.ezcoins.project.coin.entity.Type;
import com.ezcoins.project.coin.service.SysAirportService;
import com.ezcoins.project.coin.service.TypeService;
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
 *  前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-07-22
 */
@RestController
@Api(tags = "Admin-空投记录模块")
@RequestMapping("/admin/coin/sys-airport")
public class SysAirportController {

    @Autowired
    private SysAirportService airportService;

    @ApiOperation(value = "后台空投记录列表")
    @PostMapping("/airportList")
    @AuthToken
    public ResponsePageList<SysAirport> airportList(@RequestBody SearchModel<SysAirport> searchModel){
        return ResponsePageList.success(airportService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }
}

