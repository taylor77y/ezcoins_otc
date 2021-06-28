package com.ezcoins.project.coin.service.impl;

import com.ezcoins.project.coin.entity.Record;
import com.ezcoins.project.coin.mapper.RecordMapper;
import com.ezcoins.project.coin.service.RecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 资产记录表 服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-28
 */
@Service
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record> implements RecordService {

}
