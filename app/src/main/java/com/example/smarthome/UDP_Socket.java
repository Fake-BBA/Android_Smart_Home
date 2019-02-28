package com.example.smarthome;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDP_Socket implements Serializable {
    final String LOG_TAG = "UDP_Socket";   //日志类型
    DatagramSocket mSocket;             //套接字
    ReceiveThread mReceiveThread;       //接收线程
    SendThread mSendThread;             //发送线程
    protected Handler mHandlerToClass;          //向类发送消息的handler
    protected Handler mHandlerFromClass;          //接收Class类发送消息的handler
    //InetAddress address;
    int port;

    UDP_Socket(int port) throws IOException {

        if (mSocket == null || mSocket.isClosed()) {
            try {
                //address = InetAddress.getByName(domain_name);
                //this.port=port;
               // mSocket = new DatagramSocket(port);
                mSocket = new DatagramSocket(port); //监听1026端口
                //mSocket.connect(address, port);
                //Log.i(LOG_TAG, "connect "+domain_name);
                //Log.i(LOG_TAG, "local port is : "+mSocket.getLocalPort());
                //开启接收线程
                mReceiveThread = new ReceiveThread();
                mReceiveThread.start();

            } catch (SocketException e) {
                Log.e(LOG_TAG, "connect fail");
                e.printStackTrace();
                Log.e(LOG_TAG, e.toString());

            }
        }
    }

    public  int Bind(InetAddress localAddress,int localPort)
    {
        return 1;
    }

    public int Send(byte sendBuffer[],int len,InetAddress remoteAddress,int port)
    {
        SendThread sendThread=new SendThread(sendBuffer,len,remoteAddress,port);
        return 1;
    }

    class SendThread extends Thread{
        private byte sendBuffer[];
        private int  length;
        private InetAddress remoteIP;
        private int port;

        SendThread()
        {
            //this.sendBuffer[]=new byte[1024];
        }

        SendThread(byte sendBuffer[],int len,InetAddress remoteAddress,int port)
        {
            this.sendBuffer=sendBuffer;
            this.length=len;
            this.remoteIP=remoteAddress;
            this.port=port;
            this.start();

        };

        @ Override
        public void run() {
            super.run();
            try {
                if (mSocket == null || mSocket.isClosed()) {
                    Log.e(LOG_TAG, "Socket is null");
                }
                //Log.i(LOG_TAG, "send Remote IP is " + remoteIP);
                final DatagramPacket packet = new DatagramPacket(sendBuffer, length, remoteIP, port);
                mSocket.send(packet);

            } catch (UnknownHostException e) {
                Log.e(LOG_TAG, "Send Fail");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Send Fail");
                e.printStackTrace();
            }
        }
    };

    private class ReceiveThread extends Thread {
        byte recvData[];
        ReceiveThread()
        {
            recvData=new byte[1024];
        }
        @ Override
        public void run() {
            super.run();
            if (mSocket == null || mSocket.isClosed())
            {
                Log.e(LOG_TAG,"Receive Thread Socket is null");
                return;
            }

            while(!Thread.currentThread().isInterrupted())
            {
                try {
                    byte datas[] = new byte[512];
                    DatagramPacket packet = new DatagramPacket(datas, datas.length);
                    mSocket.receive(packet);
                    recvData=packet.getData();

                    int functionWord=recvData[4]&0xff;
                    Message message = Message.obtain();
                    switch(functionWord)
                    {
                        case 0:
                            break;
                        case 1:
                            break;
                        case 2:
                            message = Message.obtain();
                            message.what=2;
                            break;
                        case 3:
                            break;
                        case 4:
                            message = Message.obtain();
                            message.what=4;
                            break;
                        case 255:
                            message = Message.obtain();
                            message.what=255;


                            break;
                            default:
                                break;
                    }
                    message.obj=recvData;
                    mHandlerToClass.sendMessage(message);

                    Log.i(LOG_TAG,"recv message:"+functionWord);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Recv Fail");
                    e.printStackTrace();
                }
            }
        }
    }
}
