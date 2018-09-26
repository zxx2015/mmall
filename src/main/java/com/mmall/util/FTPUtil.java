package com.mmall.util;

import com.mmall.service.impl.FileServiceImpl;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Create by zhouxin
 **/
public class FTPUtil {


    private static String ftpIp=PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static String ftpPwd = PropertiesUtil.getProperty("ftp.pass");

    private static Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    public FTPUtil(String ip, int port, String user, String pwd) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
    }

    public static boolean uploadFile(List<File> fileList){
        //创建ftp对象
        FTPUtil ftpUtil = new FTPUtil(ftpIp,21,ftpUser,ftpPwd);
        logger.info("开始连接ftp服务器");
        try {
            boolean result = ftpUtil.uploadFile(null,fileList);
            logger.info("上传文件到服务器，结束上传，上传结果{}",result);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean uploadFile(String remotePath,List<File> fileList) throws IOException {
        boolean upload = false;
        FileInputStream fileInputStream = null;
        if(connectFtpServer(this.ip,this.port,this.user,this.pwd)){
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();

                for (File file:fileList) {
                    fileInputStream = new FileInputStream(file);
                    upload = ftpClient.storeFile(file.getName(),fileInputStream);
                    logger.info("storefile方法执行");
                }
            } catch (IOException e) {
                logger.error("上传文件失败",e);
            }
            finally {
                fileInputStream.close();
                ftpClient.disconnect();
            }
        }
        else {
            logger.error("未连接上服务器");
        }
        return upload;
    }

    private boolean connectFtpServer(String ip, int port, String user, String pwd){
        //利用FTPClient连接服务器
        //调用connect(ip)
        //调用login(user,pwd)
        ftpClient = new FTPClient();
        boolean isSuccess = false;
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user,pwd);
        } catch (IOException e) {
            logger.error("连接服务器失败",e);
        }
        return isSuccess;
    }

    private String ip;
    private int port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
