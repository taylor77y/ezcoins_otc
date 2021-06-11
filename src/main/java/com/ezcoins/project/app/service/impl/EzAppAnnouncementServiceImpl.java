package com.ezcoins.project.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.project.app.entity.EzAppAnnouncementTag;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.app.entity.EzAppAnnouncement;
import com.ezcoins.project.app.entity.req.AnnouncementReqDto;
import com.ezcoins.project.app.entity.resp.AppVersionRespDto;
import com.ezcoins.project.app.mapper.EzAppAnnouncementMapper;
import com.ezcoins.project.app.mapper.EzAppAnnouncementTagMapper;
import com.ezcoins.project.app.service.EzAppAnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 平台公告表 服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-10
 */
@Service
public class EzAppAnnouncementServiceImpl extends ServiceImpl<EzAppAnnouncementMapper, EzAppAnnouncement> implements EzAppAnnouncementService {


    @Autowired
    private EzAppAnnouncementTagMapper announcementTagMapper;

    /**
     * 发布公告
     *
     * @param announcementReqDto
     */
    @Override
    public void announce(AnnouncementReqDto announcementReqDto) {
        EzAppAnnouncement ezAppAnnouncement=new EzAppAnnouncement();
        BeanUtils.copyBeanProp(ezAppAnnouncement,announcementReqDto);
        ezAppAnnouncement.setCreateBy(ContextHandler.getUserName());
        if ("0".equals(announcementReqDto.getUserType())){
            baseMapper.insert(ezAppAnnouncement);
        }
    }

    /**
     * 获取 公告列表
     *
     * @param userId
     * @return
     */
    @Override
    public List<AppVersionRespDto> getAnnouncement(String userId) {
        List<AppVersionRespDto> respDtoArrayList=new ArrayList<>();

        //查询所有没有撤销或删除的公告列表
        LambdaQueryWrapper<EzAppAnnouncement> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(EzAppAnnouncement::getIfCancel,"0");
        queryWrapper.orderByAsc(EzAppAnnouncement::getPriority);
        List<EzAppAnnouncement> list=baseMapper.selectList(queryWrapper);
        if (list.size()==0){
            return respDtoArrayList;
        }
        //将所有公告分类
        Map<String,List<EzAppAnnouncement>> mapByUserType=list.stream().collect(Collectors.groupingBy(EzAppAnnouncement::getUserType));



        LambdaQueryWrapper<EzAppAnnouncementTag> tagQueryWrapper=new LambdaQueryWrapper<>();
        tagQueryWrapper.eq(EzAppAnnouncementTag::getUserId,userId);
        List<EzAppAnnouncementTag> tags= announcementTagMapper.selectList(tagQueryWrapper);

        List<String> announcementIdList=tags.stream().map(EzAppAnnouncementTag::getAnnouncementId).collect(Collectors.toList());
         mapByUserType.get("0").forEach(e->{
             AppVersionRespDto versionRespDto= new AppVersionRespDto();
             BeanUtils.copyBeanProp(versionRespDto,e);
             //标记公告是否已读
             if (announcementIdList.contains(e.getId())){
//                versionRespDto.setIsRead("0");
             }
         });

        //判断用户类型


      return respDtoArrayList;


    }
}
