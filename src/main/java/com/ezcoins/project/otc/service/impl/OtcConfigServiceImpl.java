package com.ezcoins.project.otc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.project.otc.entity.OtcConfig;
import com.ezcoins.project.otc.entity.req.OtcConfigReqDto;
import com.ezcoins.project.otc.mapper.OtcConfigMapper;
import com.ezcoins.project.otc.service.OtcConfigService;
import com.ezcoins.response.Response;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.utils.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class OtcConfigServiceImpl extends ServiceImpl<OtcConfigMapper, OtcConfig> implements OtcConfigService {

    /***
     * @Description: 增加提币地址
     * @Param: [addrReqDto]
     * @return: com.ezcoins.response.Response
     * @Author: taylor
     * @Date: 2021/12/3
     * @param otcConfigReqDto
     */
    @Override
    public Response addOrUpdateOtcConfig(OtcConfigReqDto otcConfigReqDto) {
//        String userId = ContextHandler.getUserId();
        OtcConfig otcConfig = new OtcConfig();
        BeanUtils.copyBeanProp(otcConfig,otcConfigReqDto);
//        otcConfig.setUserId(userId);
        if (StringUtils.isEmpty(otcConfigReqDto.getId())){
//            userWalletAddr.setCreateBy(userId);
            baseMapper.insert(otcConfig);
        }else {
//            userWalletAddr.setUpdateBy(userId);
            baseMapper.updateById(otcConfig);
        }
        return Response.success();
    }
}
