package com.example.smarthome;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;

public class LightActivity extends AppCompatActivity {

    private SeekBar LightSeekBar;
    private TextView CurrentBrightness;
    private TextView DevicePlace;
    private TextView deviceNum;

    UDP_Socket mUDP_Socket;
    private byte sendBuffer[];
    private byte ip[];
    protected Handler mHandlerFromUDP_Socket;   //接收UDP类发送的消息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

         LightSeekBar=(SeekBar)findViewById(R.id.seekBar2);
         CurrentBrightness=(TextView)findViewById(R.id.CurrentBrightness);
         DevicePlace=(TextView)findViewById(R.id.DevicePlace);
         deviceNum=(TextView)findViewById(R.id.DeviceNumber);

         DevicePlace.setText("");  //设置设备号所在位置

         LightSeekBar.setMax(100);   //滑动条最大值
         LightSeekBar.setProgress(50);  //滑动条当前值

        try {
        mUDP_Socket = new UDP_Socket(1027);//(UDP_Socket) intent.getSerializableExtra("mUDP_Socket");
    }
        catch(IOException e){
        e.printStackTrace();
    }


    sendBuffer=new byte[100];
    ip=new byte[4];
    ip[0]=(byte)172;
    ip[1]=20;
    ip[2]=10;
    ip[3]=2;

    sendBuffer[0]=1;
    sendBuffer[1]=0;
    sendBuffer[2]=3;
    sendBuffer[3]=0;
    sendBuffer[4]=(byte)2;    //设置灯

         SeekBar.OnSeekBarChangeListener osbcl = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                int position=LightSeekBar.getProgress();
                CurrentBrightness.setText(Integer.toString(position));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int position=LightSeekBar.getProgress();
                //CurrentBrightness.setText(Integer.toString(position));

                sendBuffer[5]=(byte)position;
                try{
                    //InetAddress localIP=InetAddress.getLocalHost(); //获取本地IP
                    //data=localIP.getAddress();
                    InetAddress remoteIP = InetAddress.getByAddress(ip);
                    mUDP_Socket.Send(sendBuffer,6,remoteIP, 1026);
                }
                catch(IOException e){
                    e.printStackTrace();
                };
            }

        };
        // 为拖动条绑定监听器
        LightSeekBar.setOnSeekBarChangeListener(osbcl);

        mHandlerFromUDP_Socket=new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {

                switch (msg.what)
                {
                    case 2:

                        byte recvData[]=(byte [])msg.obj;

                        short DevideNum=0;
                        int ch1=recvData[0]&0xff;
                        int ch2=recvData[1]&0xff;
                        DevideNum=(short)((ch1<<8)+(ch2));

                        Toast.makeText(getApplicationContext(), "成功设置设备："+Integer.toString(DevideNum)+"亮度",
                                Toast.LENGTH_SHORT).show();
                        deviceNum.setText(Integer.toString(DevideNum));
                        break;
                }

            }
        };
        mUDP_Socket.mHandlerToClass=mHandlerFromUDP_Socket; //将本类handler复制给UDP_Socket类

    }
}
