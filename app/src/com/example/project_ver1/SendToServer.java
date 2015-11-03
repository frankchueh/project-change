package com.example.project_ver1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.os.Handler;
import android.os.Message;

public class SendToServer extends Thread {

	public static final int GET_USER_INFO = 1001, GET_PHOTO = 1002,
			UPDATE_USER_INFO = 1003, LOGIN = 1004, SIGNUP = 1005,
			UPDATE_USER_PHOTO = 1006, LIST_CHAT_ROOM = 1007,
			DOWNLOAD_MESSAGE = 1008, UPDATE_MESSAGE = 1009,
			GET_CHAT_ROOM = 1010,
			SUCCESS = 2001, FAIL = 2002, SERVER_ERROR = 2003,
			SUCCESS_GET_PHOTO = 2004, SUCCESS_GET_USERINFO = 2005,
			SUCCESS_GET_CHAT_LIST = 2006;

	String address; // Server��address
	int Port; // server��ť��port
	Socket client;
	InetSocketAddress isa;
	Object msg;
	PrintWriter pw;
	BufferedReader br;
	Handler MessageHandler;
	int command;
	Message return_msg = new Message();

	SendToServer(String address, int Port, Object message,
			Handler MessageHandler, int command) {
		this.address = address;
		this.Port = Port;
		this.msg = message;
		this.MessageHandler = MessageHandler;
		this.command = command;
		// �n�^�Ǫ�message
	}

	public void run() {
		try {
			isa = new InetSocketAddress(address, Port);
			client = new Socket();
			client.connect(isa, 10000);

			pw = new PrintWriter(new OutputStreamWriter(
					client.getOutputStream(), "utf-8"), true);
			br = new BufferedReader(new InputStreamReader(
					client.getInputStream()));

			

			switch (command) // �ھ�command�Ӱ��B�z
			{

			/*
			 * ���o�ϥΪ�,�ݭn�ǤJ"GetUserInfo" + $(UserAccount), �p�G���o��Account���� �^��
			 * UserAccount, Username, PhotoPath, age, Birthday, sex, phone,
			 * email �p�G�S�����ܦ^��fail
			 */
			case GET_USER_INFO:
				pw.println(msg);
				if (br.readLine().equals("success")) {
					String data = "";
					String line;
					while ((line = br.readLine()) != null) {
						// Ū���Ҧ��^�Ǫ���T
						data += line + "\n";
					}
					return_msg.obj = data;
					return_msg.what = SUCCESS_GET_USERINFO;

				} else {
					return_msg.what = FAIL;
				}
				break;

			/*
			 * ���o�Ӥ�,�ݭn�ǤJ"GetPhoto" + $(PhotoPath), �p�GServer���Ӥ����ܦ^��success+Photo
			 * byteArray �p�G�S�����ܦ^��fail
			 */
			case GET_PHOTO:
				pw.println(msg);

				if (br.readLine().equals("success")) {
					ObjectInputStream ois = new ObjectInputStream(
							client.getInputStream());
					return_msg.what = SUCCESS_GET_PHOTO;
					return_msg.obj = (byte[]) ois.readObject();
				} else
					return_msg.what = FAIL;
				break;
			/*
			 * ���ϥΪ̸�T �ݭn�ǤJ"UpdateUserInfo" + $(Account) + $(username) + $(age)
			 * + $(birthday) + $(sex) + $(phone) + $(email)
			 * �p�G���\���ܦ^��success,���Ѫ��ܦ^��fail
			 */
			case UPDATE_USER_INFO:
				pw.println(msg);
				if (br.readLine().equals("success")) {
					return_msg.what = SUCCESS;
				} else
					return_msg.what = FAIL;
				break;

			/*
			 * �n�J�b��,�ݭn�ǤJ"Login" + $(UserAccount) +�@$(UserPassword)
			 * �p�G���\�n�J�^��"success",�p�G���Ѧ^��"fail"
			 */
			case LOGIN:
				pw.println(msg);
				if (br.readLine().equals("success")) {
					return_msg.what = SUCCESS;
				} else
					return_msg.what = FAIL;
				break;

			case UPDATE_USER_PHOTO:
				pw.println("UpdateUserPhoto");
				pw.println(mainActivity.Account);
				if (br.readLine().equals("OK")) {
					ObjectOutputStream oos = new ObjectOutputStream(
							client.getOutputStream()); // ��Ӥ��g�J
					oos.writeObject(msg);
					oos.flush();

					if (br.readLine().equals("success")) {
						return_msg.what = SUCCESS;
					} else
						return_msg.what = FAIL;

					oos.close();
				} else
					return_msg.what = FAIL;
				break;

			case LIST_CHAT_ROOM:

				pw.println(msg.toString()); // ListChatRoom +\n+ UserAccount
				if (br.readLine().equals("success")) {
					String B = br.readLine();
					String S = br.readLine();
					return_msg.what = SUCCESS_GET_CHAT_LIST;
					return_msg.obj = B+"\n"+S;
				}
				else
					return_msg.what = FAIL;
				
				break;
			
			case DOWNLOAD_MESSAGE:
				pw.println(msg.toString());
				if(br.readLine().equals("success"))
				{
					String data="", line;
					
					while((line=br.readLine())!=null)
					{
						data+=line;
					}
					return_msg.what = DOWNLOAD_MESSAGE;
					return_msg.obj = data;
				}
				else
				{
					return_msg.what = FAIL;
					return_msg.obj = "Download message fail";
				}
				break;
			
			case UPDATE_MESSAGE:
				
				break;
			
			case GET_CHAT_ROOM:
				pw.println(msg.toString());
				if(br.readLine().equals("success"))
				{
					int chatID = Integer.parseInt(br.readLine());
					return_msg.what = GET_CHAT_ROOM;
					return_msg.obj = chatID;
				}
				else
				{	
					return_msg.what = FAIL;
					return_msg.obj = "get chat room fail";
				}
				break;	
				
			
			}

			pw.close(); // ����command��������������ʧ@
			client.close();
			client = null;

			MessageHandler.sendMessage(return_msg);
			System.out.println("Pass over!");

		} catch (java.io.IOException e) {

			return_msg.what = SERVER_ERROR;
			MessageHandler.sendMessage(return_msg);
			System.out.println("socket error");
			System.out.println("IOException :" + e.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
