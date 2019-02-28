package com.example.smarthome;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public String Account="123";
    public String Password="123";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText account=(EditText)findViewById(R.id.editText);
        final EditText password=(EditText)findViewById(R.id.editText2);
        Button   login=(Button)findViewById(R.id.LoginButton);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(account.getText().toString().equals(Account)&&password.getText().toString().equals(Password))//账号密码正确，跳转界面
                {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, ChooseDevices.class);
                    startActivity(intent);


                }
                else
                    Toast.makeText(MainActivity.this,"账号或密码错误",Toast.LENGTH_LONG).show();
            }
        });
    }
}
