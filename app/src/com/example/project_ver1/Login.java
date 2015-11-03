package com.example.project_ver1;


import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {

	Button btnLogin, btnSignup;
	EditText editAccount, editPassword;
	public Handler MessageHandler;
//	public static  String address = "192.168.0.102";
	public static String address = "192.168.0.102";
	public static int port1 = 3838;
	String savePath = Environment.getExternalStorageDirectory().getPath()+"/PolarTrade";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		btnLogin = (Button) this.findViewById(R.id.btnLogin);
		btnSignup = (Button) this.findViewById(R.id.btnSignup);
		editAccount = (EditText) this.findViewById(R.id.editAccount);
		editPassword = (EditText) this.findViewById(R.id.editPassword);
		
		MessageHandler = new Handler() {

			public void handleMessage(Message msg) {
				switch (msg.what) {
				case SendToServer.SUCCESS:
					Toast.makeText(getApplicationContext(), "Login success", Toast.LENGTH_SHORT).show();
					Intent it = new Intent();
					it.setClass(Login.this,mainActivity.class);
					it.putExtra("Account", editAccount.getText().toString());
					File f = new File(savePath);
					f.mkdirs();	//避免資料夾不存在造成無法寫入
					String LoginDataPath = savePath+"/login.dat";
					FileManager loginData = new FileManager(LoginDataPath);
					loginData.writeLine(editAccount.getText().toString());	//寫入帳號
					loginData.writeLine(editPassword.getText().toString());	//寫入密碼
					startActivity(it);
					finish();
					break;
				case SendToServer.FAIL:
					Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
					break;
				case SendToServer.SERVER_ERROR:
					Toast.makeText(getApplicationContext(), "Server not response", Toast.LENGTH_SHORT).show();
					break;
				}
				super.handleMessage(msg);
			}
		};
		btnLogin.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String msg = "";
				msg += "Login\n" + editAccount.getText() + "\n"
						+ editPassword.getText();
				new SendToServer(address, port1, msg, MessageHandler, SendToServer.LOGIN).start();
			}
		});
		
		btnSignup.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent();
				it.setClass(Login.this, Signup.class);
				startActivity(it);
			}
		});
		
	}

	
	
}


