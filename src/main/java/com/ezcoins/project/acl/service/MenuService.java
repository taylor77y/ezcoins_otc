package com.ezcoins.project.acl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.acl.entity.Menu;
import com.ezcoins.project.acl.entity.req.AclMenuReqDto;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-07
 */
public interface MenuService extends IService<Menu> {

    /***
    * @Description:  添加权限
    * @Param: [aclMenuReqDto]
    * @return: void
    * @Author: Wanglei
    * @Date: 2021/6/7
    */
    void addAclMenu(AclMenuReqDto aclMenuReqDto);


    /***
     * @Description:  删除权限
     * @Param: [aclMenuReqDto]
     * @return: void
     * @Author: Wanglei
     * @Date: 2021/6/7
     */
    void delAclMenu(Integer id);


    /***
     * @Description:  修改权限
     * @Param: [aclMenuReqDto]
     * @return: void
     * @Author: Wanglei
     * @Date: 2021/6/7
     */
    void updateAclMenu(AclMenuReqDto aclMenuReqDto);


    /***
     * @Description:  查询权限树
     * @Param: [aclMenuReqDto]
     * @return: void
     * @Author: Wanglei
     * @Date: 2021/6/7
     */
    List<Menu> findMenuTree();


    List<Menu> selectAdminRoleMenu(String userId);
}
