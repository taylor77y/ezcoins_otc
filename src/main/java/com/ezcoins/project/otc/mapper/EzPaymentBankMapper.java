package com.ezcoins.project.otc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ezcoins.project.otc.entity.EzPaymentBank;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户 支付宝信息 Mapper 接口
 * </p>
 *
 * @author taylor
 * @since 2021-12-03
 */
public interface EzPaymentBankMapper extends BaseMapper<EzPaymentBank> {

    int updateStatusById(@Param("id")String id, @Param("status")String status);
}
