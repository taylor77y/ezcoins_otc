package com.ezcoins.project.otc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.otc.entity.OtcFiatCurrency;

import java.util.List;

public interface OtcFiatCurrencyService extends IService<OtcFiatCurrency> {

    List<OtcFiatCurrency> fiatList();
}
