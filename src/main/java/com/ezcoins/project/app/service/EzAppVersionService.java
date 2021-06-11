package com.ezcoins.project.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.app.entity.EzAppVersion;
import com.ezcoins.project.app.entity.req.AppStatusReqDto;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-05
 */
public interface EzAppVersionService extends IService<EzAppVersion> {

    /**
     *  上传安装包
     * @param file
     * @param platform
     * @return
     */
    public String uploadAppInstallPackage(MultipartFile file, String platform,String id);

    /**
     * 添加app最新版本
     * @param ezAppVersion
     */
    void addAppVersion(EzAppVersion ezAppVersion);



    /***
    * @Description: 上下架app
    * @Param: [appUpdateType, id]
    * @return: void
    * @Author: Wanglei
    * @Date: 2021/6/5
    */
    void updateAppStatus(AppStatusReqDto statusReqDto);


    /**
     *
     * @param platform
     * @param id
     */
    void updateStableApp(String platform, String id);

    /***
     * @Description: 修改稳定版app
     * @Param: [appUpdateType, id]
     * @return: void
     * @Author: Wanglei
     * @Date: 2021/6/5
     */

//    //修改苹果版本号
//    public R editIosVersion(double version);
//
//    //苹果状态修改
//    public R editIosStatus(Integer status);
//
//    //下载安卓apk
//    public void downApk(HttpServletResponse response);

}
