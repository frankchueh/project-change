package com.example.project_ver1;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChatRoom extends Activity {
	
	Button btnChatSend;
	EditText editChatMsg;
	TextView txtChatData;
	int chatID=-1;
	Handler MessageHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.chatroom);
		Intent call_it = getIntent();
		
		btnChatSend = (Button) this.findViewById(R.id.btnChatSend);
		editChatMsg = (EditText) this.findViewById(R.id.editChatMsg);
		txtChatData = (TextView) this.findViewById(R.id.txtChatData);
		
		
		
		MessageHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch(msg.what)
				{
				case SendToServer.GET_CHAT_ROOM:
					chatID = Integer.parseInt(msg.obj.toString());
					new SendToServer(Login.address, Login.port1, "DownloadMessage\n"+chatID
							, MessageHandler, SendToServer.DOWNLOAD_MESSAGE);
					break;
				case SendToServer.DOWNLOAD_MESSAGE:
					txtChatData.setText(msg.obj.toString());
					break;
				case SendToServer.UPDATE_MESSAGE:
					
					break;
				case SendToServer.SERVER_ERROR:
					Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_SHORT).show();
					break;
				case SendToServer.FAIL:
					Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
					break;
				}
			}
		};
		
		//先抓看有沒有chatID
		chatID = call_it.getIntExtra("chatID",-1);
		//如果沒有chatID
		if(chatID==-1)
		{
			int PID = call_it.getIntExtra("produceID", -1);
			int BID = call_it.getIntExtra("buyerID", -1);
			int SID = call_it.getIntExtra("sellerID", -1);
			String msg = "GetChatRoom\n" + PID + "\n" + SID + "\n" + BID;
			new SendToServer(Login.address, Login.port1, msg, MessageHandler, SendToServer.GET_CHAT_ROOM);
		}
		
		
	}
	
}
