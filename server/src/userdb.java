import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

	public userdb() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("註冊driver");
			// 註冊driver
			con = DriverManager
					.getConnection(
							"jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=Big5",
							"user", "12345678");
			System.out.println("取得connection");
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

	public boolean Login(String account, String password) {
		String query = "select Password from userdb where binary Account=?"; // 使用binary辨別大小寫
		try {
			pst = con.prepareStatement(query);
			pst.setString(1, account);
			rs = pst.executeQuery();
			if (rs.next()) { // 判斷是否有result
				String tem = rs.getString("Password");
				if (tem.equals(password)) {
					System.out.println("Login Success");
					return true;
				} else {
					System.out.println("Password Incorrect");
					return false;
				}
			} else {
				System.out.println("Account Not Exist");
				return false;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			Close();
		}
		return false;
	}

	public boolean isAvailable(String account) {
		String query = "select Password from userdb where binary Account=?"; // 使用binary辨別大小寫
		try {
			pst = con.prepareStatement(query);
			pst.setString(1, account);
			rs = pst.executeQuery();
			if (rs.next()) {
				System.out.println("Account is Exist");
				return false;
			} else {
				System.out.println("Account Not Exist");
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Close();
		}
		return false;
	}

	public void setPhoto(String account, String photoPath) {
		String query = "update userdb set Profile=? where Account=?";
		try {
			pst = con.prepareStatement(query);
			pst.setString(1, photoPath);
			pst.setString(2, account);
			pst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			Close();
		}
	}

	public String getUserInfo(String Account) {
		String info = "";
		String query = "select * from userdb where Account=?;";

		try {
			pst = con.prepareStatement(query);
			pst.setString(1, Account);
			rs = pst.executeQuery();
			if (rs.next())
				info += rs.getString("Account") + "\n"
						+ rs.getString("Username") + "\n"
						+ rs.getString("Profile"); // 若後續有追加新資料,Profile要維持最後一個
			return info;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			Close();
		}

	}

	public String getUserDetail(int userID) {
		String info = "";
		String query = "select * from userinfodb where userID=?";
		try {
			pst = con.prepareStatement(query);
			pst.setInt(1, userID);
			rs = pst.executeQuery();

			if (rs.next()) {
				info += rs.getInt("age") + "\n" + rs.getDate("Birthday") + "\n"
						+ rs.getString("sex") + "\n"
						+ rs.getString("cellphone") + "\n"
						+ rs.getString("email");
			}
			return info;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			Close();
		}
	}

	public boolean updateUserinfo(String username, String account) {
		String query = "update userdb set username =? where account=?";

		try {
			pst = con.prepareStatement(query);
			pst.setString(1, username);
			pst.setString(2, account);
			pst.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			Close();
		}
	}

	public int getUserID(String account) {
		String query = "select userID from userdb where account=?";

		try {
			pst = con.prepareStatement(query);
			pst.setString(1, account);
			rs = pst.executeQuery();
			if (rs.next())
				return rs.getInt("userID");
			else
				return -1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		} finally {
			Close();
		}
	}

	public boolean updateUserDetail(int userID, int Age, String Bdate,
		String sex, String cellphone, String email) {
		String query = "update userinfodb set "
				+ "age=?,Birthday=?,sex=?,cellphone=?,email=? "
				+ "where userID=?";

		try {

			// 轉換birthday的格式
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date parsed = sdf.parse(Bdate);
			java.sql.Date date = new java.sql.Date(parsed.getTime());
			pst = con.prepareStatement(query);
			pst.setInt(1, Age);
			pst.setDate(2, date);
			pst.setString(3, sex);
			if(cellphone.equals(""))
				pst.setNull(4, java.sql.Types.VARCHAR);
			else
				pst.setString(4, cellphone);
			
			if(cellphone.equals(""))
				pst.setNull(5, java.sql.Types.VARCHAR);
			else
				pst.setString(5, email);
			
			pst.setInt(6, userID);
			pst.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			Close();
		}

	}

	public boolean SignUp(String account, String password, String username) {
		String query = "insert into userdb(userID,Username,Account,Password) "
				+ "select ifNULL(max(userID),0)+1,?,?,? from userdb;";
		try {
			if (isAvailable(account)) {
				pst = con.prepareStatement(query); // 建立一筆帳戶資料
				pst.setString(1, username);
				pst.setString(2, account);
				pst.setString(3, password);
				pst.executeUpdate();

				int id = getUserID(account);
				if (id != -1) {
//					System.out.print("id:" + id+"\n");
					query = "insert into userinfodb(userID) value(?)"; // 建立帳戶詳細資料,目前只有userID
					pst = con.prepareStatement(query);
					pst.setInt(1, id);
					pst.executeUpdate();
				}
				return true;
			} else
				return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally {
			Close();
		}

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

//	public static void main(String[] args) {
//		userdb userDB = new userdb();
//		userDB.SignUp("test", "test", "frankchueh");
//		userDB.Login("test", "test");
//		userDB.updateUserDetail(userDB.getUserID("Frank1"), 21, "1994-07-31", "m", "0912345678",
//				"frank@email");
//		String r=userDB.getUserDetail(userDB.getUserID("test"));
//		System.out.println(r);
//		r=userDB.getUserInfo("test");
//		System.out.println(r);
//		
//	}
}
