package com.ezcoins.project.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.base.BaseException;
import com.ezcoins.constant.enums.app.AppUpdateType;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.app.entity.EzAppVersion;
import com.ezcoins.project.app.entity.req.AppStatusReqDto;
import com.ezcoins.project.app.mapper.EzAppVersionMapper;
import com.ezcoins.project.app.service.EzAppVersionService;
import com.ezcoins.project.common.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-05
 */
@Service
public class EzAppVersionServiceImpl extends ServiceImpl<EzAppVersionMapper, EzAppVersion> implements EzAppVersionService {


    @Autowired
    private OssService ossService;

    /**
     * 上传安装包
     *
     * @param file
     * @param platform
     * @return
     */
    @Override
    public String uploadAppInstallPackage(MultipartFile file, String platform, String id) {
        String url = ossService.uploadFileAvatar(file, platform);
        if (null!=id) {
            LambdaUpdateWrapper<EzAppVersion> updateWrapper=new LambdaUpdateWrapper<>();
            updateWrapper.eq(EzAppVersion::getId,id).set(EzAppVersion::getAddr,url)
                    .set(EzAppVersion::getUpdateBy, ContextHandler.getUserName());
            baseMapper.update(null,updateWrapper);
            return null;
        }
        return url;
    }

    /**
     * 添加app最新版本
     *
     * @param ezAppVersion
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void addAppVersion(EzAppVersion ezAppVersion) {
        LambdaQueryWrapper<EzAppVersion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EzAppVersion::getThisVersion, ezAppVersion.getThisVersion());
        if (baseMapper.selectCount(queryWrapper)>0){
            throw new BaseException("版本重复");
        }
        LambdaUpdateWrapper<EzAppVersion> updateWrapper = new UpdateWrapper<EzAppVersion>().lambda();
        updateWrapper.eq(EzAppVersion::getIsDefault, "0")
                .eq(EzAppVersion::getPlatform,ezAppVersion.getPlatform())
                .set(EzAppVersion::getIsDefault,"1");
        baseMapper.update(null,updateWrapper);
        ezAppVersion.setIsRacking(AppUpdateType.ONSHELF.getCode());
        ezAppVersion.setIsDefault("0");
        baseMapper.insert(ezAppVersion);
    }

    /***
     * @Description: 上下架app
     * @Param: [appUpdateType, id]
     * @return: void
     * @Author: Wanglei
     * @Date: 2021/6/5
     */
    @Override
    public void updateAppStatus(AppStatusReqDto statusReqDto) {
        //查看下架app是否为稳定版本
        if (statusReqDto.getIsRacking().equals(AppUpdateType.OFFSHELF.getCode())) {
            LambdaQueryWrapper<EzAppVersion> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(EzAppVersion::getIsDefault, "0");
            queryWrapper.eq(EzAppVersion::getPlatform, statusReqDto.getPlatform());
            EzAppVersion ezAppVersion = baseMapper.selectOne(queryWrapper);
            if (ezAppVersion.getId().equals(statusReqDto.getId())) {
                throw new BaseException("稳定版本不支持下架");
            }
        }
        LambdaUpdateWrapper<EzAppVersion> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(EzAppVersion::getId, statusReqDto.getId()).eq(EzAppVersion::getPlatform, statusReqDto.getPlatform())
                .set(EzAppVersion::getIsRacking, statusReqDto.getIsRacking());
        baseMapper.update(null,updateWrapper);
    }


    /**
     * @param platform
     * @param id
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateStableApp(String platform, String id) {
        if ("0".equals(baseMapper.selectById(id).getIsDefault())){
            throw new BaseException("请先开启其他稳定版");
        }
        //改变稳定app状态
        LambdaUpdateWrapper<EzAppVersion> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(EzAppVersion::getIsDefault, "0")
                .eq(EzAppVersion::getPlatform,platform).
                set(EzAppVersion::getIsDefault,"1");
        baseMapper.update(null, updateWrapper);

        LambdaUpdateWrapper<EzAppVersion> updateWrapper2 = new LambdaUpdateWrapper<>();
        updateWrapper2.eq(EzAppVersion::getId, id).set(EzAppVersion::getIsDefault,"0");
        baseMapper.update(null,updateWrapper2);
    }
}
