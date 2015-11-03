package com.example.project_ver1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UserInfo extends Activity {

	Handler MessageHandler;

	Button btnUserinfoEdit, btnUserinfoBack;
	TextView txtUserinfo;
	ImageView imgUserinfoPhoto;
	String photoPath = ""; // 另外接收photopath,傳到server來取得照片
	byte[] mPhoto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userinfo);

		txtUserinfo = (TextView) this.findViewById(R.id.txtUserinfo);
		imgUserinfoPhoto = (ImageView) this.findViewById(R.id.imgUIMPhoto);
		btnUserinfoEdit = (Button) this.findViewById(R.id.btnUserinfoEdit);
		btnUserinfoBack = (Button) this.findViewById(R.id.btnUIMComfirm);

		btnUserinfoBack.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		btnUserinfoEdit.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent();
				it.setClass(UserInfo.this, UserinfoManager.class);
				startActivity(it);
			}
		});

		MessageHandler = new Handler() {

			public void handleMessage(Message msg) {
				switch (msg.what) {
				case SendToServer.SUCCESS_GET_USERINFO:
					// Toast.makeText(getApplicationContext(), "",
					// Toast.LENGTH_SHORT).show();
					txtUserinfo.setText(msg.obj.toString());
					photoPath = msg.obj.toString().split("\n")[2]; // 2為photopath的位置
					String msg_getphoto = "GetPhoto\n" + photoPath;
					new SendToServer(Login.address, Login.port1, msg_getphoto,
							MessageHandler, SendToServer.GET_PHOTO).start(); // 傳到server並抓取圖片
					break;
				case SendToServer.SUCCESS_GET_PHOTO:
					mPhoto = (byte[]) msg.obj;
					Bitmap bm = BitmapFactory.decodeByteArray(mPhoto, 0,
							mPhoto.length, null);
					imgUserinfoPhoto.setImageBitmap(bm);
					break;
				case SendToServer.FAIL:
					Toast.makeText(getApplicationContext(), "Get Info Fail",
							Toast.LENGTH_SHORT).show();
					break;

				case SendToServer.SERVER_ERROR:
					Toast.makeText(getApplicationContext(),
							"Server not response", Toast.LENGTH_SHORT).show();
					break;
				}
				super.handleMessage(msg);
			}
		};

	}

	public void getUserInfo(String account) {
		String msg = "GetUserInfo" + "\n" + account;
		new SendToServer(Login.address, 3838, msg, MessageHandler,
				SendToServer.GET_USER_INFO).start();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
		getUserInfo(mainActivity.Account);
		super.onResume();
	}
	
	
}
