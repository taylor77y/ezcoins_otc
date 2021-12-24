package com.ezcoins.project.otc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.project.otc.entity.OtcOrder;
import com.ezcoins.project.otc.mapper.OtcOrderMapper;
import com.ezcoins.project.otc.service.OtcOrderService;
import org.springframework.stereotype.Service;

@Service
public class OtcOrderServiceImpl extends ServiceImpl<OtcOrderMapper, OtcOrder> implements OtcOrderService {
}
