package com.example.project_ver1;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
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
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class UserinfoManager extends Activity {

	EditText editUsername, editAge, editPhone, editEmail;
	DatePicker dateBirth;
	RadioGroup RadioGroupSex;
	RadioButton radioMale, radioFemale;
	ImageView imgUIMPhoto;
	Button btnUIMComfirm, btnUIMBack;

	Handler MessageHandler;

	
	
	String username="", age="", phone="",
			email="", birth="", sex="", photoPath="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userinfomanager);

		editUsername = (EditText) this.findViewById(R.id.editUsername);
		editAge = (EditText) this.findViewById(R.id.editAge);
		editPhone = (EditText) this.findViewById(R.id.editPhone);
		editEmail = (EditText) this.findViewById(R.id.editEmail);
		dateBirth = (DatePicker) this.findViewById(R.id.dateBirth);
		RadioGroupSex = (RadioGroup) this.findViewById(R.id.RadioGroupSex);
		radioMale = (RadioButton) this.findViewById(R.id.radioMale);
		radioFemale = (RadioButton) this.findViewById(R.id.radioFemale);
		imgUIMPhoto = (ImageView) this.findViewById(R.id.imgUIMPhoto);
		btnUIMComfirm = (Button) this.findViewById(R.id.btnUIMComfirm);
		btnUIMBack = (Button) this.findViewById(R.id.btnUIMBack);

		imgUIMPhoto.setOnClickListener(new ImageView.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final CharSequence[] items = { "從相册選擇照片", "使用相機拍攝照片" };
				AlertDialog dlg = new AlertDialog.Builder(UserinfoManager.this)
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
				
			}});
		
		btnUIMComfirm.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				username = editUsername.getText().toString();
				age = editAge.getText().toString();
				phone = editPhone.getText().toString();
				email = editEmail.getText().toString();
				if(birth.equals("null"))
					birth = "1994-01-01";
				if(sex.equals("null"))
					sex = "M";
				if(age.equals(""))
					age="0";
				if(phone.equals(""))
					age="0";
				if(email.equals(""))
					age="0";
				if(username.equals(""))
				{
					Toast.makeText(getApplicationContext(),"username can't be empty", Toast.LENGTH_SHORT).show();
					return;
				}	

				
				String msg = "UpdateUserInfo\n" + mainActivity.Account + "\n"
						+ username + "\n" + age + "\n" + birth + "\n" + sex
						+ "\n" + phone + "\n" + email;
				new SendToServer(Login.address, 3838, msg, MessageHandler,
						SendToServer.UPDATE_USER_INFO).start();
				
				if(mPhoto!=null)
				{
					new SendToServer(Login.address, 3838, mPhoto, MessageHandler,
							SendToServer.UPDATE_USER_PHOTO).start();
				}

			}
		});

		btnUIMBack.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		MessageHandler = new Handler() {

			public void handleMessage(Message msg) {
				switch (msg.what) {
				case SendToServer.SUCCESS_GET_USERINFO:
					
					String[] info = msg.obj.toString().split("\n");

					username = info[1];
					if (!username.equals("null"))
						editUsername.setText(username);

					age = info[3];
					if (!age.equals("null"))
						editAge.setText(age);

					phone = info[6];
					if (!phone.equals("null"))
						editPhone.setText(phone);

					email = info[7];
					if (!email.equals("null"))
						editEmail.setText(email);

					birth = info[4];
					int year = 1994;
					int month = 1;
					int day = 1;
					if (!birth.equals("null")) {
						year = Integer.parseInt(birth.split("-")[0]);
						month = Integer.parseInt(birth.split("-")[1]);
						day = Integer.parseInt(birth.split("-")[2]);

					}

					dateBirth.init(year, month - 1, day,
							new DatePicker.OnDateChangedListener() {
								@Override
								public void onDateChanged(DatePicker view,
										int year, int monthOfYear,
										int dayOfMonth) {
									// TODO Auto-generated method stub
									birth = "" + year + "-" + (monthOfYear + 1)
											+ "-" + dayOfMonth;
								}
							});

					sex = info[5];
					if (sex.equals("F")) {
						radioMale.setChecked(true);
						radioFemale.setChecked(false);
					} else {
						radioMale.setChecked(true);
						radioFemale.setChecked(false);
					}

					photoPath = info[2];
					if (!photoPath.equals("null")) {
						String msg_getphoto = "GetPhoto\n" + photoPath;
						new SendToServer(Login.address, 3838, msg_getphoto,
								MessageHandler, SendToServer.GET_PHOTO).start();
					}
					break;
					
				case SendToServer.SUCCESS_GET_PHOTO:
					mPhoto = (byte[]) msg.obj;
					Bitmap bm = BitmapFactory.decodeByteArray(mPhoto, 0,
							mPhoto.length, null);
					imgUIMPhoto.setImageBitmap(bm);
					break;
					
				case SendToServer.SERVER_ERROR:
					Toast.makeText(getApplicationContext(),
							"Server not response", Toast.LENGTH_SHORT).show();
					break;
					
				case SendToServer.SUCCESS:
					Toast.makeText(getApplicationContext(), "Update success",
							Toast.LENGTH_SHORT).show();
					finish();
					break;

				case SendToServer.FAIL:
					Toast.makeText(getApplicationContext(), "Update fail",
							Toast.LENGTH_SHORT).show();
					break;
					
				default:
					Toast.makeText(getApplicationContext(), "Commit Error",
							Toast.LENGTH_SHORT).show();
					break;
				}

				super.handleMessage(msg);
			}
		};

		RadioGroupSex
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO Auto-generated method stub
						switch (checkedId) {
						case R.id.radioMale:
							sex = "M";
							break;
						case R.id.radioFemale:
							sex = "F";
							break;
						}
					}
				});

		String msg = "GetUserInfo" + "\n" + mainActivity.Account;
		new SendToServer(Login.address, 3838, msg, MessageHandler,
				SendToServer.GET_USER_INFO).start();

	}
	
	
	//照片function
	
	private Bitmap myBitmap;
	private byte[] mPhoto=null;
	private File file_photo;
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
				CropPhoto(Uri.fromFile(file_photo));
			} catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}
		}

		else if (requestCode == PICTURE_AFTER_CROP) {
			try {
				// 獲得圖片的uri
				Uri orginalUri = Uri.fromFile(file_photo);
				System.out.print(orginalUri);
				// 將圖片内容解析成字節數組
				mPhoto = readStream(contentResolver.openInputStream(Uri
						.parse(orginalUri.toString())));
				// 將字節數組轉換為ImageView可調用的Bitmap對象
				myBitmap = BitmapFactory.decodeByteArray(mPhoto, 0,
						mPhoto.length, null);
				//壓縮照片
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				myBitmap.compress(CompressFormat.JPEG, 80, bos);
				mPhoto = bos.toByteArray();
				//把得到的圖片绑定在控件上顯示
				imgUIMPhoto.setImageBitmap(myBitmap);
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
		file_photo = new File(TakePhotoDir, getPhotoFileName());
		getImageByCamera
				.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file_photo));
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
		file_photo = new File(CropPhotoDir,localTempImgFileName);
		Uri uri2 = Uri.fromFile(file_photo);
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

	
}
