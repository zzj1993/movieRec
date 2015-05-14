package slopeone;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import util.DBUtil;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

public class SlopeOne {
	Table<Integer, Integer, Float> mData;//�û�-��Ŀ-����
	Table<Integer, Integer, Float> mDiffMatrix;//��Ŀ-��Ŀ-���ֲ�
	Table<Integer, Integer, Integer> mFreqMatrix;//��Ŀ-��Ŀ-��ͬ������
    
	static int[] mAllItems = new int[1000];

	public static void test_slopeone() throws ClassNotFoundException, SQLException, IOException{
		long t1 = System.currentTimeMillis();		
		Table<Integer, Integer, Float> data = DBUtil.getTrainData();
		SlopeOne so = new SlopeOne(data);
		Connection conn = DBUtil.getConn();
	    PreparedStatement pst = conn.prepareStatement("select distinct(userid) from test");
	    ResultSet rs = pst.executeQuery();
	    int userid;
	    while(rs.next()){
	    	userid = rs.getInt(1);
			so.predict(userid);
			so.weightlesspredict(userid);
		}

    	long t2 = System.currentTimeMillis();
    	System.out.println((t2-t1)+"ms");
	}

	public SlopeOne(Table<Integer, Integer, Float> data) {
		mData = data;
		buildDiffMatrix();
	}

  /**
   * Based on existing data, and using weights, try to predict all missing
   * ratings. The trick to make this more scalable is to consider only
   * mDiffMatrix entries having a large (>1) mFreqMatrix entry.
   * 
   * It will output the prediction 0 when no prediction is possible.
 * @throws SQLException
 * @throws ClassNotFoundException 
   */
  public Map<Integer, Double> predict(int userid) throws ClassNotFoundException, SQLException {
    Map<Integer, Float> predictions = new HashMap<Integer, Float>();
    Map<Integer, Integer> frequencies = new HashMap<Integer, Integer>();
    Connection conn = DBUtil.getConn();
    
    //the movie already rate by userid
    PreparedStatement pst = conn.prepareStatement("select movieid,score from base where userid=?");
    pst.setInt(1, userid);
    ResultSet rs = pst.executeQuery();
    int movieid;
    double score;
    Map<Integer,Double> itemRating = new TreeMap<Integer,Double>();
    while(rs.next()){
    	movieid = rs.getInt(1);
    	score = rs.getDouble(2);
    	itemRating.put(movieid, score);
    }
    //the movie in test
    pst = conn.prepareStatement("select movieid from test where userid=?");
    pst.setInt(1, userid);
    rs = pst.executeQuery();
    List<Integer> movie = new ArrayList<Integer>();
    while(rs.next()){
    	movieid = rs.getInt(1);
    	movie.add(movieid);
    }
    
    //ֻ��ʼ����Щ��Ҫ���ֵ�
    for (int j : movie) {
      frequencies.put(j, 0);
      predictions.put(j, 0.0f);
    }
    
    for (int j : itemRating.keySet()) {//��Ŀ-���������Ŀ��user.put(1, 4.0f);ֻ��1
      for (int k : movie) {//
          if( j != k) {
              /* Only for items the user has not seen */
              if(!itemRating.containsKey(k)){
                  try {
                      float newval = (mDiffMatrix.get(k, j).floatValue() + itemRating.get(j).floatValue()) 
                    		  * mFreqMatrix.get(k, j).intValue();
                      predictions.put(k, predictions.get(k) + newval);
                      frequencies.put(k, frequencies.get(k) + mFreqMatrix.get(k, j).intValue());
                    } catch (NullPointerException e) {
                    }
              }
          }        
      }
    }
    Map<Integer, Double> cleanpredictions = new TreeMap<Integer, Double>();
	pst = conn.prepareStatement("insert into slope_weight(userid,movieid,score) values(?,?,?)");
    double temp;
	for (int j : predictions.keySet()) {
      if (frequencies.get(j) > 0) {//��Щ�����0�����ǳ�ʼ����ʱ��test1��base1����0����
    	  temp = predictions.get(j).floatValue() / frequencies.get(j).intValue();
    	  cleanpredictions.put(j, temp);
    	  
    	  pst.setInt(1, userid);
    	  pst.setInt(2, j);
    	  pst.setDouble(3, temp);
    	  pst.executeUpdate();
      }else if (frequencies.get(j) == 0){
    	  cleanpredictions.put(j, 0.0);
    	  
    	  pst.setInt(1, userid);
    	  pst.setInt(2, j);
    	  pst.setDouble(3, 0.0);
    	  pst.executeUpdate();
      }
    }
	if(pst!=null){
		pst.close();
	}
	if(conn!=null){
		pst.close();
	}
    /*for (int j : itemRating.keySet()) {//��Щ���еľͲ��Ž�ȥ��
      cleanpredictions.put(j, itemRating.get(j));
    }*/
    return cleanpredictions;
  }

