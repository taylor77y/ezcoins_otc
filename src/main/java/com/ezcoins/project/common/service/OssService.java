package com.ezcoins.project.common.service;


import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.ezcoins.utils.DateUtils;
import com.ezcoins.config.OssConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
public class OssService {
    /**
     *
     * @param file
     * @param model 文件属于模块
     * @return
     */
    public String uploadFileAvatar(MultipartFile file,String model) {
        // 工具类获取值
        String endpoint = OssConfig.END_POIND;
        String accessKeyId = OssConfig.ACCESS_KEY_ID;
        String accessKeySecret = OssConfig.ACCESS_KEY_SECRET;
        String bucketName = OssConfig.BUCKET_NAME;

        try {
            // 创建OSS实例。
                       OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            //获取上传文件输入流
            InputStream inputStream = file.getInputStream();
            //获取文件名称
            String fileName = file.getOriginalFilename();

            if ("Android".equals(model) || "IOS".equals(model)){
                fileName=file.getOriginalFilename();
                SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmm");
                String str=sdf.format(new Date());
                fileName = "taskezcoins"+"/"+model+"/"+str+"-"+fileName;

            }else {
                //1 在文件名称里面添加随机唯一的值
                String uuid = UUID.randomUUID().toString().replaceAll("-","");
                // yuy76t5rew01.jpg
                fileName = uuid+fileName;
                //2 把文件按照日期进行分类
                //获取当前日期
                //   2019/11/12
                String datePath = DateUtils.datePath();
                //拼接
                //  2019/11/12/ewtqr313401.jpg
                fileName = "taskezcoins"+"/"+model+"/"+datePath+"/"+fileName;
            }


            //调用oss方法实现上传
            //第一个参数  Bucket名称
            //第二个参数  上传到oss文件路径和文件名称   aa/bb/1.jpg
            //第三个参数  上传文件输入流
            ossClient.putObject(bucketName,fileName , inputStream);

            // 关闭OSSClient。
            ossClient.shutdown();

            //把上传之后文件路径返回
            //需要把上传到阿里云oss路径手动拼接出来
            //  https://edu-guli-1010.oss-cn-beijing.aliyuncs.com/01.jpg
            String url = "https://"+bucketName+"."+endpoint+"/"+fileName;
            return url;
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
