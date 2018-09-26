package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Create by zhouxin
 **/
@Service("iFileService")
public class FileServiceImpl implements IFileService {
    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file,String path){
        //获取文件名
        String fileName = file.getOriginalFilename();
        //获取文件后缀
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        //如果遇到用户一上传了abc.jpg；用户二也上传了abc.jpg。用户二会把用户一的文件覆盖掉，所以filename需要不同
        String uploadFilename=UUID.randomUUID().toString()+"."+fileExtensionName;
        logger.info("开始上传文件，上传文件的文件名：{},上传文件的路径{},上传文件的新文件名{}",fileName,path,uploadFilename);
        File fileDir = new File(path);
        // 判断文件上传的文件夹是否存在，不存在则创建文件夹
        if(!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }

        File targetFile = new File(path,uploadFilename);

        //利用transferTo上传文件
        try {
            file.transferTo(targetFile);
            boolean isSuccess= FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //删除upload文件夹下的文件
            targetFile.delete();
            if(!isSuccess){
                return null;
            }

        } catch (IOException e) {
            logger.error("上传文件失败"+e);
            return null;
        }

        return targetFile.getName();
    }
}
