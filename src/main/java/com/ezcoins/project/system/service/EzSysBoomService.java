package com.ezcoins.project.system.service;

import com.ezcoins.project.otc.entity.req.PageQuery;
import com.ezcoins.project.otc.entity.resp.Info;
import com.ezcoins.project.system.entity.EzSysBoom;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.response.Response;

/**
 * <p>
 * 首页轮播图 服务类
 * </p>
 *
 * @author wanglei
 * @since 2021-07-29
 */
public interface EzSysBoomService extends IService<EzSysBoom> {

    Response<Info> info(PageQuery pageQuery);
}
