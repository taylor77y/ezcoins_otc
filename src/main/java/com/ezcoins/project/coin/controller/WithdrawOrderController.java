package com.ezcoins.project.coin.controller;


import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.aspectj.lang.annotation.NoRepeatSubmit;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.project.coin.entity.WithdrawConfig;
import com.ezcoins.project.coin.entity.WithdrawOrder;
import com.ezcoins.project.coin.entity.req.CheckWithdrewOrderReqDto;
import com.ezcoins.project.coin.entity.req.WithdrewConfigReqDto;
import com.ezcoins.project.coin.service.WithdrawOrderService;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponsePageList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-06-17
 */
@RestController
@Api(tags = "Admin-提币申诉模块")
@RequestMapping("/admin/coin/withdrawOrder")
public class WithdrawOrderController {

    @Autowired
    private WithdrawOrderService withdrawOrderService;

    @ApiOperation(value = "提币订单列表")
    @PostMapping("/withdrawOrderList")
    @AuthToken
    public ResponsePageList<WithdrawOrder> withdrawOrderList(@RequestBody SearchModel<WithdrawOrder> searchModel){
        return ResponsePageList.success(withdrawOrderService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }

    @NoRepeatSubmit
    @ApiOperation(value = "审核提币订单")
    @PutMapping("reviewWithdrawOrder")
    @AuthToken
    @Log(title = "审核提币订单", businessType = BusinessType.INSERT, operatorType = OperatorType.MANAGE)
    public Response reviewWithdrawOrder(@RequestBody CheckWithdrewOrderReqDto checkWithdrewOrderReqDto) {
        withdrawOrderService.reviewWithdrawOrder(checkWithdrewOrderReqDto);
        return Response.success();
    }
}

