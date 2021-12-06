package com.ezcoins.project.otc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ezcoins.project.otc.entity.EzAdvertisingBusiness;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wanglei
 * @since 2021-06-16
 */
public interface EzAdvertisingBusinessMapper extends BaseMapper<EzAdvertisingBusiness> {

    Integer existByAdvertisingName(@Param("advertisingName")String advertisingName);
}
