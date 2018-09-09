package com.leeup.service.impl;

import com.google.common.collect.Lists;
import com.leeup.service.IFileService;
import com.leeup.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @ClassName FileServiceImpl
 * @Description 文件服务的实现类
 * @Author李闯
 * @Date 2018/9/1 11:32
 * @Version 1.0
 **/
@Service("iFileService")
public class FileServiceImpl implements IFileService {

    //服务会经常被调用，所以我们打印一个日志
    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    /**
     * @Author 李闯
     * @Description 将上传文件的文件名返回
     * @Date 11:34 2018/9/1
     * @Param [file 上传的文件, path 上传的路径]
     * @return java.lang.String
     **/
    public String upload(MultipartFile file,String path){
        //1 拿到文件名
        String fileName = file.getOriginalFilename();
        //2 获取扩展名 从后面开始lastIndexOf，查找到.的位置返回，但是这样输出的就是.jpg我们不要点所以+1即可
        //例如abc.jpg
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        //上传文件的名字,如果两个人上传文件名相同的话会被覆盖，我们使用UUID给文件设置新名字
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
        //打印相关日志
        logger.info("开始上传文件，上传文件的文件名:{},上传的路径是:{}，新文件名:{}",fileName,path,uploadFileName);

        //声明文件目录的file
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            //如果目录不存在
            //我们就创建它
            fileDir.setWritable(true);//赋予权限可写，比如我们启动tomcat的用户权限,不一定会有在tomcat，webapps下发布文件夹的权限
            fileDir.mkdirs();//mkdir当前级别的，mkdirs如果所在文件夹在a/b/c/d，这些文件夹服务器都没有我们通过mkdirs来创建
        }
        //创建文件
        File targetFile = new File(path, uploadFileName);//路径+文件名，完成的file

        //使用SpringMVC封装的File
        try {
            file.transferTo(targetFile);
            //文件已经上传成功了

            //todo 将target上传到我们的FTP服务器上
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));//文件已经上传到ftp服务器上

            //todo 上传完之后，删除upload下面的文件
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件异常",e);
            return null;
        }
        //返回目标文件的文件名
        return targetFile.getName();
    }
}
