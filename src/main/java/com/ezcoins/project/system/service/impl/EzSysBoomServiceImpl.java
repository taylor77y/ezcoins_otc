package com.ezcoins.project.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.constant.enums.otc.MatchOrderStatus;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.otc.entity.EzOtcOrderMatch;
import com.ezcoins.project.otc.entity.req.PageQuery;
import com.ezcoins.project.otc.entity.resp.Info;
import com.ezcoins.project.otc.service.EzOtcOrderMatchService;
import com.ezcoins.project.system.entity.EzSysBoom;
import com.ezcoins.project.system.entity.EzSysTips;
import com.ezcoins.project.system.mapper.EzSysBoomMapper;
import com.ezcoins.project.system.service.EzSysBoomService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.project.system.service.EzSysTipsService;
import com.ezcoins.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * <p>
 * 首页轮播图 服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-07-29
 */
@Service
public class EzSysBoomServiceImpl extends ServiceImpl<EzSysBoomMapper, EzSysBoom> implements EzSysBoomService {


    @Autowired
    private EzSysTipsService tipsService;

    @Autowired
    private EzOtcOrderMatchService orderMatchService;

    /**
     * @Description: 新订单
     * @Param: []
     * @return: com.ezcoins.response.ResponseList<com.ezcoins.project.otc.entity.resp.OtcOrderRespDto>
     * @Author: Wanglei
     * @Date: 2021/6/22
     */
    @Override
    public Response<Info> info(PageQuery pageQuery) {
        Info info = new Info();
        String userId = ContextHandler.getUserId();
        if ("0".equals(pageQuery.getIsHandle())) {
            LambdaQueryWrapper<EzOtcOrderMatch> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(EzOtcOrderMatch::getOtcOrderUserId, userId).and(we -> we.eq(
                    EzOtcOrderMatch::getStatus, MatchOrderStatus.WAITFORPAYMENT).or()
                    .eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.PAID).or()
                    .eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.PENDINGORDER));
            //查询未处理订单
            int count = orderMatchService.count(queryWrapper);
            info.setNoHandleNum(count == 0 ? null : count);
            info.setNoHandleNum(5);
        }
        LambdaQueryWrapper<EzSysTips> tipsqueryWrapper = new LambdaQueryWrapper<>();
        //未读消息数量
        tipsqueryWrapper.eq(EzSysTips::getUserId, userId);
        tipsqueryWrapper.eq(EzSysTips::getIsRead, "1");
        info.setSysTipsNum(tipsService.count(tipsqueryWrapper));
        info.setBoomAddrList(this.list().stream().map(EzSysBoom::getUrl).collect(Collectors.toList()));
        return Response.success(info);
    }

}
