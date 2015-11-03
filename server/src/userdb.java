import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class userdb {

	private Connection con = null; // Database objects
	// 連接object
	private Statement stat = null;
	// 執行,傳入之sql為完整字串
	private ResultSet rs = null;
	// 結果集
	private PreparedStatement pst = null;
	// 執行,傳入之sql為預儲之字申,需要傳入變數之位置
	// 先利用?來做標示

	private String dropdbSQL = "DROP TABLE userdb ";

	private String createdbSQL = "CREATE TABLE User (" + "    id     INTEGER "
			+ "  , name    VARCHAR(20) " + "  , passwd  VARCHAR(20))";

	private String insertdbSQL = "insert into userdata(UserID,UserName,UserLatitude,UserLongitude) "
			+ "select ifNULL(max(UserID),0)+1,?,?,? from userdata;";

	private String selectSQL = "select * from userdb ";

	public userdb() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			// 註冊driver
			con =  DriverManager.getConnection("jdbc:mysql://localhost:8038/schoolproject?useUnicode=true&characterEncoding=Big5", 
					  "root", 
					  "steveandfrank");
			// 取得connection

			// jdbc:mysql://localhost/test?useUnicode=true&characterEncoding=Big5
			// localhost是主機名,test是database名
			// useUnicode=true&characterEncoding=Big5使用的編碼

		} catch (ClassNotFoundException e) {
			System.out.println("DriverClassNotFound :" + e.toString());
		}// 有可能會產生sqlexception
		catch (SQLException x) {
			System.out.println("Exception :" + x.toString());
		}

	}

	// 建立table的方式
	// 可以看看Statement的使用方式
	public void createTable() {
		try {
			stat = con.createStatement();
			stat.executeUpdate(createdbSQL);
		} catch (SQLException e) {
			System.out.println("CreateDB Exception :" + e.toString());
		} finally {
			Close();
		}
	}

	// 新增資料
	// 可以看看PrepareStatement的使用方式
	public void insertTable(String userid, String username , double ulat , double ulong) {
		try {
			pst = con.prepareStatement(insertdbSQL);
			pst.setString(1, username);
			pst.setDouble(2, ulat);
			pst.setDouble(3, ulong);
			pst.executeUpdate();
		} catch (SQLException e) {
			System.out.println("InsertDB Exception :" + e.toString());
		} finally {
			Close();
		}
	}

	// 刪除Table,
	// 跟建立table很像
	public void dropTable() {
		try {
			stat = con.createStatement();
			stat.executeUpdate(dropdbSQL);
		} catch (SQLException e) {
			System.out.println("DropDB Exception :" + e.toString());
		} finally {
			Close();
		}
	}

	// 查詢資料
	// 可以看看回傳結果集及取得資料方式
	public void SelectTable() {
		try {
			stat = con.createStatement();
			rs = stat.executeQuery(selectSQL);
			System.out.println("Usernumber\tUsername\tAccount\t\tPassword\tPhoto");
			while (rs.next()) {
				System.out.println(rs.getInt("Usernumber") + "\t\t"
						+ rs.getString("Username") + "\t\t"
						+ rs.getString("Account") + "\t\t" 
						+ rs.getString("Password") + "\t\t"
						+ rs.getString("Photo") + "\t\t"
						);
			}
		} catch (SQLException e) {
			System.out.println("DropDB Exception :" + e.toString());
		} finally {
			Close();
		}
	}
	
	public boolean Login(String account,String password)
	{	
		String query = "select Password from userdb where binary Account=?";	//使用binary辨別大小寫
		try {
			pst = con.prepareStatement(query);
			pst.setString(1, account);
			rs=pst.executeQuery();
			if(rs.next())
			{
				String tem=rs.getString("Password");
				if(tem.equals(password))
				{
					System.out.println("Login Success");
					return true;
				}
				else
				{
					System.out.println("Password Incorrect");
					return false;
				}
			}
			else
			{
				System.out.println("Account Not Exist");
				return false;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean isAvailable(String account)
	{
		String query = "select Password from userdb where binary Account=?";	//使用binary辨別大小寫
		try {
			pst = con.prepareStatement(query);
			pst.setString(1, account);
			rs=pst.executeQuery();
			if(rs.next())
			{
				System.out.println("Account is Exist");
				return false;
			}
			else
			{
				System.out.println("Account Not Exist");
				return true;
			}
			}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			Close();
		}
		return false;
	}
	
	public boolean SignUp(String account,String password,String username)
	{	
		String query = "insert into userdb(Usernumber,Username,Account,Password) " +
					"select ifNULL(max(Usernumber),0)+1,?,?,? from userdb;";
		try {
			if(isAvailable(account))
			{pst = con.prepareStatement(query);
			pst.setString(1, username);
			pst.setString(2, account);
			pst.setString(3, password);
			pst.executeUpdate();}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			Close();
		}
		return false;
		
	}
	
	public int getUserID(String account) {
		
		String query = "select userID FROM userdb WHERE Account = ?";
		int userID = -1;
		try {
			//System.out.println(account);
			pst = con.prepareStatement(query);
			pst.setString(1, account);
			rs = pst.executeQuery();
			
			
			if(rs.next()) {
				userID = rs.getInt("userID");
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			Close();
		}
		return userID;
		
	}
	
	// 完整使用完資料庫後,記得要關閉所有Object
	// 否則在等待Timeout時,可能會有Connection poor的狀況
	private void Close() {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (stat != null) {
				stat.close();
				stat = null;
			}
			if (pst != null) {
				pst.close();
				pst = null;
			}
		} catch (SQLException e) {
			System.out.println("Close Exception :" + e.toString());
		}
	}

	/*public static void main(String[] args) {
		// 測看看是否正常
		userdb test = new userdb();
		//test.SelectTable();
		test.insertTable("Frank704", "Frank", 24.222, 45.9873);
		test.insertTable("test1", "test1", 54.212, 43.699);
		test.insertTable("test2", "test2", 54.212, 43.699);
		test.insertTable("test3", "test3", 54.212, 43.699);
		test.insertTable("test4", "test4", 54.212, 43.699);
		測試登入
		test.Login("frank", "frank");
		test.Login("frank", "frak");
		test.Login("fran", "frank");
		test.Login("STEVE", "steve");
		test.Login("Steve", "Steven");
		test.Login("Steve", "Stev");
		
		
		//test.SignUp("test1","test2" , "test3");
		//test.SelectTable();
	}*/
}
