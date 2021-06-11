package com.ezcoins.project.acl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.base.BaseException;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.exception.CheckException;
import com.ezcoins.project.acl.entity.AclUser;
import com.ezcoins.project.acl.entity.Menu;
import com.ezcoins.project.acl.entity.RoleMenu;
import com.ezcoins.project.acl.entity.req.AclMenuReqDto;
import com.ezcoins.project.acl.mapper.MenuMapper;
import com.ezcoins.project.acl.mapper.RoleMapper;
import com.ezcoins.project.acl.mapper.RoleMenuMapper;
import com.ezcoins.project.acl.service.AclUserService;
import com.ezcoins.project.acl.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-07
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    private RoleMenuMapper roleMenuMapper;


    @Autowired
    private AclUserService aclUserService;

    @Autowired
    private RoleMapper roleMapper;


    /***
     * @Description: 添加权限
     * @Param: [aclMenuReqDto]
     * @return: void
     * @Author: Wanglei
     * @Date: 2021/6/7
     * @param aclMenuReqDto
     */
    @Override
    public void addAclMenu(AclMenuReqDto aclMenuReqDto) {
        //code name route 全部唯一
        LambdaQueryWrapper<Menu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Menu::getTitle, aclMenuReqDto.getTitle()).
                or().eq(Menu::getCode, aclMenuReqDto.getCode()).or().eq(Menu::getCode, aclMenuReqDto.getRouter());
        List<Menu> authMenuList = baseMapper.selectList(lambdaQueryWrapper);
        CheckException.check(authMenuList.size() > 0, "权限内容重复");
        Menu aclMenu = new Menu();
        BeanUtils.copyBeanProp(aclMenu,aclMenuReqDto);
        aclMenu.setCreateBy(ContextHandler.getUserName());
        aclMenu.setDepth(aclMenuReqDto.getParentId() == 0 ? 1 : 2);
        CheckException.checkDb(baseMapper.insert(aclMenu), "权限添加失败");
    }


    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delAclMenu(Integer id) {
         checkChildren(id);
        //删除角色权限关联表
        LambdaQueryWrapper<RoleMenu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(RoleMenu::getMenuId, id);
        roleMenuMapper.delete(lambdaQueryWrapper);
        //删除权限
        baseMapper.deleteById(id);
    }

    private void checkChildren(Integer menuId) {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getParentId, menuId);
        List<Menu> pingAuthMenus = baseMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(pingAuthMenus)){
           throw new BaseException("请先删除子节点");
        }
    }



    /***
     * @Description: 修改权限
     * @Param: [aclMenuReqDto]
     * @return: void
     * @Author: Wanglei
     * @Date: 2021/6/7
     * @param aclMenuReqDto
     */
    @Override
    public void updateAclMenu(AclMenuReqDto aclMenuReqDto) {
        CheckException.checkNull(aclMenuReqDto.getMenuId(), "权限id不能为空");

        //code name route 全部唯一
        LambdaQueryWrapper<Menu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Menu::getTitle, aclMenuReqDto.getTitle()).
                or().eq(Menu::getCode, aclMenuReqDto.getCode()).or().eq(Menu::getRouter, aclMenuReqDto.getRouter());
        List<Menu> authMenuList = baseMapper.selectList(lambdaQueryWrapper);
        CheckException.check(authMenuList.size() > 1, "权限内容重复");

        Menu menu = new Menu();
        BeanUtils.copyBeanProp(menu,aclMenuReqDto);
        menu.setUpdateBy(ContextHandler.getUserName());

        CheckException.checkDb(baseMapper.updateById(menu), "权限更新失败");
    }

    /***
     * @Description: 查询权限树
     * @Param: [aclMenuReqDto]
     * @return: void
     * @Author: Wanglei
     * @Date: 2021/6/7
     */
    @Override
    public List<Menu> findMenuTree() {
        //查询所有菜单
        List<Menu> pingAuthMenuAll = baseMapper.selectList(null);
        //获取分类一级菜单
        List<Menu> menuList = pingAuthMenuAll.stream().filter(authMenu -> authMenu.getParentId() == 0)
                .peek(menu -> { //设置当前子节点
                    menu.setChildren(getChildList(menu, pingAuthMenuAll));
                })
                .collect(Collectors.toList());
        return CollectionUtils.isEmpty(menuList) ? pingAuthMenuAll : menuList;
    }


    //设置子节点
    private List<Menu> getChildList(Menu first, List<Menu> all) {
        List<Menu> list = all.stream().filter(menu -> {
            return menu.getParentId().equals(first.getMenuId());//
        }).peek(menu -> menu.setChildren(getChildList(menu, all))).collect(Collectors.toList());
        return list;
    }



    @Override
    public List<Menu> selectAdminRoleMenu(String userId) {
        //获取当前登录用户用户名
        List<Menu> authMenuList = null;
        if(this.isSysAdmin(userId)) {
            //如果是系统管理员，获取所有权限
            //查询所有菜单
           authMenuList = baseMapper.selectList(null);
        } else {
           authMenuList = roleMapper.selectAdminRoleMenu(userId);
        }
        //获取分类一级菜单
        List<Menu> finalAuthMenuList = authMenuList;
        List<Menu> menuList = authMenuList.stream().filter(authMenu -> authMenu.getParentId() == 0)
                .map(menu -> { //设置当前子节点
                    menu.setChildren(getChildList(menu, finalAuthMenuList));
                    return menu;
                })
                .collect(Collectors.toList());
        return CollectionUtils.isEmpty(menuList) ? authMenuList : menuList;

    }


    /**
     * 判断用户是否系统管理员
     * @param userId
     * @return
     */
    private boolean isSysAdmin(String userId) {
        AclUser user = aclUserService.getById(userId);
        if(null != user && "admin".equals(user.getUserName())) {
            return true;
        }
        return false;
    }

}