  /**
   * Based on existing data, and not using weights, try to predict all missing
   * ratings. The trick to make this more scalable is to consider only
   * mDiffMatrix entries having a large (>1) mFreqMatrix entry.
 * @throws SQLException 
 * @throws ClassNotFoundException 
   */
  public Map<Integer, Float> weightlesspredict(int userid) throws ClassNotFoundException, SQLException {
    Map<Integer, Float> predictions = new HashMap<Integer, Float>();
    Map<Integer, Integer> frequencies = new HashMap<Integer, Integer>();
    Connection conn = DBUtil.getConn();
    
    //the movie already rate by userid
    PreparedStatement pst = conn.prepareStatement("select movieid,score from base where userid=?");
    pst.setInt(1, userid);
    ResultSet rs = pst.executeQuery();
    int movieid;
    double score;
    Map<Integer,Double> itemRating = new TreeMap<Integer,Double>();
    while(rs.next()){
    	movieid = rs.getInt(1);
    	score = rs.getDouble(2);
    	itemRating.put(movieid, score);
    }
    //the movie in test
    pst = conn.prepareStatement("select movieid from test where userid=?");
    pst.setInt(1, userid);
    rs = pst.executeQuery();
    List<Integer> movie = new ArrayList<Integer>();
    while(rs.next()){
    	movieid = rs.getInt(1);
    	movie.add(movieid);
    }
    
    //ֻ��ʼ����Щ��Ҫ���ֵ�
    for (int j : movie) {
      frequencies.put(j, 0);
      predictions.put(j, 0.0f);
    }
    
    for (int j : itemRating.keySet()) {//��Ŀ-���������Ŀ��user.put(1, 4.0f);ֻ��1
      for (int k : movie) {//
          if( j != k) {
              /* Only for items the user has not seen */
              if(!itemRating.containsKey(k)){
                  try {
                	  float newval = (mDiffMatrix.get(k, j).floatValue() + itemRating.get(j).floatValue());
            		  predictions.put(k, predictions.get(k) + newval);
            		  frequencies.put(k, frequencies.get(k) + mFreqMatrix.get(k, j).intValue());
                    } catch (NullPointerException e) {
                    }
              }
          }        
      }
    }
    Map<Integer, Double> cleanpredictions = new TreeMap<Integer, Double>();
	pst = conn.prepareStatement("insert into slope_weightless(userid,movieid,score) values(?,?,?)");
    double temp;
	for (int j : predictions.keySet()) {
      if (frequencies.get(j) > 0) {//��Щ�����0�����ǳ�ʼ����ʱ��test1��base1����0����
    	  temp = predictions.get(j).floatValue() / frequencies.get(j).intValue();
    	  cleanpredictions.put(j, temp);
    	  
    	  pst.setInt(1, userid);
    	  pst.setInt(2, j);
    	  pst.setDouble(3, temp);
    	  pst.executeUpdate();
      }else if (frequencies.get(j) == 0){
    	  cleanpredictions.put(j, 0.0);
    	  
    	  pst.setInt(1, userid);
    	  pst.setInt(2, j);
    	  pst.setDouble(3, 0.0);
    	  pst.executeUpdate();
      }
    }
	if(pst!=null){
		pst.close();
	}
	if(conn!=null){
		pst.close();
	}
    return predictions;
  }

  //����Ҫ����������Ŀ-��Ŀ-���ֲ�;�����Ŀ-��Ŀ-��ͬ�û�������
  public void buildDiffMatrix() {
    mDiffMatrix = TreeBasedTable.create();
    mFreqMatrix = TreeBasedTable.create();
    // first iterate through users
    for (Map<Integer, Float> user : mData.rowMap().values()) {//����item-rating
      // then iterate through user data
      for (Map.Entry<Integer, Float> entry : user.entrySet()) {
        if (!mDiffMatrix.containsRow(entry.getKey())) {
          mDiffMatrix.put(entry.getKey(), 0,0f);//�ȸ�һά��ֵ����ά��ֵ��û��
          mFreqMatrix.put(entry.getKey(), 0,0);
        }
        for (Map.Entry<Integer, Float> entry2 : user.entrySet()) {//����ά��ֵ
          int oldcount = 0;
          if (mFreqMatrix.row(entry.getKey()).containsKey(entry2.getKey()))//�����ά�Ǹ�ֵ����
            oldcount = mFreqMatrix.row(entry.getKey()).get(entry2.getKey()).intValue();//��ͬ������
          
          float olddiff = 0.0f;
          if (mDiffMatrix.row(entry.getKey()).containsKey(entry2.getKey()))
            olddiff = mDiffMatrix.row(entry.getKey()).get(entry2.getKey()).floatValue();//ƽ�����ֲ�ֵ
          
          float observeddiff = entry.getValue() - entry2.getValue();//����item����֮��
          mFreqMatrix.row(entry.getKey()).put(entry2.getKey(), oldcount + 1);////��ͬ�������������ϴε�
          mDiffMatrix.row(entry.getKey()).put(entry2.getKey(), olddiff + observeddiff);//����item����֮��ĺ�
        }
      }
    }
    for (int j : mDiffMatrix.rowKeySet()) {
      for (int i : mDiffMatrix.row(j).keySet()) {
        float oldvalue = mDiffMatrix.row(j).get(i).floatValue();
        int count = mFreqMatrix.row(j).get(i).intValue();
        mDiffMatrix.row(j).put(i, oldvalue / count);//item i��item j��ƽ�����ֲ�
      }
    }
  }
}


