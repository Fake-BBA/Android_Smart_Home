package com.example.smarthome;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.List;

public class ChooseDevices extends AppCompatActivity {
    final String LOG_TAG = "ChooseDevices";   //日志类型
    private Button ReflashDevice;
    private  TextView NumberOfDevice;
    private  Button Light;
    private  Button Temperature;

    public UDP_Socket mUDP_Socket;   //UDP Sokcet 类
    protected Handler mHandlerToUDP_Socket;   //给UDP类发送消息
    protected Handler mHandlerFromUDP_Socket;   //接收UDP类发送的消息
    public int devicesNum;  //找到的设备数
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_devices);

         ReflashDevice=(Button)findViewById(R.id.CheckDevice);
         NumberOfDevice=(TextView)findViewById(R.id.NumberOfDevice);
         Light=(Button)findViewById(R.id.Light);
         Temperature=(Button)findViewById(R.id.TemperatureButton);

         try {
             mUDP_Socket = new UDP_Socket(1026);
         }
        catch(IOException e){
        e.printStackTrace();
        Log.e(LOG_TAG, "UDP init Fail!");
    }

        ReflashDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  //刷新设备按钮
                Log.i(LOG_TAG, "reflash devices");
                byte sendBuffer[]=new byte[100];
                byte ip[]=new byte[4];
                ip[0]=(byte)172;
                ip[1]=20;
                ip[2]=10;
                devicesNum=0;
                sendBuffer[0]=1;
                sendBuffer[1]=0;
                sendBuffer[2]=0;
                sendBuffer[3]=0;
                sendBuffer[4]=(byte)1;    //查找设备
                //NumberOfDevice.setText("发出请求");
                try{
                    //InetAddress localIP=InetAddress.getLocalHost(); //获取本地IP
                    //data=localIP.getAddress();
                    for(int i=1;i<254;i++)
                    {
                        ip[3]=(byte)i;

                        InetAddress remoteIP = InetAddress.getByAddress(ip);
                        mUDP_Socket.Send(sendBuffer,5,remoteIP, 1026);
                    }
                }
                catch(IOException e){
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "扫描局域网完成",
                        Toast.LENGTH_SHORT).show();

            }
        });

        Light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  //点击灯
                Intent intent=new Intent();
                intent.setClass(ChooseDevices.this, LightActivity.class);
                //intent.putExtra("mUDP_Socket",mUDP_Socket);
                startActivity(intent);
            }
        });

        Temperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  //点击温度计
                Intent intent=new Intent();
                intent.setClass(ChooseDevices.this, TemperatureActivity.class);
                startActivity(intent);
            }
        });

        mHandlerFromUDP_Socket=new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what)
                {
                    case 255:
                        byte recvData[]=(byte [])msg.obj;
                        devicesNum++;
                        NumberOfDevice.setText(Integer.toString(devicesNum));
                        break;
                }
            }
        };
        mUDP_Socket.mHandlerToClass=mHandlerFromUDP_Socket; //将本类handler复制给UDP_Socket类
        //mHandlerToUDP_Socket=mUDP_Socket.mHandlerFromClass; //复制UDP_Socket的handler到本类
    }



}
