package com.example.project_ver1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class mainActivity extends Activity{
	
	Button btnUserInfo, btnChatroomList;
	public static String Account;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		btnUserInfo = (Button) this.findViewById(R.id.btnUserInfo);
		btnChatroomList = (Button) this.findViewById(R.id.btnChatroomList);
		Intent con = getIntent();
		Account = con.getStringExtra("Account");
		
		btnUserInfo.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent();
				it.setClass(mainActivity.this, UserInfo.class);
				startActivity(it);
			}});
		
		btnChatroomList.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent();
				it.setClass(mainActivity.this, ChatRoomList.class);
				startActivity(it);
			}});
		
	}
	

}
