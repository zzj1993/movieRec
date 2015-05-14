package common0503;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Compare {
	DBUtil db = new DBUtil();
	Connection conn;
	PreparedStatement pst;
	ResultSet rs;
	public static void main(String[] args) throws ClassNotFoundException, SQLException{
		Compare c = new Compare();
//		for(int i=1;i<=8;i++){
			c.MAE_RMSE_recall_precision(4);//pearson-item-base
			c.MAE_RMSE_recall_precision(8);//pearson-user-base
//		}	
	}
	
	//����ƽ���������;��������
	public void MAE_RMSE_recall_precision(int s) throws ClassNotFoundException, SQLException{
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
		pst = conn.prepareStatement("SELECT movieid from test where score>=4");
		rs = pst.executeQuery();
		while(rs.next()){
			list1.add(rs.getInt(1));//test����userid<=5��movie����
		}		
		pst = conn.prepareStatement("SELECT movieid from test where score<4");
		rs = pst.executeQuery();
		while(rs.next()){
			list2.add(rs.getInt(1));//test����userid<=5��movie����
		}
	
		
		/*****����MAE-RMSE******/
		switch(s){
		/**************item-base**************/
/*			case 1:
				System.out.println("*********Euclidean*********");
				pst1 = conn.prepareStatement("SELECT sum(abs(a.score-b.score)),sum(pow(a.score-b.score,2)),count(*) from result1 a,test1 b where a.movieid=b.movieid and a.userid=b.userid");
				pst2 = conn.prepareStatement("SELECT movieid from result1 where userid=? order by score desc limit 20");
				break;
			case 2:
				System.out.println("*********Cosine*********");
				pst1 = conn.prepareStatement("SELECT sum(abs(a.score-b.score)),sum(pow(a.score-b.score,2)),count(*) from result2 a,test1 b where a.movieid=b.movieid and a.userid=b.userid");
				pst2 = conn.prepareStatement("SELECT movieid from result2 where userid=? order by score desc limit 20");
				break;
			case 3:
				System.out.println("*********Modicos*********");
				pst1 = conn.prepareStatement("SELECT sum(abs(a.score-b.score)),sum(pow(a.score-b.score,2)),count(*) from result3 a,test1 b where a.movieid=b.movieid and a.userid=b.userid");
				pst2 = conn.prepareStatement("SELECT movieid from result3 where userid=? order by score desc limit 20");
				break;*/
			case 4:
				System.out.println("*********Pearson*********");				
				pst = conn.prepareStatement("SELECT sum(abs(a.score-b.score)),sum(pow(a.score-b.score,2)),count(*) from result1 a,test b where a.movieid=b.movieid and a.userid=b.userid");				
				rs = pst.executeQuery();
				while(rs.next()){
					sum1 = rs.getDouble(1);
					sum2 = rs.getDouble(2);
					count = rs.getInt(3);
				}
				mae = sum1/count;
				rmse = Math.sqrt(sum2/count);
				System.out.println("MAE="+mae);
				System.out.println("RMSE="+rmse);
				
				//recall-precision
				pst = conn.prepareStatement("SELECT movieid from result1 where score>=4");
				rs = pst.executeQuery();
				while(rs.next()){
					list3.add(rs.getInt(1));
				}
				pst = conn.prepareStatement("SELECT movieid from result1 where score<4");
				rs = pst.executeQuery();
				while(rs.next()){
					list4.add(rs.getInt(1));
				}
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
				break;
				
				/**************user-base**************/
/*			case 5:
				System.out.println("*********Euclidean*********");
				pst1 = conn.prepareStatement("SELECT sum(abs(a.score-b.score)),sum(pow(a.score-b.score,2)),count(*) from u1 a,test1 b where a.movieid=b.movieid and a.userid=b.userid");
				pst2 = conn.prepareStatement("SELECT movieid from u1 where userid=? order by score desc limit 20");
				break;
			case 6:
				System.out.println("*********Cosine*********");
				pst1 = conn.prepareStatement("SELECT sum(abs(a.score-b.score)),sum(pow(a.score-b.score,2)),count(*) from u2 a,test1 b where a.movieid=b.movieid and a.userid=b.userid");
				pst2 = conn.prepareStatement("SELECT movieid from u2 where userid=? order by score desc limit 20");
				break;
			case 7:
				System.out.println("*********Modicos*********");
				pst1 = conn.prepareStatement("SELECT sum(abs(a.score-b.score)),sum(pow(a.score-b.score,2)),count(*) from u3 a,test1 b where a.movieid=b.movieid and a.userid=b.userid");
				pst2 = conn.prepareStatement("SELECT movieid from u3 where userid=? order by score desc limit 20");
				break;*/
			case 8:
				System.out.println("*********Pearson*********");
				pst = conn.prepareStatement("SELECT sum(abs(a.score-b.score)),sum(pow(a.score-b.score,2)),count(*) from u1 a,test b where a.movieid=b.movieid and a.userid=b.userid");				
				rs = pst.executeQuery();
				while(rs.next()){
					sum1 = rs.getDouble(1);
					sum2 = rs.getDouble(2);
					count = rs.getInt(3);
				}
				mae = sum1/count;
				rmse = Math.sqrt(sum2/count);
				System.out.println("MAE="+mae);
				System.out.println("RMSE="+rmse);
				
				//recall-precision
				pst = conn.prepareStatement("SELECT movieid from u1 where score>=4");
				rs = pst.executeQuery();
				while(rs.next()){
					list3.add(rs.getInt(1));
				}
				pst = conn.prepareStatement("SELECT movieid from u1 where score<4");
				rs = pst.executeQuery();
				while(rs.next()){
					list4.add(rs.getInt(1));
				}		
				result.clear();
				result.addAll(list1);
				result.retainAll(list3);//ϲ��&���Ƽ�
				hit = result.size();//hit����test1��user1��movie && �Ƽ���1��ǰ20movie
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
				break;
		}
		
		
		/*******����recall-precision******/
		//ȡǰ20��Ϊ�Ƽ���Ŀ		

		if(rs!=null){
			rs.close();
		}
		if(pst!=null){
			pst.close();
		}
		if(conn!=null){
			conn.close();
		}
		long t2 = System.currentTimeMillis();
		System.out.println((t2-t1)+"ms");
	}
}
