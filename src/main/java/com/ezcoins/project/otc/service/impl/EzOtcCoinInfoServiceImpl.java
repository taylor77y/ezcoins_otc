package com.ezcoins.project.otc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.project.otc.entity.EzOtcCoinInfo;
import com.ezcoins.project.otc.mapper.EzOtcCoinInfoMapper;
import com.ezcoins.project.otc.service.EzOtcCoinInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class EzOtcCoinInfoServiceImpl extends ServiceImpl<EzOtcCoinInfoMapper, EzOtcCoinInfo> implements EzOtcCoinInfoService {


    @Value("${huobi.restapi.gateway}")
    private String huobiUrl;

    private static final RestTemplate restTemplate;

    static {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(5000);
        factory.setConnectTimeout(5000);

        restTemplate = new RestTemplate(factory);
    }

    /**
     * 查询所有可用 coin 信息
     *
     * @param
     * @return
     */
    @Override
    public List<EzOtcCoinInfo> queryAllCoins() {
        LambdaQueryWrapper<EzOtcCoinInfo> lambdaQueryWrapper=new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(EzOtcCoinConfig::getTransactionType, transactionType);
        List<EzOtcCoinInfo> lists = baseMapper.selectList(lambdaQueryWrapper);
        return lists;
    }

    @Override
    public List<EzOtcCoinInfo> queryAllCoinsFromHuobi() {

        ResponseEntity<String> response = restTemplate.getForEntity(huobiUrl, String.class);
        String transferResponseStr = response.getBody();
//        log.debug("Received {}", transferResponseStr);
        return null;
    }
}
