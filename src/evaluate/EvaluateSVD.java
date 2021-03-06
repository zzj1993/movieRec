package evaluate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import util.DBUtil;

public class EvaluateSVD {
	DBUtil db = new DBUtil();
	Connection conn;
	PreparedStatement pst;
	ResultSet rs;
	double sum1=0.0,sum2=0.0;
	double mae;
	double rmse;
	double recall;
	double precision;
	int count=0;
	Object[] a = db.getTestData();
	List<Integer> list1 = (List<Integer>) a[0];
	List<Integer> list2 = (List<Integer>) a[1];
	
	List<Integer> list3 = new ArrayList<Integer>();//result rec
	List<Integer> list4 = new ArrayList<Integer>();//result unrec
	List<Integer> result = new ArrayList<Integer>();
	public EvaluateSVD() throws ClassNotFoundException, SQLException {
		/**************svd**************/
		System.out.println("*********svd*********");
		MAE_RMSE_svd();
		recall_precision_svd();
	}
	public void MAE_RMSE_svd() throws ClassNotFoundException, SQLException{
		conn = DBUtil.getConn();
		pst = conn.prepareStatement("SELECT movieid from svd1 where score>=4");
		rs = pst.executeQuery();
		while(rs.next()){
			list3.add(rs.getInt(1));
		}
		pst = conn.prepareStatement("SELECT movieid from svd1 where score<4");
		rs = pst.executeQuery();
		while(rs.next()){
			list4.add(rs.getInt(1));
		}
		
		pst = conn.prepareStatement("SELECT sum(abs(a.score-b.score)),sum(pow(a.score-b.score,2)),count(*) from svd1 a,test b where a.movieid=b.movieid and a.userid=b.userid");
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

		db.Close();
	}
	
	public void recall_precision_svd(){
		/*******recall-precision******/	
		result.clear();
		result.addAll(list1);
		result.retainAll(list3);
		double hit = result.size();
		result.clear();
		result.addAll(list2);
		result.retainAll(list3);
		precision = hit/(result.size()+hit);
				
		result.clear();
		result.addAll(list1);
		result.retainAll(list4);
		recall = hit/(result.size()+hit);
		
		System.out.println("recall="+recall);
		System.out.println("precision="+precision);
	}
}
