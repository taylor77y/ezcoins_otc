package com.ezcoins.project.coin.service;

import com.ezcoins.project.coin.entity.Type;
import com.baomidou.mybatisplus.extension.service.IService;

import java.security.PrivateKey;

/**
 * <p>
 * 币种类型表 服务类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-17
 */
public interface TypeService extends IService<Type> {


    boolean statusService(Type coinType,Integer type);

}
