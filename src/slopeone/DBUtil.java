package slopeone;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
	public static Connection getConn() throws ClassNotFoundException, SQLException{
		Connection conn=null;
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/data", "root", "123456");
		return conn;
	}
}
