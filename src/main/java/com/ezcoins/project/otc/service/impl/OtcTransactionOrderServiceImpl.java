package com.ezcoins.project.otc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.exception.CheckException;
import com.ezcoins.project.otc.entity.OtcTransaction;
import com.ezcoins.project.otc.entity.req.OtcTransactionReqDto;
import com.ezcoins.project.otc.entity.resp.OtcTransactionRespDto;
import com.ezcoins.project.otc.mapper.OtcTransactionOrderMapper;
import com.ezcoins.project.otc.service.OtcTransactionOrderService;
import com.ezcoins.response.ResponseList;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OtcTransactionOrderServiceImpl extends ServiceImpl<OtcTransactionOrderMapper, OtcTransaction> implements OtcTransactionOrderService {

    /**
     * 次级菜单-- OTC订单
     *
     * @param
     * @return
     */
    @Override
    public ResponseList<OtcTransactionRespDto> otcTransactionOrderList(OtcTransactionReqDto otcTransactionReqDto) {
        IPage<OtcTransaction> page = new Page<OtcTransaction>(otcTransactionReqDto.getPage(), otcTransactionReqDto.getLimit());
        LambdaQueryWrapper<OtcTransaction> otcOrderLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 交易 ID
        String transactionId = otcTransactionReqDto.getTransactionId();
        CheckException.checkNotEmpty(transactionId, () -> {
            otcOrderLambdaQueryWrapper.eq(OtcTransaction::getTransactionId, transactionId);
        });

        //用户名
        String username = otcTransactionReqDto.getUsername();
        CheckException.checkNotEmpty(username, () -> {
            otcOrderLambdaQueryWrapper.eq(OtcTransaction::getUsername, username);
        });

        //用户类型
        String userType = otcTransactionReqDto.getUserType();
        CheckException.checkNotEmpty(userType, () -> {
            otcOrderLambdaQueryWrapper.eq(OtcTransaction::getUserType, userType);
        });

        // 设备来源
        String deviceSource = otcTransactionReqDto.getDeviceSource();
        CheckException.checkNotEmpty(deviceSource, () -> {
            otcOrderLambdaQueryWrapper.eq(OtcTransaction::getDeviceSource, deviceSource);
        });

        //状态
        String status = otcTransactionReqDto.getStatus();
        CheckException.checkNotEmpty(status, () -> {
            otcOrderLambdaQueryWrapper.eq(OtcTransaction::getStatus, status);
        });

        //发起时间
        String launchTime = otcTransactionReqDto.getLaunchTime();
        CheckException.checkNotEmpty(launchTime, () -> {
            otcOrderLambdaQueryWrapper.eq(OtcTransaction::getLaunchTime, launchTime);
        });

        //更新时间
        String updateTime = otcTransactionReqDto.getUpdateTime();
        CheckException.checkNotEmpty(updateTime, () -> {
            otcOrderLambdaQueryWrapper.eq(OtcTransaction::getUpdateTime, updateTime);
        });

        //按照价格降序排列
        otcOrderLambdaQueryWrapper.orderByDesc(OtcTransaction::getLaunchTime);
        IPage<OtcTransaction> iPage = baseMapper.selectPage(page, otcOrderLambdaQueryWrapper);
        //根据商铺id查询商铺
        List<OtcTransaction> records = iPage.getRecords();

        List<OtcTransactionRespDto> otcOrderRespDtos = new ArrayList<>();
        return ResponseList.success(otcOrderRespDtos);
    }

    /**
     * 次级菜单-- OTC订单
     *
     * @param
     * @return
     */
//    @Override
//    public Response<OtcOrder> findOrderDetailsByTxid(String txid){
//
//    }
}
