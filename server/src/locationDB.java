
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.*;


public class locationDB {
      
	    // Database ����
		private Connection dbConnect = null;
		// �����ǤJSQL���r��
		private Statement input_stat = null;
		// Query�@���G
		private ResultSet result = null;
		// �����ǤJ SQL���w�Ʀr�� -> ���ݵ��w�ƭ� -> �Ρ@"?" ���Х�
		private PreparedStatement prepare_input_stat = null;
		
				
		// Query ���O��
		
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
				
		// Database �s�u
		public locationDB() {
			
			try {
				// Driver ���U
				Class.forName("com.mysql.jdbc.Driver");
				// ���o Connection -> ���w�D���W�� �ADatabase �W��  �A�ϥΪ̵n�J��T
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
		
		// Query ��k -> CREATE , INSERT , DROP , SELECT , UPDATE
		
		
		// �إ� table
		
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
		
		 // �s�W�@���s����ƨ� locationdb table
		
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
		 // �R�� Table 
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
			// ��s ��Ʈw�y�Ф��e
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
		
		// Location �W�Ǥ�k
		
		public boolean uploadLocation(int userID , double lat , double lng) {
			
			    // �ˬd�ϥΪ̦�m��ƬO�_�w�s�b
				if(checkUserData(userID) == Check_result.EXISTED) {    // �Y�s�b -> ��s�ª�
					updateSQLTable(userID,lat,lng);
					
					return true;
				}
				else if(checkUserData(userID) == Check_result.NOT_EXISTED){   // �Y���s�b -> �s�W�@���s��
					insertSQLTable(userID , lat , lng);
					
					return true;
				}
				else {  // �y�ФW�ǥ���
					return false;
				}
				
		}
		
		public enum Check_result {     // �ˬd���G���A
			EXISTED , NOT_EXISTED , CHECK_FAILED 
		}
		
			// �ˬd�ϥΪ̮y�и�ƬO�_�s�b�� locationdb ��
		public Check_result checkUserData(int userID) {
			
			Check_result cs;
			String getUser = "SELECT userID FROM locationdb where userID = ?;";
			
			try{
				prepare_input_stat = dbConnect.prepareStatement(getUser);
				prepare_input_stat.setInt(1, userID);
				result = prepare_input_stat.executeQuery();
				
				if(result.next()) { // �Y�s�b
					//System.out.println("user data has already existed");
					cs =  Check_result.EXISTED;
				}
				else { // �Y���s�b
					//System.out.println("user data is not existed");
					cs = Check_result.NOT_EXISTED;
				}
			    
			}catch(SQLException e) {
				e.printStackTrace();  // �ˬd����
				cs = Check_result.CHECK_FAILED;
			}finally{
				close();
			}
			return cs;
		}
		
			// �ھ� user���y�� (lat , lng)  ���o�ϥΪ̽d�򤺪���L�ϥΪ�
		
		public String getRangeID(double lat , double lng ,int userID) {
			
			String Qresult = "";
			
			double range_lat =  0.004;    // �Ҩ��d��
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
				 
				 while(result.next()) {   // ���o�ŦX�ҳ]�d�򪺨ϥΪ�ID
					 
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
		
		
		// Close ��k
		
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
		
		
		 // ���ե� main 
		
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
