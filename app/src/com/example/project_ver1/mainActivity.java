package com.example.project_ver1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class mainActivity extends Activity{
	
	Button btnUserInfo, btnChatroomList , btnProductUpload , btnStartService , btnQuitService,
		   btnProductManage;
	public static String Account;    // ¨Ï¥ÎªÌ±b¤á
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		btnUserInfo = (Button) this.findViewById(R.id.btnUserInfo);
		btnChatroomList = (Button) this.findViewById(R.id.btnChatroomList);
		btnProductUpload = (Button) this.findViewById(R.id.btnProductUpload);
		btnStartService = (Button) this.findViewById(R.id.btnStartService);
		btnQuitService = (Button) this.findViewById(R.id.btnQuitService);
		btnProductManage = (Button) this.findViewById(R.id.btnProductManage);
		//Intent con = getIntent();
		//Account = con.getStringExtra("Account");
		Account = "steven0824";
		
		// Start location upload service
		/*Intent intent = new Intent(GetPosition.START_UPLOAD);
		Bundle bundle = new Bundle();
		bundle.putString("user Account", Account);
		intent.putExtras(bundle);
		startService(intent);*/
		
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
		
		btnProductUpload.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent();
				it.setClass(mainActivity.this, OnShelf.class);
				startActivity(it);
			}});
		
		btnStartService.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(GetPosition.START_UPLOAD);
				Bundle bundle = new Bundle();
				bundle.putString("Account",Account);
				intent.putExtras(bundle);
				startService(intent);
			}});
		
		btnQuitService.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(GetPosition.QUIT_UPLOAD);
				startService(intent);
			}});
		
		btnProductManage.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent();
				it.setClass(mainActivity.this, ProductManage.class);
			}});
	    
	}
	

}
