package com.example.project_ver1;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.util.ArrayList;

public class ProductManage extends Activity {
	
	private ListView productView;
	ArrayList<Product> product_set = new ArrayList<Product>();   // �ϥΪ̩Ҧ��ӫ~����
	int [] pid_set;    // �ϥΪ̩Ҧ��ӫ~ID
	String [] pName_set;	// �ϥΪ̩Ҧ��ӫ~�W��
	int [] pPrice_set;   // �ϥΪ̩Ҧ��ӫ~����
	String[] pPhotoPath_set;   // �ϥΪ̩Ҧ��ӫ~�Ӥ����|
	String[] pInfoPath_set;   // �ϥΪ̩Ҧ��ӫ~��T���|
	
	byte[][] pPhoto_set;   // �ϥΪ̩Ҧ��ӫ~�Ӥ�
	
	String p_msg = "";  // �ǰe message
	int p_num = 0;  // �`�ӫ~�ƶq
	int p_allinfo_count = 0;   // �Ψ��x�s�ӫ~�Ҧ���T�� index
	int p_photo_count = 0;   // �Ψ��x�s�ӫ~�Ӥ��� index
	public static  String address = "140.118.125.229";
	public static int port = 3838;
	Handler MessageHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_manage);
		
		productView = (ListView)findViewById(R.id.productlistview);
		
		p_msg = "getUserProduct" + "\n" + mainActivity.Account;
		new SendToServer(address,port,p_msg,MessageHandler,SendToServer.GET_USER_PRODUCT);
				
		MessageHandler = new Handler() {

			public void handleMessage(Message msg) {
				switch (msg.what) {
				case SendToServer.SUCCESS_GET_PID:
					String [] temp_pid_set = (String[]) msg.obj;
					p_num = temp_pid_set.length;
					getAllProductsInfo(temp_pid_set);
					
					//Toast.makeText(getApplicationContext(), "Product ID download success", Toast.LENGTH_SHORT).show();
					break;
				case SendToServer.SUCCESS_GET_PRODUCTINFO:
					String [] temp_pinfo_set = (String[]) msg.obj;
					getProductPhoto(temp_pinfo_set);
					//Toast.makeText(getApplicationContext(), "Product Info download success", Toast.LENGTH_SHORT).show();
					break;
				case SendToServer.SUCCESS_GET_PHOTO:
					pPhoto_set[p_photo_count++] = (byte[]) msg.obj;  // ���o�Ҧ��Ӥ�
					if(p_photo_count == p_num) {   // ��ƥ����ǿ駹���� -> �`�x�s
						saveAllProductMessage();
					}
					//Toast.makeText(getApplicationContext(), "Product Photo download success", Toast.LENGTH_SHORT).show();
					break;
				case SendToServer.FAIL:
					Toast.makeText(getApplicationContext(),"Product download failed", Toast.LENGTH_SHORT).show();
				}
				super.handleMessage(msg);
			}
		};
		
	}
	
	@Override
	protected void onStart() {
	}

	protected void getAllProductsInfo(String [] str) {
		
		for(int i = 0; i < str.length; i++) {
			pid_set[i] = Integer.parseInt(str[i]);   // �N�Ҧ� pid �s�_��
			p_msg = "getProductInfo" + str[i];
			new SendToServer(address,port,p_msg,MessageHandler,SendToServer.GET_PRODUCT_INFO);
		}
	}
	
	protected void getProductPhoto(String [] str) {
		
		pName_set[p_allinfo_count] = str[1];     // �N�S�w pid ���Ҧ���T�s�_��
		pPrice_set[p_allinfo_count] = Integer.parseInt(str[2]);
		pPhotoPath_set[p_allinfo_count] = str[3];
		pInfoPath_set[p_allinfo_count] = str[4];
		p_msg = "GetPhoto" + pPhotoPath_set[p_allinfo_count++];  // �h���o��Ϥ�
		new SendToServer(address,port,p_msg,MessageHandler,SendToServer.GET_PHOTO);
	}
	
	protected void saveAllProductMessage() {
		
		for(int i = 0; i < p_num; i++) {
			
			product_set.add(new Product(pid_set[i],pName_set[i],pPrice_set[i],pInfoPath_set[i],pPhoto_set[i]));
		}
	}
	
	
}

class ProductAdapter extends BaseAdapter {

	LayoutInflater myInflater;

	public ProductAdapter(ProductManage listViewActivity) {
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
		convertView = myInflater.inflate(R.layout.productitem, null);
		ImageView imgProduct = (ImageView) convertView
				.findViewById(R.id.productPhoto);
		TextView txtProductName = (TextView) convertView
				.findViewById(R.id.productName);
		TextView txtProductPrice = (TextView) convertView
				.findViewById(R.id.productPrice);

		txtProductPrice.setText(chatID_current[position]);

		return convertView;
	}
}
