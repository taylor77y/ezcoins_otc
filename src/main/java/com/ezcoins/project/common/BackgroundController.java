package com.ezcoins.project.common;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.constant.RecordSonType;
import com.ezcoins.project.coin.entity.Record;
import com.ezcoins.project.coin.service.RecordService;
import com.ezcoins.project.common.entity.resp.IndexRespDto;
import com.ezcoins.project.consumer.entity.EzUser;
import com.ezcoins.project.consumer.service.EzUserService;
import com.ezcoins.project.otc.entity.EzOtcOrderAppeal;
import com.ezcoins.project.otc.entity.EzOtcOrderMatch;
import com.ezcoins.project.otc.service.EzOtcOrderAppealService;
import com.ezcoins.project.otc.service.EzOtcOrderMatchService;
import com.ezcoins.response.Response;
import com.ezcoins.utils.BigDecimalUtils;
import com.ezcoins.utils.CollectorsUtil;
import com.ezcoins.utils.DateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/9 10:35
 * @Version:1.0
 */
@RestController
@Api(tags = "Admin-后台数据")
@RequestMapping("/admin/background")
public class BackgroundController {
    @Autowired
    private EzUserService ezUserService;

    @Autowired
    private EzOtcOrderMatchService matchService;

    @Autowired
    private EzOtcOrderAppealService appealService;

    @Autowired
    private RecordService recordService;

    @ApiOperation(value = "用户列表")
    @PostMapping("index")
    @AuthToken
    public Response<IndexRespDto> index(){
        IndexRespDto indexRespDto = new IndexRespDto();
        //得到昨天12点的时间
        Date yesterdayStart = DateUtils.getYesterdayStart();
        List<EzUser> userList = ezUserService.list();
        indexRespDto.setSignUpKcy(userList.stream().filter(e -> "0".equals(e.getKycStatus())).count());
        indexRespDto.setSignUpAdvertising(userList.stream().filter(e -> "0".equals(e.getLevel())).count());
        indexRespDto.setSignUp((long) userList.size());
        indexRespDto.setSignUpDay(userList.stream().filter(e->e.getCreateTime().getTime()>yesterdayStart.getTime()).count());
        List<EzOtcOrderMatch> orderList = matchService.list();
        List<EzOtcOrderMatch> collect = orderList.stream().filter(e -> "6".equals(e.getStatus())).collect(Collectors.toList());
        indexRespDto.setTotalOrderSuccess((long) collect.size());//总成交单数
        indexRespDto.setTotalTransactionAmount(collect.stream().collect(CollectorsUtil.summingBigDecimal(EzOtcOrderMatch::getAmount)));//总成交数量
        List<EzOtcOrderMatch> matches = collect.stream().filter(e -> e.getCreateTime().getTime() > yesterdayStart.getTime()).collect(Collectors.toList());
        indexRespDto.setDayOrderSuccess((long) matches.size());//当天成交单数
        indexRespDto.setDayTransactionAmount(matches.stream().collect(CollectorsUtil.summingBigDecimal(EzOtcOrderMatch::getAmount)));//当天交易额
        indexRespDto.setTotalOrderComplaint(orderList.stream().filter(e-> "0".equals(e.getIsAppeal())).count());//总投诉单数
        LambdaQueryWrapper<EzOtcOrderAppeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(EzOtcOrderAppeal::getStatus,"1");
        indexRespDto.setUntreatedComplaint((long) appealService.list(queryWrapper).size());
        LambdaQueryWrapper<Record> queryWrapper1=new LambdaQueryWrapper<>();
        queryWrapper1.eq(Record::getSonType, RecordSonType.ORDINARY_WITHDRAWAL).or().eq(Record::getSonType, RecordSonType.ORDINARY_RECHARGE);
        queryWrapper1.eq(Record::getStatus,"1");
        List<Record> list = recordService.list(queryWrapper1);
        //按照类型进行分类
        Map<String, List<Record>> stringListMap = list.stream().collect(Collectors.groupingBy(Record::getSonType));
        indexRespDto.setTotalRecharge(stringListMap.get(RecordSonType.ORDINARY_RECHARGE).stream().collect(CollectorsUtil.summingBigDecimal(Record::getAmount)).abs());
        indexRespDto.setTotalWithdraw(stringListMap.get(RecordSonType.ORDINARY_WITHDRAWAL).stream().collect(CollectorsUtil.summingBigDecimal(Record::getAmount)).abs());

        return Response.success(indexRespDto);
    }












}
