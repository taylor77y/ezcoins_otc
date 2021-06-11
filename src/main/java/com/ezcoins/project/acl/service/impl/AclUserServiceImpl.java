package com.ezcoins.project.acl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.utils.EncoderUtil;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.exception.CheckException;
import com.ezcoins.project.acl.entity.AclUser;
import com.ezcoins.project.acl.entity.UserRole;
import com.ezcoins.project.acl.entity.req.AclUserReqDto;
import com.ezcoins.project.acl.mapper.AclUserMapper;
import com.ezcoins.project.acl.mapper.UserRoleMapper;
import com.ezcoins.project.acl.service.AclUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-01-12
 */
@Service
public class AclUserServiceImpl extends ServiceImpl<AclUserMapper, AclUser> implements AclUserService {

    @Autowired
    private UserRoleMapper userRoleMapper;


    @Override
    public AclUser selectByUsername(String username) {
        return baseMapper.selectOne(new QueryWrapper<AclUser>().lambda().eq(AclUser::getUserName, username));
    }

    @Override
    public void register(AclUserReqDto user) {
        LambdaQueryWrapper<AclUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AclUser::getUserName, user.getUserName());
        AclUser admin = baseMapper.selectOne(queryWrapper);
        CheckException.checkNotNull(admin, "用户名已存在");

        AclUser aclUser = new AclUser();
        BeanUtils.copyBeanProp(aclUser,user);
        aclUser.setPassword(EncoderUtil.encode(user.getPassword()));
        aclUser.setCreateBy(ContextHandler.getUserName());

        CheckException.checkDb(baseMapper.insert(aclUser), "账号管理员失败");
        //添加用户角色关系
        UserRole userRole = new UserRole();
        userRole.setAdminId(aclUser.getId());
        userRole.setRoleId(user.getRoleId());
        CheckException.checkDb(userRoleMapper.insert(userRole), "添加角色失败");
    }
}
