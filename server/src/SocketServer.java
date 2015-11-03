import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import java.util.*;

public class SocketServer {

	userdb DBuser = new userdb();
    locationDB DBmap = new locationDB();
    productDB DBproduct = new productDB();
    
	public void run() {

		MessageRecevicer mr = new MessageRecevicer();
		mr.start();
	}
	

	public static void main(String args[]) {

		InetAddress ip = null;
		String hostname;
		try {
			ip = InetAddress.getLocalHost();
			hostname = ip.getHostName();
			System.out.println("IP:" + ip + "\nname:" + hostname);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		(new SocketServer()).run();
		

	}

	class MessageRecevicer extends Thread {

		private ServerSocket socket1;
		private Socket conn1;
		private final int Port1 = 3838;

		public void run() {
			try {
				socket1 = new ServerSocket(Port1);

			} catch (Exception e) {
				e.printStackTrace();
			}

			int threadNo;
			for (threadNo = 1;; threadNo++) {
				System.out.println("MessageRecevicer waiting...");
				String conIp = "";
				try {
					conn1 = socket1.accept();
					conIp = conn1.getInetAddress().toString();
					System.out.println("MessageRecevicer: receive " + conIp
							+ " calling....");
					conn1.setSoTimeout(15000);
				} catch (Exception e) {
					e.printStackTrace();
				}

				System.out.println("MessageRecevicer: Thread " + threadNo
						+ "handling");
				MessageReceviceThread newMessageThread = new MessageReceviceThread(
						conn1, threadNo);
				newMessageThread.start();

			}
		}
	}


	class MessageReceviceThread extends Thread {
		private Socket conn;
		int threadNo;
		FileManager fileMgr;

		public MessageReceviceThread(Socket conn, int threadNo) {
			this.conn = conn;
			this.threadNo = threadNo;

			System.out.println("MessageReceviceThread " + threadNo + " handle "
					+ conn.getInetAddress() + "'s calling");
		}

		public void run() {
			try {

				BufferedReader br = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				
				String data = "", line, command;
				command = br.readLine(); // 第一行為指令

				PrintWriter pw = new PrintWriter(new OutputStreamWriter(
						conn.getOutputStream(), "utf-8"), true);
				
				//判斷指令是什麼
				if (command.equals("Login")) {	//登入指令,會回傳是否成功
					/*if (DBuser.Login(br.readLine(), br.readLine())) {
						pw.println("success");
						System.out.println("login success");
					} else {
						pw.println("fail");
						System.out.println("login fail");
					}*/
				} 
				else if (command.equals("SignUp")) {	//建立帳戶指令,會回傳是否成功(帳戶是否已存在)
					/*String account = br.readLine();
					String password = br.readLine();
					String username = br.readLine();
					if (DBuser.SignUp(account, password, username)) {
						System.out.println("Sign up success");
						ObjectInputStream ois = new ObjectInputStream(conn.getInputStream());
						byte[] buffer;
						try {
							if((buffer=(byte[])ois.readObject())!=null)	//判斷是否有照片
							{
								FileManager photo=new FileManager("UserPhoto\\"+account+".jpg");
								photo.writeObjec(buffer);
								DBuser.setPhoto(account, photo.savePath);	//變更使用者照片
							}
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						pw.println("success");
					} else {
						System.out.println("Sign up fail");	//帳戶已存在
						pw.println("fail");
					}*/
				}
				else if(command.equals("GetUserInfo"))
				{	
					/*String account = br.readLine();
					String userInfo=DBuser.getUserInfo(account);
					int userID = DBuser.getUserID(account);
					String userDetail = DBuser.getUserDetail(userID);
					
					if(userInfo!=null)
					{	
						pw.println("success");
						pw.println(userInfo);
						pw.println(userDetail);
						

					}
					else
					{
						pw.print("fail");
					}*/
				}		
				else if(command.equals("GetPhoto"))
				{	
					String photoPath = br.readLine();
					File f = new File(photoPath);
					if(f.exists())
					{	pw.println("success");
						System.out.println("檔案存在");
//						if(br.readLine().equals("OK"))
//						{
						//寫入照片
						FileInputStream fis = new FileInputStream(f);
						ObjectOutputStream oos = new ObjectOutputStream(conn.getOutputStream());
						byte[] buffer = new byte[1024];
						int len=-1;
						ByteArrayOutputStream outStream = new ByteArrayOutputStream();
						while ((len = fis.read(buffer)) != -1) {
							outStream.write(buffer, 0, len);
						}
						byte[] photo = outStream.toByteArray();
						oos.writeObject(photo);
						fis.close();
//						}
					}
					else
					{
						pw.println("fail");
						System.out.println("檔案不存在");
					}	
				}
				else if(command.equals("UpdateUserInfo"))
				{	
					/*String account = br.readLine();
					String username = br.readLine();
					String age = br.readLine();
					String birth = br.readLine();
					String sex = br.readLine();
					String phone = br.readLine();
					String email = br.readLine();
					
					if(DBuser.updateUserinfo(username, account))
					{
					int userID = DBuser.getUserID(account);
					if(DBuser.updateUserDetail(userID, Integer.parseInt(age), birth, sex, phone, email))
					{
						pw.println("success");
					}
					
					}
					else
					pw.println("fail");*/
				}
				else if(command.equals("updateLocate")) {
					
					int userID = DBuser.getUserID(br.readLine());
					if(DBmap.uploadLocation(userID,
											Double.parseDouble(br.readLine()),
											Double.parseDouble(br.readLine()))) {
						pw.println("success");
						System.out.println("location upload success");
					}
					else {
						pw.println("failed");
						System.out.println("location upload failed");
					}
				}
				else if(command.equals("InsertProduct")) {
					//System.out.println("Product start insert");

					String productInfo = "";
					String [] s_productInfo;
					int productID;
					
					String productName = br.readLine();   // 讀取  product 名稱
					int productPrice = Integer.parseInt(br.readLine());  // 讀取 product 金額
					int userID = DBuser.getUserID(br.readLine());  // 讀取使用者ID
					 
					pw.println("msg1 success");
					
					productID = DBproduct.insertProduct(productName,productPrice,userID); 
					if(productID != -1) {  // 若 insert 成功 -> 建立商品路徑 ( 用來存 photo , info )
						
						try {
							File product_dir = new File("C:/DataBase/product/"+ productID);
							product_dir.mkdir();
						}catch(Exception e) {
							System.out.println("Create directory failed");
							e.printStackTrace();
						}
				
						ObjectInputStream ois = new ObjectInputStream(conn.getInputStream());
						byte[] buffer;
						
						try {
							if((buffer=(byte[])ois.readObject())!=null)	//判斷是否有 product info傳來
							{
				 
								FileManager info = new FileManager("DataBase/product/"+ productID +"/" + "info.txt");
								productInfo = new String(buffer,StandardCharsets.UTF_8);
								s_productInfo = productInfo.split("\n");
								info.writeAllLine(s_productInfo);;  // 將product  info存到指定路徑
								
								pw.println("msg2 success");
								pw.println(productID);
							}
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}							
					}
					else {
						System.out.println("Product insert failed!!");
						pw.println("upload failed");
					}
					
				}
				else if(command.equals("uploadProductPhoto")) {
					
					int userID = DBuser.getUserID(br.readLine());
					pw.println("msg1 success");
					
					int productID = DBproduct.getNewProductIDbyUserID(userID);  // 重新根據 userID 取得 productID
					
					ObjectInputStream ois = new ObjectInputStream(conn.getInputStream());
					byte[] buffer;
					
					try {
						if((buffer=(byte[])ois.readObject())!=null)	//判斷是否有 product photo 傳來
						{
							FileManager photo = new FileManager("DataBase/product/"+ productID + "/" + "photo.jpg");
							photo.writeObjec(buffer);  // 將照片存到指定路徑
							
							pw.println("msg2 success");
						}
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
				}
				else if(command.equals("getUserProduct")) {
					int userID = DBuser.getUserID(br.readLine());
					String pid_set = "";
					
					pid_set = DBproduct.getUserProduct(userID);
					pw.println(pid_set);
					pw.println("success");
				}
				else if(command.equals("getProductInfo")) {
					int productID = Integer.parseInt(br.readLine());
					
					String pinfo = DBproduct.getProductInfo(productID);
					
					if(pinfo!=null)
					{	
						pw.println("success");
						pw.println(pinfo);
					}
					else
					{
						pw.print("fail");
					}
				}

				
//				try {
//					Thread.sleep(3000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}

				conn.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	
}
