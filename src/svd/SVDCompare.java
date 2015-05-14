package svd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SVDCompare {
	static DBUtil db = new DBUtil();
	static Connection conn;
	static PreparedStatement pst1;
	static PreparedStatement pst2;
	static ResultSet rs1;
	static ResultSet rs2;
	public static void main(String[] args) throws ClassNotFoundException, SQLException{
		SVDCompare c = new SVDCompare();
		c.MAE_RMSE_recall_precision();
	}

	public static void MAE_RMSE_recall_precision() throws ClassNotFoundException, SQLException{
		long t1 = System.currentTimeMillis();
		
		double sum1=0.0,sum2=0.0;
		double mae;
		double rmse;
		double recall;
		double precision;
		int count=0;
		List<Integer> list1 = new ArrayList<Integer>();//test1 like
		List<Integer> list2 = new ArrayList<Integer>();//test1 unlike
		List<Integer> list3 = new ArrayList<Integer>();//result rec
		List<Integer> list4 = new ArrayList<Integer>();//result unrec
		List<Integer> result = new ArrayList<Integer>();
		conn = db.getConn();
		pst1 = conn.prepareStatement("SELECT movieid from test where score>=4");
		rs1 = pst1.executeQuery();
		while(rs1.next()){
			list1.add(rs1.getInt(1));//test����userid<=5��movie����
		}		
		pst1 = conn.prepareStatement("SELECT movieid from test where score<4");
		rs1 = pst1.executeQuery();
		while(rs1.next()){
			list2.add(rs1.getInt(1));//test����userid<=5��movie����
		}
		pst1 = conn.prepareStatement("SELECT movieid from svd where score>=4");
		rs1 = pst1.executeQuery();
		while(rs1.next()){
			list3.add(rs1.getInt(1));//test����userid<=5��movie����
		}
		pst1 = conn.prepareStatement("SELECT movieid from svd where score<4");
		rs1 = pst1.executeQuery();
		while(rs1.next()){
			list4.add(rs1.getInt(1));//test����userid<=5��movie����
		}
		
		pst1 = conn.prepareStatement("SELECT sum(abs(a.score-b.score)),sum(pow(a.score-b.score,2)),count(*) from svd a,test b where a.movieid=b.movieid and a.userid=b.userid");
		rs1 = pst1.executeQuery();
		while(rs1.next()){
			sum1 = rs1.getDouble(1);
			sum2 = rs1.getDouble(2);
			count = rs1.getInt(3);
		}
		mae = sum1/count;
		rmse = Math.sqrt(sum2/count);
		System.out.println("MAE="+mae);
		System.out.println("RMSE="+rmse);
		
		/*******recall-precision******/	
		result.clear();
		result.addAll(list1);
		result.retainAll(list3);//ϲ��&���Ƽ�
		double hit = result.size();//hit����test1��user1��movie && �Ƽ���1��ǰ20movie
		result.clear();
		result.addAll(list2);
		result.retainAll(list3);//��ϲ��&���Ƽ�
		precision = hit/(result.size()+hit);//(ϲ��&���Ƽ�)/(ϲ��&���Ƽ�+��ϲ��&���Ƽ�)
				
		result.clear();
		result.addAll(list1);
		result.retainAll(list4);//ϲ��&δ���Ƽ�
		recall = hit/(result.size()+hit);//(ϲ��&���Ƽ�)/(ϲ��&���Ƽ�+ϲ��&�����Ƽ�)
		
		System.out.println("recall="+recall);
		System.out.println("precision="+precision);
		if(rs1!=null){
			rs1.close();
		}
		if(rs2!=null){
			rs2.close();
		}
		if(pst1!=null){
			pst1.close();
		}
		if(pst2!=null){
			pst2.close();
		}
		if(conn!=null){
			conn.close();
		}
		long t2 = System.currentTimeMillis();
		System.out.println((t2-t1)+"ms");
	}
}
