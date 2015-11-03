package com.example.project_ver1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;


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

public class Welcome extends Activity {

	private Handler MessageHandler;
	private String savePath = Environment.getExternalStorageDirectory()
			.getPath() + "/PolarTrade";
	FileManager login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);

		final File f = new File(savePath, "Login.dat");

		
		Thread showLogo = new Thread() {
			public void run() {

				try {
					sleep(3000);	//顯示三秒Logo
				} catch (Exception e) {
					e.printStackTrace();
				} finally {

					if (!f.exists()) {

						Intent it = new Intent();
						it.setClass(Welcome.this, Login.class);
						startActivity(it);
						
					} else {
						
						login = new FileManager(f.getPath());
						String[] temp = login.readAllLine();
						String msg = "Login\n" + temp[0] + "\n" + temp[1];
						new SendToServer(Login.address, Login.port1, msg,
								MessageHandler, SendToServer.LOGIN).start();
					}
					
				}
			}
		};
		showLogo.start();
		
		

		MessageHandler = new Handler() {

			public void handleMessage(Message msg) {
				switch (msg.what) {
				case SendToServer.SUCCESS:
					Toast.makeText(getApplicationContext(), "Login success",
							Toast.LENGTH_SHORT).show();
					Intent it = new Intent();
					it.setClass(Welcome.this, mainActivity.class);

					String[] temp = login.readAllLine();
					it.putExtra("Account", temp[0]);
					startActivity(it);
					finish();
					break;
				case SendToServer.FAIL:
					Toast.makeText(getApplicationContext(), "Login failed",
							Toast.LENGTH_SHORT).show();
					 it = new Intent();
					it.setClass(Welcome.this, Login.class);
					startActivity(it);
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



}
