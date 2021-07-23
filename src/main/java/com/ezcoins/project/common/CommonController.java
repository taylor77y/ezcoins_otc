package com.ezcoins.project.common;


import com.ezcoins.config.EzCoinsConfig;
import com.ezcoins.config.ServerConfig;
import com.ezcoins.constant.Constants;
import com.ezcoins.response.Response;
import com.ezcoins.utils.FileUploadUtils;
import com.ezcoins.utils.FileUtils;
import com.ezcoins.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;


@RestController
public class CommonController{
    private static final Logger log = LoggerFactory.getLogger(CommonController.class);

    @Autowired
    private ServerConfig serverConfig;


    
    @GetMapping("common/download")
    public void fileDownload(String fileName, Boolean delete, HttpServletResponse response, HttpServletRequest request)
    {
        try
        {
            if (!FileUtils.checkAllowDownload(fileName))
            {
                throw new Exception(StringUtils.format("文件名称({})非法，不允许下载。 ", fileName));
            }
            String realFileName = System.currentTimeMillis() + fileName.substring(fileName.indexOf("_") + 1);
            String filePath = EzCoinsConfig.getDownloadPath() + fileName;

            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, realFileName);
            FileUtils.writeBytes(filePath, response.getOutputStream());
            if (delete)
            {
                FileUtils.deleteFile(filePath);
            }
        }
        catch (Exception e)
        {
            log.error("下载文件失败", e);
        }
    }

    
    @PostMapping("/common/upload")
    public Response uploadFile(@RequestParam("file") MultipartFile file) throws Exception{
        try {
            // 上传文件路径
            String filePath = EzCoinsConfig.getUploadPath();
            // 上传并返回新文件名称
            String fileName = FileUploadUtils.upload(filePath, file);
            String url = serverConfig.getUrl() + fileName;
            HashMap map=new HashMap(2);
            map.put("fileName",fileName);
            map.put("url",url);
            return Response.success(map);
        }
        catch (Exception e){
            return Response.error(e.getMessage());
        }
    }

    
    @GetMapping("/common/download/resource")
    public void resourceDownload(String resource, HttpServletRequest request, HttpServletResponse response)throws Exception{
        try{
            if (!FileUtils.checkAllowDownload(resource)){
                throw new Exception(StringUtils.format("资源文件({})非法，不允许下载。 ", resource));
            }
            // 本地资源路径
            String localPath = EzCoinsConfig.getProfile();
            // 数据库资源地址
            String downloadPath = localPath + StringUtils.substringAfter(resource, Constants.RESOURCE_PREFIX);
            // 下载名称
            String downloadName = StringUtils.substringAfterLast(downloadPath, "/");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, downloadName);
            FileUtils.writeBytes(downloadPath, response.getOutputStream());
        }catch (Exception e) {
            log.error("下载文件失败", e);
        }
    }

    @PostMapping("/common/ossUpload/{model}")
    public Response ossUpload(@RequestParam("file") MultipartFile file,@PathVariable String model) {
        //获取上传文件  MultipartFile
        //返回上传到oss的路径
        try {
            // 上传文件路径
            String filePath = EzCoinsConfig.getUploadPath();
            // 上传并返回新文件名称
            String fileName = FileUploadUtils.upload(filePath, file);
            String url = serverConfig.getUrl() + fileName;
            HashMap map=new HashMap(2);
            map.put("fileName",fileName);
            map.put("url",url);
            return Response.success(map);
        }
        catch (Exception e){
            return Response.error(e.getMessage());
        }
    }
}
