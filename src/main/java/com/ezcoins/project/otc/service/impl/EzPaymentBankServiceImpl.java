package com.ezcoins.project.otc.service.impl;

import com.ezcoins.base.BaseException;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.consumer.entity.EzUserKyc;
import com.ezcoins.project.consumer.service.EzUserKycService;
import com.ezcoins.project.otc.entity.EzPaymentBank;
import com.ezcoins.project.otc.entity.req.BankReqDto;
import com.ezcoins.project.otc.mapper.EzPaymentBankMapper;
import com.ezcoins.project.otc.service.EzPaymentBankService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.utils.MessageUtils;
import com.ezcoins.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-15
 */
@Service
public class EzPaymentBankServiceImpl extends ServiceImpl<EzPaymentBankMapper, EzPaymentBank> implements EzPaymentBankService {

    @Autowired
    private EzUserKycService kycService;



    /**
     * @Description: 添加/修改 银行收款方式
     * @Param: [bankReqDto]
     * @return: void
     * @Author: Wanglei
     * @Date: 2021/6/15
     * @param bankReqDto
     */
    @Override
    public void bankPaymentMethod(BankReqDto bankReqDto) {
        String realName = bankReqDto.getRealName();
        EzUserKyc one = kycService.getOneApprove(ContextHandler.getUserId());
        if (null == one) {
            throw new BaseException(MessageUtils.message("用户实名认证尚未通过"));
        }
        if (!one.getRealName().equals(realName)) {
            throw new BaseException(MessageUtils.message("请输入认证的真实姓名"));
        }
        String id = bankReqDto.getId();

        EzPaymentBank EzPaymentBank = new EzPaymentBank();
        BeanUtils.copyBeanProp(EzPaymentBank, bankReqDto);
        if (StringUtils.isEmpty(id)){//添加
            baseMapper.insert(EzPaymentBank);
        }else {//修改
            baseMapper.updateById(EzPaymentBank);
        }
    }
}
