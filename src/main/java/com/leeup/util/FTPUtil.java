package com.leeup.util;

import lombok.Data;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * @ClassName FTPUtil
 * @Description FTP服务器
 * @Author李闯
 * @Date 2018/9/1 15:49
 * @Version 1.0
 **/
@Data
public class FTPUtil {

    private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static String ftpPass = PropertiesUtil.getProperty("ftp.pass");


    /**
     * @Author 李闯
     * @Description  boolean 用来返回上传成功还是失败
     * @Date 15:55 2018/9/1
     * @Param [fileList 批量上传]
     * @return boolean
     **/
    public static boolean uploadFile(List<File> fileList) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp,21,ftpUser,ftpPass);
        logger.info("开始连接ftp服务器");
        boolean result = ftpUtil.uploadFile("img",fileList);//传到ftp件夹下img文件夹下，将fileList传过来，我们将异常抛出到业务层，在业务层进行处理
        logger.info("开始连接ftp服务器,结束上传，上传结果:{}",result);
        return result;
    }

    /**
     * @Author 李闯
     * @Description 对外暴露上面的方法，业务逻辑写在这里
     * @Date 15:57 2018/9/1
     * @Param [remotePath 远程路径 上传到ftp服务器上，ftp在linux下是一个文件夹，如果想上传到ftp服务器上的文件夹再下一层的文件夹的话，我们需要用到remotepath
     * 使我们上传的路径再多一些, fileList 批量上传文件]
     * @return boolean
     **/
    private boolean uploadFile(String remotePath,List<File> fileList) throws IOException {
        boolean uploaded = true;
        FileInputStream fis =  null;
        //连接ftp服务器
        if (connectServer(this.getIp(),this.port,this.user,this.pwd)){
            //通过ftpClient进行操作
            //使用ftpClient进行文件目录的更改
            try {
                ftpClient.changeWorkingDirectory(remotePath);//如果我们传空过来的话就切换不了了
                //设置我们的缓冲区
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);//将我们的文件设置成一个二进制的文件的类型，放置一个乱码的问题
                //之前我们在FTP上设置的是被动模式，也对外开放了一个服务的移动端口范围
                ftpClient.enterLocalPassiveMode();//打开本地的被动模式
                //遍历fileList
                for (File fileItem : fileList){
                    //通过fileInputStrem
                    fis = new FileInputStream(fileItem);
                    //通过input流调用fileclient的sotreFile方法
                    ftpClient.storeFile(fileItem.getName(),fis);//存储文件

                }
            } catch (IOException e) {
                logger.error("上传文件异常",e);
                uploaded = false;
                e.printStackTrace();
            } finally {
                fis.close();
                ftpClient.disconnect();
            }
        }
        return uploaded;
    }

    /**
     * @Author 李闯
     * @Description 连接ftp服务器我们再进行封装
     * @Date 16:01 2018/9/1
     * @Param [ip, port, user, ftpPass]
     * @return boolean
     **/
    private boolean connectServer(String ip,int port,String user,String ftpPass){

        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);

            //登录，将账号和密码传过来,login的返回值是一个boolean也是成功或者失败，我们把这个返回值返回给上一层，如果连接成功的话我们再继续调用文件上传功能
            isSuccess = ftpClient.login(user,ftpPass);
        } catch (IOException e) {
            logger.error("连接FTP服务器异常",e);
        }
        return isSuccess;
    }

    private String ip;
    private Integer port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;

    public FTPUtil(String ip, Integer port, String user, String pwd) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
    }
}
