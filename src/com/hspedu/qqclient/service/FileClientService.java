package com.hspedu.qqclient.service;

import com.hspedu.qqcommon.Message;
import com.hspedu.qqcommon.MessageType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @ author: bin
 * @ version :1.0
 * 该类完成文件传输服务
 */
public class FileClientService {
    /**
     * @param src      源文件位置
     * @param dest     目标文件位置
     * @param sendId   发送用户id
     * @param getterId 接收用户id
     */
    public void sendFileToOne(String src, String dest, String sendId, String getterId) {
        //读取src文件->封装到message
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_FILE_MES);
        message.setSender(sendId);
        message.setGetter(getterId);
        message.setSrc(src);
        message.setDest(dest);
        //需要将文件读取
        FileInputStream fileInputStream = null;
        byte[] fileBytes = new byte[(int) new File(src).length()];
        try {
            fileInputStream = new FileInputStream(src);
            fileInputStream.read(fileBytes);//将src文件读入到程序的字节数组
            //将文件对应的字节数组设置为message
            message.setFileBytes(fileBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            //关闭
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        System.out.println("\n" + sendId + "给 " + getterId +
                " 发送文件：" + src + " 到对方的电脑的目录" + dest);
        //发送
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    MangeClientConnectServerThread.getClientConnectServerThread(
                            sendId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
