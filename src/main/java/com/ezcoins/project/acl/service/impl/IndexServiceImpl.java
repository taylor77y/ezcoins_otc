package com.ezcoins.project.acl.service.impl;

import com.ezcoins.redis.RedisCache;
import com.ezcoins.project.acl.mapper.MenuMapper;
import com.ezcoins.project.acl.service.AclUserService;
import com.ezcoins.project.acl.service.IndexService;
import com.ezcoins.project.acl.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private AclUserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private MenuMapper menuMapper;



    @Autowired
    private RedisCache redisCache;



}
