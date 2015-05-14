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
	//ƽ����Ȩ���ԣ�Ԥ��userID��itemID������,s�����ƶȶ�������
	public double getRating(Table<Integer, Integer, Integer> uiRating1,
			Table<Integer, Integer, Double> uiTime,
			Table<Integer, Integer, Integer> iuRating2,
			int itemID,int userID,int k,int s) throws ClassNotFoundException, SQLException{
		double avgOtherItem=0.0;//���û�����������Ŀ��ƽ������
		double simSums=0.0;
		double weightAvg=0.0;//��Ȩƽ��
		int itemid;
		double similarity;
		double score;
		double timestamp;//8��ͷ
		double guiyi;
		double weight=0.0;//ʱ��Ȩ��
		
		uiCF uc = new uiCF();
		//��ȡ�����Ƶ�K����Ŀ
		List<Map.Entry<Integer, Double>> uiSim = uc.topKMatches(uiRating1,iuRating2,itemID,userID,k,s);
		Iterator<Map.Entry<Integer, Double>> it = uiSim.iterator();
		Entry<Integer, Double> entry;
		
		loadData load = new loadData();
		double a[] = load.Max_Min();
		Average avg = new Average();
		
		while(it.hasNext()){
			entry = it.next();
			itemid =entry.getKey();
			similarity = entry.getValue();//�õ����ƶ�
			timestamp = uiTime.get(itemid, userID);//�õ�ʱ���

			//��һ������ʱ��
			guiyi = (timestamp-a[1])/(a[0]-a[1]);
			
			//�õ��û�userID��itemid������
			Map<Integer,Integer> m = uiRating1.row(itemid);
			score = m.get(userID);
			
			
			if(itemid!=itemID){
				avgOtherItem = avg.getAverage(uiRating1,itemid);
//				weight = 1.0/(1+Math.exp(-guiyi));
				//�ۼ�
//				simSums += similarity*weight;				
//				weightAvg += (score-avgOtherItem)*similarity*weight;
				simSums += similarity;				
				weightAvg += (score-avgOtherItem)*similarity;			
			}
		}

		double avgItem = avg.getAverage(uiRating1,itemID);//����Ŀ�������û�����
		if(simSums==0)
			return avgItem;
		else
			return (avgItem+weightAvg/simSums);
			
	}
	
	/**
	 * @param userID ��userID���û��Ƽ���Ʒ
	 * @param k �����û���
	 * @param n �Ƽ���Ʒ����
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
		//pearson���ƶ�
		pst = conn.prepareStatement("insert into result2(userid,movieid,score) values(?,?,?)");		
		while(rs.next()){
			userid = rs.getInt(1);
			movieid = rs.getInt(2);//4��pearson���ƶ�
			rating = getRating(itemUserRating1,itemUserTime1,userItemRating1,movieid,userid,5,4);
			
			pst.setInt(1, userid);
			pst.setInt(2, movieid);//itemid=i
			pst.setDouble(3, rating);
			pst.executeUpdate();
		}
		DBUtil.Close();
	}
	
	/**
	 * @param userID ��userID���û��Ƽ���Ʒ
	 * @param k �����û���
	 * @param n �Ƽ���Ʒ����
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
		
		//��Ԥ��������,pearson���ƶ�		
		pst = conn.prepareStatement("insert into u2(userid,movieid,score) values(?,?,?)");				
		int userid,movieid;
		double rating;
		while(rs.next()){
			userid = rs.getInt(1);
			movieid = rs.getInt(2);//4��pearson���ƶ�
			rating = getRating(userItemRating1,userItemTime1,itemUserRating1,userid,movieid,5,4);
			
			pst.setInt(1, userid);
			pst.setInt(2, movieid);
			pst.setDouble(3, rating);
			pst.executeUpdate();
		}
		DBUtil.Close();
	}
}
