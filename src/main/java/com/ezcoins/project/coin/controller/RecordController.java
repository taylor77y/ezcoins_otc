package com.ezcoins.project.coin.controller;


import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.coin.entity.Record;
import com.ezcoins.project.coin.service.RecordService;
import com.ezcoins.project.common.service.mapper.Field;
import com.ezcoins.project.common.service.mapper.QueryMethod;
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
 * 资产记录表 前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-06-28
 */
@RestController
@RequestMapping("/admin/coin/record")
public class RecordController {
    @Autowired
    private RecordService recordService;

    @ApiOperation(value = "资产流水")
    @AuthToken
    @PostMapping("assetTurnover")
    public ResponsePageList<Record> assetTurnover(@RequestBody SearchModel<Record> searchModel) {
        return ResponsePageList.success(recordService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }

}

