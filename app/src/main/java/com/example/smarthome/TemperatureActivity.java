package com.example.smarthome;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.InetAddress;

public class TemperatureActivity extends AppCompatActivity {
    private SeekBar LightSeekBar;
    private TextView CurrentTemperatrue;
    private TextView deviceNumber;
    private TextView DevicePlace;
    private Button reFlashTemp;

    UDP_Socket mUDP_Socket;
    private byte sendBuffer[];
    private byte ip[];

    protected Handler mHandlerFromUDP_Socket;   //接收UDP类发送的消息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        deviceNumber=(TextView)findViewById(R.id.DeviceNumber);
        CurrentTemperatrue=(TextView)findViewById(R.id.CurrentBrightness);
        DevicePlace=(TextView)findViewById(R.id.DevicePlace);
        reFlashTemp=(Button)findViewById(R.id.TemperatureReFlashButton);

        reFlashTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //刷新设备按钮
                ReFlash();
            }
        });


        DevicePlace.setText("");  //设置设备号所在位置

        CurrentTemperatrue.setText("");  //设置显示温度

        try {
            mUDP_Socket = new UDP_Socket(1028);//(UDP_Socket) intent.getSerializableExtra("mUDP_Socket");
        }
        catch(IOException e){
            e.printStackTrace();
        }

        sendBuffer=new byte[100];
        ip=new byte[4];
        ip[0]=(byte)172;
        ip[1]=20;
        ip[2]=10;
        ip[3]=4;

        sendBuffer[0]=1;
        sendBuffer[1]=0;
        sendBuffer[2]=4;
        sendBuffer[3]=0;
        sendBuffer[4]=(byte)4;    //请求温湿度

        ReFlash();

        mHandlerFromUDP_Socket=new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what)
                {
                    case 4:
                        byte recvData[]=(byte [])msg.obj;

                        short devideNum=0;
                        int ch1=recvData[0]&0xff;
                        int ch2=recvData[1]&0xff;
                        devideNum=(short)((ch1<<8)+(ch2));

                        int temperature=recvData[5]&0xff;
                        int humidity=recvData[6]&0xff;

                        deviceNumber.setText(Integer.toString(ch1));
                        CurrentTemperatrue.setText(Integer.toString(temperature)+"    湿度:"+Integer.toString(humidity));
                        break;
                }
            }
        };
        mUDP_Socket.mHandlerToClass=mHandlerFromUDP_Socket; //将本类handler复制给UDP_Socket类

    }
    public int ReFlash()
    {
        try{
            //InetAddress localIP=InetAddress.getLocalHost(); //获取本地IP
            //data=localIP.getAddress();
            InetAddress remoteIP = InetAddress.getByAddress(ip);
            mUDP_Socket.Send(sendBuffer,6,remoteIP, 1026);
        }
        catch(IOException e){
            e.printStackTrace();
        };
        return 1;
    };
}
