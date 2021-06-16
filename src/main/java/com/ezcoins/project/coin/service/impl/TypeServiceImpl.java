package com.ezcoins.project.coin.service.impl;

import com.ezcoins.project.coin.entity.Type;
import com.ezcoins.project.coin.mapper.TypeMapper;
import com.ezcoins.project.coin.service.TypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 币种类型表 服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-16
 */
@Service
public class TypeServiceImpl extends ServiceImpl<TypeMapper, Type> implements TypeService {

}
