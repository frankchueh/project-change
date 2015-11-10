package com.example.project_ver1;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.location.Location;
import android.app.Service;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;
import android.content.Intent;

public class GetPosition extends Service implements ConnectionCallbacks,
	OnConnectionFailedListener,
	LocationListener {
	
	// Google API 用戶端物件
	private GoogleApiClient mGoogleApiClient;
	// location request 物件
	private LocationRequest locationRequest;
	// 上傳座標字串
	private String uploadString;
	// Server address
	public String address = "140.118.125.229";
	// 連接 Port
	public int port = 3838;
	// 使用者 ID
    String user_Account = "";
	// message handler
	private Handler MessageHandler; 
	
	//public static final String START_UPLOAD = "com.example.project_ver1.action.upload";
	//public static final String QUIT_UPLOAD = "com.example.project_ver1.action.action.quit";
	
	// 建立 Google API 用戶端物件
		private synchronized void configGoogleApiClient() {
			mGoogleApiClient = new GoogleApiClient.Builder(this).
								   addConnectionCallbacks(this).
								   addOnConnectionFailedListener(this).
								   addApi(LocationServices.API).
								   build();
		}
		
		// 建立 location 請求物件
		private void configLocationRequest() {
			locationRequest = LocationRequest.create().
						      setInterval(60000).
						      setFastestInterval(1000).
						      setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		}
	@Override
	public IBinder onBind(Intent arg0) {
		
		return null;
	}
	
	@Override
	public void onCreate() {
		
		super.onCreate();
		// 取得 Google API 用戶端物件
		configGoogleApiClient();
		// 取得 Location request 物件
		configLocationRequest();
		
		MessageHandler = new Handler() {

			public void handleMessage(Message msg) {
				//switch (msg.what) {
				//case 0:
				//}
				super.handleMessage(msg);
			}
		};
		
	}
	
	@Override
	public void onDestroy() {
		
		super.onDestroy();
		if(!mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}
	
	@Override 
	public int onStartCommand(Intent intent , int flags , int startId) {
		
		String action = intent.getAction();
		mGoogleApiClient.connect();
		user_Account = intent.getExtras().getString("Account");

		return super.onStartCommand(intent, flags, startId);
	}
	

	// implementation of LocationListner
		
	@Override
	public void onLocationChanged(Location updateLocation) {
        
		uploadString = "";
		
		if(updateLocation != null) {
			uploadString += "updateLocate\n" + 
			 		  user_Account + "\n" + 
			 		  updateLocation.getLatitude() + "\n" + 
			 		  updateLocation.getLongitude();
			new SendToServer(address,port,uploadString,MessageHandler,SendToServer.UPLOAD_LOCATE).start();
		}
		
	}
		
	// implementation of ConnectionCallback 
		
	@Override
	public void onConnected(Bundle connectBundle) {
			
		// 已連線到 Google Service
		// 啟動位置更新服務
		// 當位置座標更新時，App 會自動呼叫 LocationListner.onLocationChanged
		LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, 
																 locationRequest, 
																 this);
	}
		
	@Override
	public void onConnectionSuspended(int cause) {
	        // Do nothing
	}
		
    // implementation of onConnectionFailerListener
		
    @Override
	public void onConnectionFailed(ConnectionResult result) {
		     
    	 // Google Services 連線失敗
		 int errorCode = result.getErrorCode();   // 取得連線失敗資訊
		 
		 // 裝置沒有 Google Play Service
		 if(errorCode == result.SERVICE_MISSING) {
			 Toast.makeText(this, "Service missing", Toast.LENGTH_LONG).show();
		 }	
	}
}
