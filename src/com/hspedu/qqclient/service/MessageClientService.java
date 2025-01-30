package com.hspedu.qqclient.service;

import com.hspedu.qqcommon.Message;
import com.hspedu.qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;

/**
 * @ author: bin
 * @ version :1.0
 * 该类，提供和消息相关的的服务方法
 */
public class MessageClientService {
    /**
     *
     * @param content 内容
     * @param senderId 发送者
     */
    public void sendMessageToAll(String content,String senderId){
        //构建message
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_TO_ALL_MES);//群发消息
        message.setSender(senderId);
        message.setContent(content);
        message.setSendTime(new Date().toString());//发送时间设置到Message对象
        System.out.println(senderId+" 对 大家 说"+content);
        //发送给服务器
        try {
            ObjectOutputStream oos = new ObjectOutputStream(MangeClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     *
     * @param content 内容
     * @param senderId 发送用户id
     * @param getterId 接收用户id
     */
    public void sendMessageToOne(String content,String senderId,String getterId){
        //构建Message
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_COMM_MES);//普通的聊天消息
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setContent(content);
        message.setSendTime(new Date().toString());//发送时间设置到Message对象
        System.out.println(senderId+" 对"+getterId+" 说"+content);
        //发送给服务器
        try {
            ObjectOutputStream oos = new ObjectOutputStream(MangeClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
