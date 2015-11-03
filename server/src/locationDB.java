
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.*;


public class locationDB {
      
	    // Database 物件
		private Connection dbConnect = null;
		// 執行後傳入SQL的字串
		private Statement input_stat = null;
		// Query　結果
		private ResultSet result = null;
		// 執行後傳入 SQL的預備字串 -> 等待給定數值 -> 用　"?" 做標示
		private PreparedStatement prepare_input_stat = null;
		
				
		// Query 指令集
		
		// DROP TABLE
		private String dropTable = "DROP TABLE locationdb;";
		// CREATE TABLE
		private String createTable = "CREATE TABLE locationdb (" + 
									 "    userID     INT " + 
									 "  , latitude     DOUBLE " + 
									 "  , longitude     DOUBLE);";
		// INSERT TO TABLE
		private String insertToTable = "INSERT INTO locationdb(userID , latitude , longitude) " +
									   "VALUES (?,?,?);";
				
		// Database 連線
		public locationDB() {
			
			try {
				// Driver 註冊
				Class.forName("com.mysql.jdbc.Driver");
				// 取得 Connection -> 給定主機名稱 ，Database 名稱  ，使用者登入資訊
				dbConnect = DriverManager.getConnection("jdbc:mysql://localhost:8038/schoolproject?useUnicode=true&characterEncoding=Big5", 
						  "root", 
						  "steveandfrank");
				
			}catch(ClassNotFoundException e) {
				System.out.println("DriverClassNotFound :" + e.toString());
			}
			catch(SQLException x) {
				System.out.println("Exception :" + x.toString());
			}
			
		}
		
		// Query 方法 -> CREATE , INSERT , DROP , SELECT , UPDATE
		
		
		// 建立 table
		
		public void createSQLTable() {
			
			try{
				input_stat = dbConnect.createStatement();
				input_stat.executeUpdate(createTable);
			}catch(SQLException e) {
				System.out.println("CreateDB Exception :" + e.toString());
			}finally {
				close();
			}
		}
		
		 // 新增一筆新的資料到 locationdb table
		
		public void insertSQLTable(int userID , double lat , double lng) {
			
			try{
				prepare_input_stat = dbConnect.prepareStatement(insertToTable);
				prepare_input_stat.setInt(1, userID);
				prepare_input_stat.setDouble(2, lat);
				prepare_input_stat.setDouble(3, lng);
				prepare_input_stat.executeUpdate();
			}catch(SQLException e) {
				System.out.println("InsertDB Exception :" + e.toString());
			}finally {
				close();
			}
		}
		 // 刪除 Table 
		public void dropSQLTable() {
			
			try{
				input_stat = dbConnect.createStatement();
				input_stat.executeQuery(dropTable);
			}catch(SQLException e) {
				System.out.println("DropDB Exception :" + e.toString());
			}finally {
				close();
			}
		}
		
		public void selectSQLTable() {
			// not used
		}
		
		public void updateSQLTable(int userID , double lat , double lng) {
			// 更新 資料庫座標內容
			String updateUserLocate = "UPDATE locationdb set latitude = ? , longitude = ? " +
					  "WHERE userID = ?;";
			try{
				prepare_input_stat = dbConnect.prepareStatement(updateUserLocate);
				prepare_input_stat.setDouble(1, lat);
				prepare_input_stat.setDouble(2, lng);
				prepare_input_stat.setInt(3, userID);;
				prepare_input_stat.executeUpdate();
				
			}catch(SQLException e) {
				System.out.println("UpdateDB Exception :" + e.toString());
			}finally {
				close();
			}
		}
		
		// Location 上傳方法
		
		public boolean uploadLocation(int userID , double lat , double lng) {
			
			    // 檢查使用者位置資料是否已存在
				if(checkUserData(userID) == Check_result.EXISTED) {    // 若存在 -> 更新舊的
					updateSQLTable(userID,lat,lng);
					
					return true;
				}
				else if(checkUserData(userID) == Check_result.NOT_EXISTED){   // 若不存在 -> 新增一筆新的
					insertSQLTable(userID , lat , lng);
					
					return true;
				}
				else {  // 座標上傳失敗
					return false;
				}
				
		}
		
		public enum Check_result {     // 檢查結果狀態
			EXISTED , NOT_EXISTED , CHECK_FAILED 
		}
		
			// 檢查使用者座標資料是否存在於 locationdb 內
		public Check_result checkUserData(int userID) {
			
			Check_result cs;
			String getUser = "SELECT userID FROM locationdb where userID = ?;";
			
			try{
				prepare_input_stat = dbConnect.prepareStatement(getUser);
				prepare_input_stat.setInt(1, userID);
				result = prepare_input_stat.executeQuery();
				
				if(result.next()) { // 若存在
					//System.out.println("user data has already existed");
					cs =  Check_result.EXISTED;
				}
				else { // 若不存在
					//System.out.println("user data is not existed");
					cs = Check_result.NOT_EXISTED;
				}
			    
			}catch(SQLException e) {
				e.printStackTrace();  // 檢查失敗
				cs = Check_result.CHECK_FAILED;
			}finally{
				close();
			}
			return cs;
		}
		
			// 根據 user的座標 (lat , lng)  取得使用者範圍內的其他使用者
		
		public String getRangeID(double lat , double lng ,int userID) {
			
			String Qresult = "";
			
			double range_lat =  0.004;    // 所取範圍
			double range_lng = 0.02;
			
			String query = "SELECT * from locationdb where (latitude > ? AND latitude < ?) AND " + 
						   "(longitude > ? AND longitude < ?) AND (userID != ?) ;";
			
			try{
				prepare_input_stat = dbConnect.prepareStatement(query);
				prepare_input_stat.setDouble(1, lat - range_lat);
				prepare_input_stat.setDouble(2, lat + range_lat);
				prepare_input_stat.setDouble(3, lng - range_lng);
				prepare_input_stat.setDouble(4, lng + range_lng);
				prepare_input_stat.setInt(5, userID);
				result = prepare_input_stat.executeQuery();
				 
				 while(result.next()) {   // 取得符合所設範圍的使用者ID
					 
					 Qresult += result.getString("userID") + "," + result.getDouble("latitude") +
							    "," + result.getDouble("longitude") + "\n";
				 }
	
			}catch(SQLException e) {
				System.out.println("SelectDB Exception :" + e.toString());
			}finally {
				close();
			}
			
			return Qresult;
		}
		
		
		// Close 方法
		
		private void close() {
			
			try{
				if(result != null){
					result.close();
					result = null;
				}
				if(input_stat != null){
					input_stat.close();
					input_stat = null;
				}
				if(prepare_input_stat != null){
					prepare_input_stat.close();
					prepare_input_stat = null;
				}
			}catch(SQLException e) {
				System.out.println("Close Exception :" + e.toString());
			}
		}
		
		
		 // 測試用 main 
		
		/*public static void main(String[] args) {
			
			String rangeGet = "";
			locationDB userM = new locationDB();
			
			//userM.uploadLocation(1, 25.012345,121.543210);
			//userM.uploadLocation(2, 25.013475,121.541839);
			//userM.uploadLocation(3, 25.016475,121.534756);
			//userM.uploadLocation(4, 25.014383,121.535132);
			//userM.uploadLocation(5, 25.024847,121.570036);
			
			rangeGet = userM.getRangeID(25.013475,121.541839,2);
			
			System.out.println(rangeGet);
		}*/
}
