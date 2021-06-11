package com.ezcoins.project.acl.mapper;

import com.ezcoins.project.acl.entity.Menu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wanglei
 * @since 2021-06-07
 */
public interface MenuMapper extends BaseMapper<Menu> {

    List<String> selectCodeValueByUserId(String id);
}
