package com.leeup.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName IFileService
 * @Description 文件处理的服务
 * @Author李闯
 * @Date 2018/9/1 11:32
 * @Version 1.0
 **/
public interface IFileService {

    /**
     * @Author 李闯
     * @Description 将上传文件的文件名返回
     * @Date 17:19 2018/9/2
     * @Param [file, path]
     * @return java.lang.String
     **/
    String upload(MultipartFile file, String path);

}
