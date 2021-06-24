package com.ezcoins.project.app.service;

import com.ezcoins.project.app.entity.EzAppAnnouncement;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.app.entity.req.AnnouncementReqDto;
import com.ezcoins.project.app.entity.resp.AppAnnouncementRespDto;
import com.ezcoins.project.app.entity.resp.AppVersionRespDto;

import java.util.List;

/**
 * <p>
 * 平台公告表 服务类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-10
 */
public interface EzAppAnnouncementService extends IService<EzAppAnnouncement> {

    /**
     * 发布公告
     * @param announcementReqDto
     */
    void announce(AnnouncementReqDto announcementReqDto);


    /**
     * 获取 公告列表
     * @param userId
     * @return
     */
    List<AppAnnouncementRespDto> getAnnouncement(String userId);
}
