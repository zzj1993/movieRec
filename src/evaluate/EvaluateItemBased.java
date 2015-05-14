package evaluate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import util.DBUtil;

public class EvaluateItemBased {
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
	public EvaluateItemBased() throws ClassNotFoundException, SQLException {
		/**************item-base**************/
		System.out.println("*********item-base-pearson*********");
		MAE_RMSE_item();
		recall_precision_item();
	}
	
	public void MAE_RMSE_item() throws ClassNotFoundException, SQLException{	
		conn = DBUtil.getConn();
		pst = conn
				.prepareStatement("SELECT sum(abs(a.score-b.score)),sum(pow(a.score-b.score,2)),count(*) from result2 a,test b where a.movieid=b.movieid and a.userid=b.userid");
		rs = pst.executeQuery();
		while (rs.next()) {
			sum1 = rs.getDouble(1);
			sum2 = rs.getDouble(2);
			count = rs.getInt(3);
		}
		mae = sum1 / count;
		rmse = Math.sqrt(sum2 / count);
		System.out.println("MAE=" + mae);
		System.out.println("RMSE=" + rmse);
		// recall-precision
		pst = conn
				.prepareStatement("SELECT movieid from result2 where score>=4");
		rs = pst.executeQuery();
		while (rs.next()) {
			list3.add(rs.getInt(1));
		}
		pst = conn
				.prepareStatement("SELECT movieid from result2 where score<4");
		rs = pst.executeQuery();
		while (rs.next()) {
			list4.add(rs.getInt(1));
		}

		db.Close();
	}
	public void recall_precision_item(){
		result.clear();
		result.addAll(list1);
		result.retainAll(list3);// 喜欢&被推荐
//		System.out.println(list1.size()+"\t"+list3.size());
		double hit = result.size();// hit等于test1中user1的movie && 推荐给1的前20movie
		result.clear();
		result.addAll(list2);
		result.retainAll(list3);// 不喜欢&被推荐
		precision = hit / (result.size() + hit);// (喜欢&被推荐)/(喜欢&被推荐+不喜欢&被推荐)

		result.clear();
		result.addAll(list1);
		result.retainAll(list4);// 喜欢&未被推荐
		recall = hit / (result.size() + hit);// (喜欢&被推荐)/(喜欢&被推荐+喜欢&不被推荐)

		System.out.println("recall=" + recall);
		System.out.println("precision=" + precision);
	}
}
