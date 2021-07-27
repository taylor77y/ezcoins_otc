package com.ezcoins.project.coin.wallet.cc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ezcoins.base.BaseException;
import com.ezcoins.exception.CustomException;
import com.ezcoins.project.coin.service.WalletService;
import com.ezcoins.project.coin.udun.*;
import com.ezcoins.project.coin.udun.ResponseMessage;
import com.ezcoins.utils.DateUtils;
import com.ezcoins.utils.HttpUtils;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/15 9:59
 * @Version:1.0
 */
@Service
@Slf4j
public class WalletClientService {

    @Value("${wallet.cc.publicKey}")
    private String publicKey;

    @Value("${wallet.cc.gateway}")
    private String gateway;

    @Autowired
    private WalletService walletService;

    /**
     * 创建地址
     *
     * @param account 时间撮
     * @return
     * @throws Exception
     */
    public Address createAddressList(String account) {
        log.info("公钥字符串：{}", publicKey);
        HashMap<String, String> map = new HashMap<>(2);
        map.put("account", account);
        String timestamp = System.currentTimeMillis() + "";
        map.put("timestamp", timestamp);
        String data = JSONObject.toJSONString(map);
        log.debug("createCoinAddress-body[{}]", data);
        //公钥加密
        String s1 = null;
        try {
            s1 = RSAUtils.encryptByPublicKey(data, publicKey);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("加密失败");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", s1);
        String post = HttpUtils.sendPost(gateway + Api.CREATE_ADDRESS, JSON.toJSONString(jsonObject));
        Map<String, Object> resp = (Map) JSONObject.parse(post);
        Integer code = (Integer) resp.get("code");
        Address address = null;
        if (code == 200) {
            JSONArray jsonArray = (JSONArray) resp.get("data");
            JSONObject respData = jsonArray.getJSONObject(0);
            address = JSONObject.toJavaObject(respData, Address.class);
        }
        return address;
    }

    /**
     * 转账
     *
     */
    public Integer transfer(String coin_type, String chain, String request_id, String address, String account, BigDecimal num) {
        HashMap<String, String> map = new HashMap<>(6);
        map.put("account", account);
        map.put("address", address);
        map.put("num", String.valueOf(num));
        map.put("coin_type", coin_type);
        map.put("request_id", request_id);
        map.put("chain", chain);
        String data = JSONObject.toJSONString(map);
        log.debug("transfer-body[{}]", data);
        //公钥加密
        String s1 = null;
        try {
            s1 = RSAUtils.encryptByPublicKey(data, publicKey);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("加密失败");
        }
        log.info("发起提币-body[{}]", data);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", s1);
        String post = HttpUtils.sendPost(gateway + Api.WITHDRAW, JSON.toJSONString(jsonObject));
        Map<String, Object> resp = (Map) JSONObject.parse(post);
        Integer code = (Integer) resp.get("code");

        if (code != 200) {
            log.info("调用失败，重新发起1次：");
            post = HttpUtils.sendPost(gateway + Api.WITHDRAW, JSON.toJSONString(jsonObject));
            resp = (Map) JSONObject.parse(post);
            code = (Integer) resp.get("code");
        }
        if (code != 200){
            String msg = (String) resp.get("msg");
            throw new CustomException(msg);
        }
       return code;
    }




    /**
     * 充值到账回调
     *
     * @param trade 交易实体
     * @return
     * @throws Exception
     */
    public boolean rechargeCallback(Trade trade) throws Exception {
        //金额为最小单位，需要转换,包括amount和fee字段
        String mainCoinType = CoinTypeUtils.getMainCoinType(trade.getChain(), trade.getChain());
        String coinType = CoinTypeUtils.getCoinType(trade.getChain(), trade.getChain());
        return walletService.handleRecharge(trade.getTid(), trade.getAddr(), trade.getAmt(), mainCoinType,coinType);
    }

    public boolean withdrawCallback(Trade trade) {
        //金额为最小单位，需要转换,包括amount和fee字段
        return walletService.handleThirdpartyWithdrawal(trade);
    }
}
