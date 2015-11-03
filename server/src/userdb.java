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
	// �s��object
	private Statement stat = null;
	// ����,�ǤJ��sql������r��
	private ResultSet rs = null;
	// ���G��
	private PreparedStatement pst = null;

	// ����,�ǤJ��sql���w�x���r��,�ݭn�ǤJ�ܼƤ���m
	// ���Q��?�Ӱ��Х�

	public userdb() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("���Udriver");
			// ���Udriver
			con = DriverManager
					.getConnection(
							"jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=Big5",
							"user", "12345678");
			System.out.println("���oconnection");
			// ���oconnection

			// jdbc:mysql://localhost/test?useUnicode=true&characterEncoding=Big5
			// localhost�O�D���W,test�Odatabase�W
			// useUnicode=true&characterEncoding=Big5�ϥΪ��s�X

		} catch (ClassNotFoundException e) {
			System.out.println("DriverClassNotFound :" + e.toString());
		}// ���i��|����sqlexception
		catch (SQLException x) {
			System.out.println("Exception :" + x.toString());
		}

	}

	public boolean Login(String account, String password) {
		String query = "select Password from userdb where binary Account=?"; // �ϥ�binary��O�j�p�g
		try {
			pst = con.prepareStatement(query);
			pst.setString(1, account);
			rs = pst.executeQuery();
			if (rs.next()) { // �P�_�O�_��result
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
		String query = "select Password from userdb where binary Account=?"; // �ϥ�binary��O�j�p�g
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
						+ rs.getString("Profile"); // �Y���򦳰l�[�s���,Profile�n�����̫�@��
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

			// �ഫbirthday���榡
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
				pst = con.prepareStatement(query); // �إߤ@���b����
				pst.setString(1, username);
				pst.setString(2, account);
				pst.setString(3, password);
				pst.executeUpdate();

				int id = getUserID(account);
				if (id != -1) {
//					System.out.print("id:" + id+"\n");
					query = "insert into userinfodb(userID) value(?)"; // �إ߱b��ԲӸ��,�ثe�u��userID
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

	// ����ϥΧ���Ʈw��,�O�o�n�����Ҧ�Object
	// �_�h�b����Timeout��,�i��|��Connection poor�����p
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
