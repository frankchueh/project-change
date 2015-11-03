package com.example.project_ver1;


import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatRoomList extends Activity {

	ListView listChatroom;
	Button btnBuyer, btnSeller;
	String[] chatID_S = {};
	String[] chatID_B = {};
	String[] chatID_current = {};

	Handler MessageHandler;

	MyAdapter appAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chatroomlist);

		listChatroom = (ListView) this.findViewById(R.id.listChatroom);
		btnBuyer = (Button) this.findViewById(R.id.btnBuyer);
		btnSeller = (Button) this.findViewById(R.id.btnSeller);

		btnBuyer.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				chatID_current = chatID_B;
				if (appAdapter != null)
					appAdapter.notifyDataSetChanged();
			}
		});

		btnSeller.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				chatID_current = chatID_S;
				if (appAdapter != null)
					appAdapter.notifyDataSetChanged();
			}
		});

		MessageHandler = new Handler() {

			public void handleMessage(Message msg) {
				switch (msg.what) {
				case SendToServer.SUCCESS_GET_CHAT_LIST:
					Toast.makeText(getApplicationContext(), msg.obj.toString(),
							Toast.LENGTH_SHORT).show();
					chatID_B = msg.obj.toString().split("\n")[0].split(",");
					chatID_S = msg.obj.toString().split("\n")[1].split(",");
					break;
				case SendToServer.FAIL:
					Toast.makeText(getApplicationContext(), "Get list fail",
							Toast.LENGTH_SHORT).show();
					break;
				case SendToServer.SERVER_ERROR:
					Toast.makeText(getApplicationContext(), "Server Error",
							Toast.LENGTH_SHORT).show();
					break;
				}
				super.handleMessage(msg);
			}
		};

		appAdapter = new MyAdapter(this);
		listChatroom.setAdapter(appAdapter);
		listChatroom.setOnItemClickListener(new ListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (!chatID_current[position].equals("")) {
					Intent it = new Intent();
					it.setClass(ChatRoomList.this, ChatRoom.class);
					it.putExtra("chatID", Integer.parseInt(chatID_current[position]));
					startActivity(it);
				}
			}
		});

		String msg = "ListChatRoom\n" + mainActivity.Account;
		new SendToServer(Login.address, 3838, msg, MessageHandler,
				SendToServer.LIST_CHAT_ROOM).start();

	}

	class MyAdapter extends BaseAdapter {

		LayoutInflater myInflater;

		public MyAdapter(ChatRoomList listViewActivity) {
			myInflater = LayoutInflater.from(listViewActivity);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return chatID_current.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return chatID_current[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			convertView = myInflater.inflate(R.layout.chatlistview, null);
			ImageView imgChatList = (ImageView) convertView
					.findViewById(R.id.imgChatList);
			TextView txtChatList1 = (TextView) convertView
					.findViewById(R.id.txtChatList1);

			txtChatList1.setText(chatID_current[position]);

			return convertView;
		}

	}

}
