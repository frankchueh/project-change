import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.*;


public class productdb {
	
	// Database 物件
	private Connection dbConnect = null;
	// 執行後傳入SQL的字串
	private Statement input_stat = null;
	// Query　結果
	private ResultSet result = null;
	// 執行後傳入 SQL的預備字串 -> 等待給定數值 -> 用　"?" 做標示
	private PreparedStatement prepare_input_stat = null;
	
	String Qresult;
	
	
	// Database 連線
	public productdb() {
		
		try {
			// Driver 註冊
			Class.forName("com.mysql.jdbc.Driver");
			// 取得 Connection -> 給定主機名稱 ，Database 名稱  ，使用者登入資訊
			dbConnect = DriverManager.getConnection("jdbc:mysql://localhost/test?useUnicode=true&characterEncoding=Big5", 
											  "user", 
											  "12345678");
			
		}catch(ClassNotFoundException e) {
			System.out.println("DriverClassNotFound :" + e.toString());
		}
		catch(SQLException x) {
			System.out.println("Exception :" + x.toString());
		}
	}
	
	// 根據 userID 取得其所有的 productID 
	
	public String getUserProduct(int userID) {
		
	    Qresult = "";
	    String query = "SELECT productID FROM productdb WHERE userid = ?;";
	    
	    try {
	    	prepare_input_stat = dbConnect.prepareStatement(query);
			prepare_input_stat.setInt(1, userID);
			result = prepare_input_stat.executeQuery();
	    	
	    	while(result.next()) {
	    		
	    		Qresult += result.getInt("productID") + "\n";
	    	}
	    }catch(SQLException e) {
			System.out.println("SelectDB Exception :" + e.toString());
		}finally {
			close();
		}
		
		return Qresult;
	}
	
	public String getProductInfo(int productID) {
		
		Qresult = "";
		String query = "SELECT * FROM productdb WHERE productID = ? AND state != \"deleted\";";
		
		try {
	    	prepare_input_stat = dbConnect.prepareStatement(query);
			prepare_input_stat.setInt(1, productID);
			result = prepare_input_stat.executeQuery();
	    	
	    	Qresult += result.getString("Pname") + "\n" +
	    			   result.getInt("Price") + "\n" +
	    			   result.getString("Pphoto") + "\n" +
	    			   result.getString("Pinfo");
	    			   
	    }catch(SQLException e) {
			System.out.println("SelectDB Exception :" + e.toString());
		}finally {
			close();
		}
		
		return Qresult;

	}
	
	public void updateProduct(int productID , String Pname , int price) {
		
		String query = "UPDATE productdb SET Pname = ? , price = ? WHERE productID = ?;";
		
		try{
			prepare_input_stat = dbConnect.prepareStatement(query);
			prepare_input_stat.setString(1, Pname);
			prepare_input_stat.setInt(2, price);
			prepare_input_stat.setInt(3, productID);
			prepare_input_stat.executeUpdate();
			
		}catch(SQLException e) {
			System.out.println("UpdateDB Exception :" + e.toString());
		}finally {
			close();
		}
	}
	
	public int insertProduct(String Pname , int price , int userID) {
		
		String insertQuery = "INSERT INTO productdb(productID,Pname,price,userID,state)"
				+ "SELECT IFNULL(max(productID),0)+1 , ? , ? , ? FROM productdb;";
		
		String selectQuery = "SELECT MAX(productID) FROM productdb where userID = ?;";
		String updateQuery = "UPDATE productdb SET Pphoto = ? , Pinfo = ? WHERE productID = ?;";
		
		int newProductID = -1;  // 初始值為 -1 
		
		try{
			prepare_input_stat = dbConnect.prepareStatement(insertQuery);
			prepare_input_stat.setString(1, Pname);
			prepare_input_stat.setInt(2, price);
			prepare_input_stat.setInt(3, userID);
			prepare_input_stat.setString(4, "new");
			prepare_input_stat.executeUpdate();
			
			try{
				prepare_input_stat = dbConnect.prepareStatement(selectQuery);
				prepare_input_stat.setInt(1,userID);
				result = prepare_input_stat.executeQuery();
				
				newProductID = result.getInt("productID");
				
				String path = "DataBase/product/" + newProductID + "/";
				
				try{
					prepare_input_stat = dbConnect.prepareStatement(updateQuery);
					prepare_input_stat.setString(1,path + "photo.jpg");
					prepare_input_stat.setString(2,path + "info.txt");
					prepare_input_stat.setInt(3,newProductID);
					prepare_input_stat.executeUpdate();
				}catch(SQLException e) {
					System.out.println("UpdateDB Exception :" + e.toString());
				}
				
			}catch(SQLException e) {
				System.out.println("SelectDB Exception :" + e.toString());
			}
		}catch(SQLException e) {
			System.out.println("InsertDB Exception :" + e.toString());
		}finally {
			close();
		}
		
		System.out.println("product insert success");
		
		return newProductID;
	}
	
	public void deletProduct(int productID) {
		
		String query = "UPDATE productdb SET state = \"deleted\" WHERE productID = ?;";
		
		try{
			prepare_input_stat = dbConnect.prepareStatement(query);
			prepare_input_stat.setInt(1, productID);
			prepare_input_stat.executeUpdate();
			
		}catch(SQLException e) {
			System.out.println("UpdateDB Exception :" + e.toString());
		}finally {
			close();
		}
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
}
