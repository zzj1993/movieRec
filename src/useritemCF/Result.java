package useritemCF;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import util.DBUtil;

public class Result {
/*	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException{
		long t1 = System.currentTimeMillis();
		Result rt = new Result();//��ʱ58000ms����
//		Map<String,String> map = rt.get1MovieRating(1);//��ʱ251ms
		Map<String,String> map1 = rt.itemBaseRec(1);//��ʱ60168ms
		Map<String,String> map2 = rt.userBaseRec(1);//��ʱ54193ms
		//��ʱ3907215ms
		map1 = rt.itemBaseRec(1);//��ʱ60168ms
		map2 = rt.userBaseRec(1);//��ʱ54193ms
		
		map1 = rt.itemBaseRec(2);//��ʱ60168ms
		map2 = rt.userBaseRec(2);//��ʱ54193ms
		
		map1 = rt.itemBaseRec(3);//��ʱ60168ms
		map2 = rt.userBaseRec(3);//��ʱ54193ms
		
		map1 = rt.itemBaseRec(4);//��ʱ60168ms
		map2 = rt.userBaseRec(4);//��ʱ54193ms
		
		map1 = rt.itemBaseRec(5);//��ʱ60168ms
		map2 = rt.userBaseRec(5);//��ʱ54193ms
		long t2 = System.currentTimeMillis();
		System.out.println(map2);
		System.out.println("��ʱ"+(t2-t1)+"ms");
	}*/
	//�õ�һ���˵Ĺۿ���¼,movie-path
	public Map<String,String> get1MovieRating(int userid) throws ClassNotFoundException, SQLException{
		Map<String,String> m = new LinkedHashMap<String,String>();
		Connection conn = DBUtil.getConn();
		PreparedStatement pst = conn.prepareStatement("select name from movie a,base b where a.id=b.movieid and userid=? order by timestamp desc");//select movieid,score from base1 where userid=?
		pst.setInt(1, userid);
		ResultSet rs = pst.executeQuery();
		String movie;

		while(rs.next()){
			movie = rs.getString(1);
		}
		
		DBUtil.Close();
		return m;
	}

	//�õ�һ���˵�item-base�Ƽ�,movie-path
//	public Map<String,String> itemBaseRec(int userid) throws ClassNotFoundException, SQLException, IOException{			
//		Connection conn = DBUtil.getConn();
//		PreparedStatement pst = conn.prepareStatement("truncate result1");
//		pst.executeUpdate();
//		Recommend re = new Recommend();
//		re.getItemBaseRating(userid);//
//		
//		String sql = "insert into itemrec(userid,movieid) select userid,movieid from movie as a,result1 as b where a.id=b.movieid and userid=? order by score desc limit 0,20";//select movieid from result1 where userid=? order by score desc limit 0,20
//		pst = conn.prepareStatement(sql);
//		pst.setInt(1, userid);
////		ResultSet rs = pst.executeQuery();
//		pst.executeUpdate();
//		Map<String,String> m = new LinkedHashMap<String,String>();
//		
//		while(rs.next()){
//			m.put(rs.getString(1), path+String.valueOf(rand.nextInt(4)+1)+".jpg");//[0,4)
//		}
//		DBUtil.Close();
//		return m;
//	}
	
//	//�õ�һ���˵�user-base�Ƽ�,movie-path
//	public Map<String,String> userBaseRec(int userid) throws ClassNotFoundException, SQLException, IOException{	
//		Connection conn = DBUtil.getConn();
//		PreparedStatement pst = conn.prepareStatement("truncate u1");
//		pst.executeUpdate();
//		Recommend re = new Recommend();
//		re.getUserBaseRating(userid);//
//		
//		String sql = "insert into userrec(userid,movieid) select userid,movieid from movie as a,u1 as b where a.id=b.movieid and userid=? order by score desc limit 0,20";//select movieid from result1 where userid=? order by score desc limit 0,20
//		pst = conn.prepareStatement(sql);
//		pst.setInt(1, userid);
////		ResultSet rs = pst.executeQuery();
//		pst.executeUpdate();
//		
//		Map<String,String> m = new LinkedHashMap<String,String>();
//		
//		while(rs.next()){
//			m.put(rs.getString(1), path+String.valueOf(rand.nextInt(4)+1)+".jpg");//[0,4)
//		}
//		DBUtil.Close();
//		return m;
//	}
	
	//item-base�Ƽ�
/*	public Map<String,String> IselectRecByUID(int userid) throws ClassNotFoundException, SQLException{
		Map<String,String> map = new LinkedHashMap<String,String>();
		Connection conn = DBUtil.getConn();
		PreparedStatement pst = conn.prepareStatement("select name from movie a,itemrec b where a.id=b.movieid and userid=?");
		pst.setInt(1, userid);
		ResultSet rs = pst.executeQuery();
		Random rand = new Random();
		while(rs.next()){
			map.put(rs.getString(1), "images/more/more"+String.valueOf(rand.nextInt(4)+1)+".jpg");
		}
		return map;
	}
	
	//item-base�Ƽ�
	public Map<String,String> UselectRecByUID(int userid) throws ClassNotFoundException, SQLException{
		Map<String,String> map = new LinkedHashMap<String,String>();
		Connection conn = DBUtil.getConn();
		PreparedStatement pst = conn.prepareStatement("select name from movie a,userrec b where a.id=b.movieid and userid=?");
		pst.setInt(1, userid);
		ResultSet rs = pst.executeQuery();
		Random rand = new Random();
		while(rs.next()){
			map.put(rs.getString(1), "images/more/more"+String.valueOf(rand.nextInt(4)+1)+".jpg");
		}
		return map;
	}*/	
}
