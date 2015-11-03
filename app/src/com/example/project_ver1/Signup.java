package com.example.project_ver1;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class Signup extends Activity {
	Button btnSignChange, btnSignDelete, btnSignUp;
	EditText editSignAccount, editSignPassword, editSignPasswordConfirm,
			editSignUsername;
	ImageView imgSignPhoto;
	Handler MessageHandler;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);
		btnSignChange = (Button) this.findViewById(R.id.btnSignChange);
		btnSignDelete = (Button) this.findViewById(R.id.btnSignDelete);
		btnSignUp = (Button) this.findViewById(R.id.btnSignUp);
		editSignAccount = (EditText) this.findViewById(R.id.editSignAccount);
		editSignPassword = (EditText) this.findViewById(R.id.editSignPassword);
		editSignPasswordConfirm = (EditText) this
				.findViewById(R.id.editSignPasswordConfirm);
		editSignUsername = (EditText) this.findViewById(R.id.editSignUsername);
		imgSignPhoto = (ImageView) this.findViewById(R.id.imgSignPhoto);
		btnSignChange.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final CharSequence[] items = { "從相册選擇照片", "使用相機拍攝照片" };

				AlertDialog dlg = new AlertDialog.Builder(Signup.this)
						.setTitle("選擇照片")
						.setItems(items, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								// 這裡item是根據選擇的方式，
								// 在items數據裡面定義了兩種方式，拍照的下標為1所以就調用拍照方法
								if (which == 1) {
									TakePicture(); // 用相機拍照
								} else {
									SelectPhoto(); // 從相簿中選擇照片
								}
							}
						})
						.create();
				
				
				dlg.show();
			}
		});

		btnSignDelete.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				imgSignPhoto.setImageResource(R.drawable.default_photo);
				mContent=null;
			}
		});

		btnSignUp.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String Account, Password, PasswordConfirm, Username;
				if (!editSignAccount.getText().toString().equals("")) {		//檢查Account
					Account = editSignAccount.getText().toString();
					if (!CheckInput(Account)) {
						Toast.makeText(getApplicationContext(),
								"Account請使用半形英文和數字組成", Toast.LENGTH_LONG)
								.show();
						return;
					} else if (!editSignPassword.getText().toString()		//檢查password
							.equals("")) {
						Password = editSignPassword.getText().toString();
						if (!CheckInput(Password)) {
							Toast.makeText(getApplicationContext(),
									"Password請使用半形英文和數字組成", Toast.LENGTH_LONG)
									.show();
							return;
						} else if (!editSignPasswordConfirm.getText()		//檢查password Confirm
								.toString().equals("")) {
							PasswordConfirm = editSignPasswordConfirm.getText()
									.toString();
							if (!PasswordConfirm.equals(Password)) {
								Toast.makeText(getApplicationContext(),
										"兩次密碼不相符", Toast.LENGTH_LONG).show();
								return;
							} else if (!editSignUsername.getText().toString()	//檢查username
									.equals("")) {
								Username = editSignUsername.getText()
										.toString();
							} else {
								Toast.makeText(getApplicationContext(),
										"請輸入Username", Toast.LENGTH_LONG)
										.show();
								return;
							}
						} else {
							Toast.makeText(getApplicationContext(),
									"請輸入PasswordConfirm", Toast.LENGTH_LONG)
									.show();
							return;
						}
					} else {
						Toast.makeText(getApplicationContext(), "請輸入Password",
								Toast.LENGTH_LONG).show();
						return;
					}
				} else {
					Toast.makeText(getApplicationContext(), "請輸入Account",
							Toast.LENGTH_LONG).show();
					return;
				}
				//都檢查過後把資料傳到Server
				String msg = "";
				msg += "SignUp\n" + Account + "\n"
						+ Password +"\n"
						+ Username;
				new SendThread(Login.address, 3838, msg).start();
				
			}
		});
		
		 MessageHandler = new Handler() {

			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					Toast.makeText(getApplicationContext(), "Sign up success", Toast.LENGTH_SHORT).show();
					finish();
					break;
				case 1:
					Toast.makeText(getApplicationContext(), "帳戶已經存在", Toast.LENGTH_SHORT).show();
					break;
				case 2:
					Toast.makeText(getApplicationContext(), "Server not response", Toast.LENGTH_SHORT).show();
					break;
				}
				super.handleMessage(msg);
			}
		};
		
	}
	
	
	private Bitmap myBitmap;
	private byte[] mContent=null;
	private File mPhoto;
	private File CropPhotoDir = new File(
			Environment.getExternalStorageDirectory() + "/PolarTrade/CropPhoto");
	private File TakePhotoDir = new File(
			Environment.getExternalStorageDirectory() + "/PolarTrade/TakePhoto");
	private final int PICTURE_FROM_CAMERA = 1000;
	private final int PICTURE_FROM_ALBUM = 1001;
	private final int PICTURE_AFTER_CROP = 1002;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		ContentResolver contentResolver = getContentResolver();
		/**
		 * 因為兩種方式都用到了startActivityForResult方法，這個方法執行完後都會執行onActivityResult方法，
		 * 所以為了區別到底選擇了那個方式獲取圖片要進行判斷
		 * ，這裡的requestCode跟startActivityForResult裡面第二個參數對應
		 */

		if (requestCode == PICTURE_FROM_ALBUM) {
			try {
				Uri orginalUri = data.getData();
				CropPhoto(orginalUri);
			} catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}
		} else if (requestCode == PICTURE_FROM_CAMERA) {
			try {
				CropPhoto(Uri.fromFile(mPhoto));
			} catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}
		}

		else if (requestCode == PICTURE_AFTER_CROP) {
			try {
				// 獲得圖片的uri
				Uri orginalUri = Uri.fromFile(mPhoto);
				System.out.print(orginalUri);
				// 將圖片内容解析成字節數組
				mContent = readStream(contentResolver.openInputStream(Uri
						.parse(orginalUri.toString())));
				// 將字節數組轉換為ImageView可調用的Bitmap對象
				myBitmap = BitmapFactory.decodeByteArray(mContent, 0,
						mContent.length, null);
				//壓縮照片
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				myBitmap.compress(CompressFormat.JPEG, 80, bos);
				mContent = bos.toByteArray();
				//把得到的圖片绑定在控件上顯示
				imgSignPhoto.setImageBitmap(myBitmap);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 取得拍攝後照片的名稱
	 */
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date) + ".jpg";
	}

	/**
	 * 利用相簿來取得照片
	 */
	public void SelectPhoto() {
		Intent getImage = new Intent(Intent.ACTION_PICK);
		getImage.setType("image/*");
		startActivityForResult(getImage, PICTURE_FROM_ALBUM);
	}

	/**
	 * 利用相機來拍攝照片
	 */
	public void TakePicture() {
		Intent getImageByCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE,
				null);
		getImageByCamera.putExtra("outputFormat", "JPEG");
		TakePhotoDir.mkdirs();
		mPhoto = new File(TakePhotoDir, getPhotoFileName());
		getImageByCamera
				.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhoto));
		startActivityForResult(getImageByCamera, PICTURE_FROM_CAMERA);
	}

	/**
	 * 對照片做裁剪處理
	 */
	public void CropPhoto(Uri photoUri) {
		Intent it = new Intent("com.android.camera.action.CROP");
		it.setDataAndType(photoUri, "image/*");
		it.putExtra("crop", "true");
		it.putExtra("outputFormat", "JPEG");
		it.putExtra("return-data", false);

		String localTempImgFileName = System.currentTimeMillis() + ".jpg";
		CropPhotoDir.mkdirs();
	mPhoto = new File(CropPhotoDir,localTempImgFileName);
		Uri uri2 = Uri.fromFile(mPhoto);
		it.putExtra(MediaStore.EXTRA_OUTPUT, uri2);
		startActivityForResult(it, PICTURE_AFTER_CROP);
	}

	public static byte[] readStream(InputStream in) throws Exception {
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while ((len = in.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		in.close();
		return data;
	}

	public boolean CheckInput(String input) {
		char[] check = input.toCharArray();

		for (int i = 0; i < check.length; i++) {
			if (!(check[i] >= 'a' && check[i] <= 'z' || check[i] >= 'A'
					&& check[i] <= 'Z' || check[i] >= '0' && check[i] <= '9'))
				return false;
		}
		return true;
	}
	
	
	class SendThread extends Thread {
		String address;
		int Port;
		Socket client;
		InetSocketAddress isa;
		String msg;
		PrintWriter pw;
		BufferedReader br;
		SendThread(String address, int Port, String message) {
			this.address = address;
			this.Port = Port;
			this.msg = message;

		}

		public void run() {
			try {
				isa = new InetSocketAddress(address, Port);
				client = new Socket();
				client.connect(isa, 10000);

				pw = new PrintWriter(new OutputStreamWriter(
						client.getOutputStream(), "utf-8"),true);
				br = new BufferedReader(new InputStreamReader(
						client.getInputStream()));
				
				pw.println(msg);
				ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());	//把照片寫入
				oos.writeObject(mContent);
				oos.flush();
		
					String rs;
					if((rs=br.readLine())!=null)
					{
						System.out.println(rs);
					}
					
					
					pw.close();			//要等到read完成才能close,不然會產生錯誤
					oos.close();
					client.close();
					client = null;
				if(rs.equals("success"))
				{
					Message msg = new Message();
					msg.what=0;
					MessageHandler.sendMessage(msg);
				}
				else
				{
					Message msg = new Message();
					msg.what=1;
					MessageHandler.sendMessage(msg);
				}
				

				System.out.println("Pass over!");
			} catch (java.io.IOException e) {
				Message msg = new Message();
				msg.what=2;
				MessageHandler.sendMessage(msg);
				System.out.println("socket error");
				System.out.println("IOException :" + e.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}


		}

	}
	

}
