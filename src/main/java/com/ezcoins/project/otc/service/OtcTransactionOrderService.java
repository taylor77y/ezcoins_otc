package com.ezcoins.project.otc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.otc.entity.OtcTransaction;
import com.ezcoins.project.otc.entity.req.OtcTransactionReqDto;
import com.ezcoins.project.otc.entity.resp.OtcTransactionRespDto;
import com.ezcoins.response.ResponseList;

public interface OtcTransactionOrderService extends IService<OtcTransaction> {

    /**
     * 次级菜单-- OTC订单
     * @param
     * @return
     */
    ResponseList<OtcTransactionRespDto> otcTransactionOrderList(OtcTransactionReqDto otcTransactionReqDto);

    /**
     * 次级菜单-- 根据交易 id 查询 订单详情
     * @param
     * @return
     */
//    Response<OtcOrder> findOrderDetailsByTxid(String txid);
}
