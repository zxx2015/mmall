package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Create by zhouxin
 **/
public interface IFileService {
    String upload(MultipartFile file, String path);
}
