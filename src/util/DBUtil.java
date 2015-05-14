package util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

public class DBUtil {
	static Connection conn;
	static PreparedStatement pst;
	static ResultSet rs;
	List<Integer> list1 = new ArrayList<Integer>();//test1 like
	List<Integer> list2 = new ArrayList<Integer>();//test1 unlike
	public static Connection getConn() throws ClassNotFoundException, SQLException{
		Connection conn=null;
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/data", "root", "123456");
		return conn;
	}
	
	//训练集
	public static Table<Integer, Integer, Float> getTrainData() throws ClassNotFoundException, SQLException, IOException{
		Table<Integer, Integer, Float> mm = TreeBasedTable.create();
		
		Connection conn = getConn();
		//这里要按movieid排序，不然下面赋值到数组时会出错，因为if和else的map是承接的
		PreparedStatement pst = conn.prepareStatement("select * from base order by userid asc,movieid asc");
		ResultSet rs = pst.executeQuery();
		int userid,movieid;
		float score;
		while(rs.next()){
			userid = rs.getInt(2);
			movieid = rs.getInt(3);
			score = rs.getFloat(4);
			
			mm.put(userid, movieid, score);
		}
		Close();
		return mm;
	}
	
	public Object[] getTestData() throws ClassNotFoundException, SQLException{
		Object[] a = new Object[2];
		conn = getConn();
		pst = conn.prepareStatement("SELECT movieid from test where score>=4");
		rs = pst.executeQuery();
		while(rs.next()){
			list1.add(rs.getInt(1));//
		}		
		pst = conn.prepareStatement("SELECT movieid from test where score<4");
		rs = pst.executeQuery();
		while(rs.next()){
			list2.add(rs.getInt(1));
		}
		a[0]=list1;
		a[1]=list2;
		Close();
		return a;
	}

	
	public static void Close() throws SQLException{
		if(rs!=null){
			rs.close();
		}
		if(pst!=null){
			pst.close();
		}
		if(conn!=null){
			conn.close();
		}
	}
}
