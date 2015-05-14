package useritemCF;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import util.DBUtil;

import com.google.common.collect.Table;

public class Recommend {
	//平均加权策略，预测userID对itemID的评分,s是相似度度量方法
	public double getRating(Table<Integer, Integer, Integer> uiRating1,
			Table<Integer, Integer, Double> uiTime,
			Table<Integer, Integer, Integer> iuRating2,
			int itemID,int userID,int k,int s) throws ClassNotFoundException, SQLException{
		double avgOtherItem=0.0;//本用户所有其他项目的平均评分
		double simSums=0.0;
		double weightAvg=0.0;//加权平均
		int itemid;
		double similarity;
		double score;
		double timestamp;//8开头
		double guiyi;
		double weight=0.0;//时间权重
		
		uiCF uc = new uiCF();
		//获取最相似的K个项目
		List<Map.Entry<Integer, Double>> uiSim = uc.topKMatches(uiRating1,iuRating2,itemID,userID,k,s);
		Iterator<Map.Entry<Integer, Double>> it = uiSim.iterator();
		Entry<Integer, Double> entry;
		
		loadData load = new loadData();
		double a[] = load.Max_Min();
		Average avg = new Average();
		
		while(it.hasNext()){
			entry = it.next();
			itemid =entry.getKey();
			similarity = entry.getValue();//得到相似度
			timestamp = uiTime.get(itemid, userID);//得到时间戳

			//归一化处理时间
			guiyi = (timestamp-a[1])/(a[0]-a[1]);
			
			//得到用户userID对itemid的评分
			Map<Integer,Integer> m = uiRating1.row(itemid);
			score = m.get(userID);
			
			
			if(itemid!=itemID){
				avgOtherItem = avg.getAverage(uiRating1,itemid);
//				weight = 1.0/(1+Math.exp(-guiyi));
				//累加
//				simSums += similarity*weight;				
//				weightAvg += (score-avgOtherItem)*similarity*weight;
				simSums += similarity;				
				weightAvg += (score-avgOtherItem)*similarity;			
			}
		}

		double avgItem = avg.getAverage(uiRating1,itemID);//本项目的所有用户评分
		if(simSums==0)
			return avgItem;
		else
			return (avgItem+weightAvg/simSums);
			
	}
	
	/**
	 * @param userID 给userID的用户推荐物品
	 * @param k 相似用户数
	 * @param n 推荐物品个数
	 */
	public void getItemBaseRating() throws ClassNotFoundException, SQLException, IOException{
		loadData load = new loadData();
		Object a[] = load.loadMovieLensTrain();//item-user
		Table<Integer, Integer, Integer> itemUserRating1 = (Table<Integer, Integer, Integer>) a[0];
		Table<Integer, Integer, Double> itemUserTime1 = (Table<Integer, Integer, Double>) a[1];
		Table<Integer, Integer, Integer> userItemRating1 = (Table<Integer, Integer, Integer>) a[2];
		Table<Integer, Integer, Double> userItemTime1 = (Table<Integer, Integer, Double>) a[3];
		
		
		Connection conn = DBUtil.getConn();
		String sql = "SELECT userid,movieid from test";
		PreparedStatement pst = conn.prepareStatement(sql);
		ResultSet rs = pst.executeQuery();
		int userid,movieid;
		double rating;
		
		/******************item-base*********************/		
		//pearson相似度
		pst = conn.prepareStatement("insert into result2(userid,movieid,score) values(?,?,?)");		
		while(rs.next()){
			userid = rs.getInt(1);
			movieid = rs.getInt(2);//4是pearson相似度
			rating = getRating(itemUserRating1,itemUserTime1,userItemRating1,movieid,userid,5,4);
			
			pst.setInt(1, userid);
			pst.setInt(2, movieid);//itemid=i
			pst.setDouble(3, rating);
			pst.executeUpdate();
		}
		DBUtil.Close();
	}
	
	/**
	 * @param userID 给userID的用户推荐物品
	 * @param k 相似用户数
	 * @param n 推荐物品个数
	 */
	public void getUserBaseRating() throws ClassNotFoundException, SQLException, IOException{
		loadData db = new loadData();
		Object a[] = db.loadMovieLensTrain();//item-user
		Table<Integer, Integer, Integer> itemUserRating1 = (Table<Integer, Integer, Integer>) a[0];
		Table<Integer, Integer, Double> itemUserTime1 = (Table<Integer, Integer, Double>) a[1];
		Table<Integer, Integer, Integer> userItemRating1 = (Table<Integer, Integer, Integer>) a[2];
		Table<Integer, Integer, Double> userItemTime1 = (Table<Integer, Integer, Double>) a[3];
	
		Connection conn = DBUtil.getConn();
		/******************user-base*********************/
		String sql = "SELECT userid,movieid from test";
		PreparedStatement pst = conn.prepareStatement(sql);
		ResultSet rs = pst.executeQuery();
		
		//将预测结果插入,pearson相似度		
		pst = conn.prepareStatement("insert into u2(userid,movieid,score) values(?,?,?)");				
		int userid,movieid;
		double rating;
		while(rs.next()){
			userid = rs.getInt(1);
			movieid = rs.getInt(2);//4是pearson相似度
			rating = getRating(userItemRating1,userItemTime1,itemUserRating1,userid,movieid,5,4);
			
			pst.setInt(1, userid);
			pst.setInt(2, movieid);
			pst.setDouble(3, rating);
			pst.executeUpdate();
		}
		DBUtil.Close();
	}
}
