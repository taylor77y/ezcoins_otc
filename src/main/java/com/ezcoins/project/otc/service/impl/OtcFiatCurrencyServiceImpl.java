package com.ezcoins.project.otc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.project.otc.entity.OtcFiatCurrency;
import com.ezcoins.project.otc.mapper.OtcFiatCurrencyMapper;
import com.ezcoins.project.otc.service.OtcFiatCurrencyService;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OtcFiatCurrencyServiceImpl extends ServiceImpl<OtcFiatCurrencyMapper, OtcFiatCurrency> implements OtcFiatCurrencyService {


    @Value("${binance.restapi.gateway}")
    private String binanceUrl;

    private static final RestTemplate restTemplate;

    static {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(5000);
        factory.setConnectTimeout(5000);

        restTemplate = new RestTemplate(factory);
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
    }

    /**
     * 法币币种列表
     *
     * @param
     * @return
     */
    @Override
    public List<OtcFiatCurrency> fiatList() {
        LambdaQueryWrapper<OtcFiatCurrency> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        return baseMapper.selectList(lambdaQueryWrapper);
    }

    /**
     * 法币币种列表
     *
     * @param
     * @return
     */
    @Override
    public List<OtcFiatCurrency> fiatListFromBinance() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity("https://p2p.binance.com/bapi/c2c/v1/public/c2c/trade-rule/fiat-list", request, String.class);

        String transferResponse = response.getBody();
        log.debug("Received {}", transferResponse);

        Map<String, Object> transferResponseMap;
        try {
            transferResponseMap = objectMapper.readValue(transferResponse, Map.class);
        } catch (IOException e) {
            log.error("Failed to convert json to map", e);
            return Collections.emptyList();
        }
        return (List<OtcFiatCurrency>)transferResponseMap.get("data");
    }
}
