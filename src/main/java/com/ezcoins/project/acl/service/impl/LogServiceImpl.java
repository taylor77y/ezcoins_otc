package com.ezcoins.project.acl.service.impl;

import com.ezcoins.project.acl.entity.Log;
import com.ezcoins.project.acl.mapper.LogMapper;
import com.ezcoins.project.acl.service.LogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-07-28
 */
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, Log> implements LogService {

}
