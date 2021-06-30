package com.ezcoins.project.otc.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.base.BaseException;
import com.ezcoins.constant.enums.coin.CoinConstants;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.coin.entity.vo.BalanceChange;
import com.ezcoins.project.coin.service.AccountService;
import com.ezcoins.project.otc.entity.EzOneSellConfig;
import com.ezcoins.project.otc.entity.EzOneSellOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.otc.entity.req.SellOneKeyReqDto;
import com.ezcoins.response.BaseResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 一键卖币订单 服务类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-28
 */
public interface EzOneSellOrderService extends IService<EzOneSellOrder> {


}
