package com.hspedu.qqclient.service;

import com.hspedu.qqcommon.Message;
import com.hspedu.qqcommon.MessageType;
import com.hspedu.qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @ author: bin
 * @ version :1.0
 * 该类完成用户登录和用户验证等功能
 */
public class UserClientService {
    private User u = new User();
    //因为我们可能在其他地方使用user信息，因此作为成员属性
    //因为Socket在其它地方也可能会使用，因此作为属性较好
    private Socket socket;

    //根据userId和pwd到服务器验证该用户是否合法
    public boolean checkUser(String userId, String pwd) {
        boolean b = false;
        //创建user对象
        u.setUserId(userId);
        u.setPasswd(pwd);
        //连接到服务端发送u对象
        try {
            socket = new Socket(InetAddress.getByName("127.0.0.1"), 9999);
            //得到ObjectOutputStream对象
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(u);//发送User对象
            //读取从服务器回复的Message对象
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message ms = (Message) ois.readObject();
            if (ms.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCEED)) {//登录OK


                //创建一个和服务器端保持通信的线程->创建一个线程类ClientConnectServerThread
                ClientConnectServerThread clientConnectServerThread = new ClientConnectServerThread(socket);
                //启动客户端
                clientConnectServerThread.start();
                //为了客户端的扩展，我们将线程放入集合管理
                MangeClientConnectServerThread.addClientConnectServerThread(userId, clientConnectServerThread);
                b = true;
            } else {
               //如果登录失败，我们就不能启动和服务器通信的线程，关闭socket
               socket.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return b;
    }
    //向服务器端请求在线用户列表
    public void onlineFriendList(){
        //发送一个Message，类型MESSAGE_GET_ONLINE_FRIEND
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
        message.setSender(u.getUserId());
        //发送给服务器


        try {//应该得到当前线程的socket对应的ObjectOutputStream对象
            ObjectOutputStream  oos = new ObjectOutputStream(
                    MangeClientConnectServerThread.getClientConnectServerThread(
                            u.getUserId()).getSocket().getOutputStream());
            oos.writeObject(message);//发送一个message 对象
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    //编写方法，退出客户端，并给服务端发送一个退出系统的Message对象
    public void logout(){
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
        message.setSender(u.getUserId());//一定要指定是哪个客户端Id
        //发送message
        try {
            //ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectOutputStream oos = new ObjectOutputStream
                    (MangeClientConnectServerThread.getClientConnectServerThread
                            (u.getUserId()).getSocket().getOutputStream());
            oos.writeObject(message);
            System.out.println(u.getUserId()+" 退出系统");
            System.exit(0);//结束进程
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
