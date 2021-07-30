package com.ezcoins.project.otc.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.otc.entity.EzAdvertisingBusiness;
import com.ezcoins.project.otc.entity.EzOtcChatMsg;
import com.ezcoins.project.otc.service.EzAdvertisingBusinessService;
import com.ezcoins.project.otc.service.EzOtcChatMsgService;
import com.ezcoins.response.ResponseList;
import com.ezcoins.response.ResponsePageList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * otc聊天信息表 前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-06-19
 */
@RestController
@Api(tags = "Admin-聊天记录模块")
@RequestMapping("/admin/otc/ezOtcChatMsg")
public class EzOtcChatMsgController {

    @Autowired
    private EzOtcChatMsgService otcChatMsgService;


    @ApiOperation(value = "根据 匹配订单id查询聊天记录")
    @PostMapping("chatMsg/{orderMatchNo}")
    @AuthToken
    public ResponseList<EzOtcChatMsg> advertisingBusinessList(@PathVariable String orderMatchNo) {
        LambdaQueryWrapper<EzOtcChatMsg> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(EzOtcChatMsg::getOrderMatchNo,orderMatchNo);
        return ResponseList.success(otcChatMsgService.list(queryWrapper));
    }
}

