package slopeone;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

public class SlopeOne {
	Table<Integer, Integer, Float> mData;//用户-项目-评分
	Table<Integer, Integer, Float> mDiffMatrix;//项目-项目-评分差
	Table<Integer, Integer, Integer> mFreqMatrix;//项目-项目-共同评分数
    
	static int[] mAllItems = new int[1000];
	public static void main(String args[]) throws ClassNotFoundException, SQLException, IOException {
		long t1 = System.currentTimeMillis();
	
		Table<Integer, Integer, Float> data = loadMovieLensTrain();
		// next, I create my predictor engine
		SlopeOne so = new SlopeOne(data);
//		System.out.println("Here's the data I have accumulated...");
//		so.printData();
		// then, I'm going to test it out...
//		HashMap<Integer, Float> user = new HashMap<Integer, Float>();
//		int user=1;
//		System.out.println("Ok, now we predict...");
//		user.put(1, 4.0f);//movie-rating
//		System.out.println("Inputting...");
//		SlopeOne1.print(user);
//		System.out.println("Getting...");
//		int i=1;
		Connection conn = DBUtil.getConn();
	    PreparedStatement pst = conn.prepareStatement("select distinct(userid) from test");
	    ResultSet rs = pst.executeQuery();
	    int userid;
	    while(rs.next()){
	    	userid = rs.getInt(1);
			so.predict(userid);
			so.weightlesspredict(userid);
//			so.weightlesspredict(i);
//			SlopeOne1.print(so.weightlesspredict(i));
		}
//    	SlopeOne1.print(so.weightlesspredict(user));//weightlesspredict返回prediction<Integer,Float>
    	//
    	/*user.put(4, 3.0f);
    	System.out.println("Inputting...");
//    	SlopeOne1.print(user);
    	System.out.println("Getting...");*/
    	
    	long t2 = System.currentTimeMillis();
    	System.out.println((t2-t1)+"ms");
  }

	public static void test_slopeone() throws ClassNotFoundException, SQLException, IOException{
		long t1 = System.currentTimeMillis();		
		Table<Integer, Integer, Float> data = loadMovieLensTrain();
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
	public static Connection getConn() throws ClassNotFoundException, SQLException{
		Connection conn=null;
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/data", "root", "123456");
		return conn;
	}
	//训练集
	public static Table<Integer, Integer, Float> loadMovieLensTrain() throws ClassNotFoundException, SQLException, IOException{
		Table<Integer, Integer, Float> mm = TreeBasedTable.create();
		Map<Integer,Float> m = new TreeMap<Integer,Float>();
		
		Connection conn = getConn();
		//这里要按movieid排序，不然下面赋值到数组时会出错，因为if和else的map是承接的
		PreparedStatement pst = conn.prepareStatement("select * from base1 order by userid asc,movieid asc");
		ResultSet rs = pst.executeQuery();
		int userid,movieid;
		float score;
		int i=0;
		while(rs.next()){
			userid = rs.getInt(2);
			movieid = rs.getInt(3);
			score = rs.getFloat(4);
			
			mm.put(userid, movieid, score);
		}
		/*pst = conn.prepareStatement("select distinct movieid from base1 order by movieid");
		rs = pst.executeQuery();
		while(rs.next()){
			mAllItems[i] = rs.getInt(1);
//			System.out.println(i+" "+mAllItems[i]);
			i++;
			
		}	*/
		if(rs!=null){
			rs.close();
		}
		if(pst!=null){
			pst.close();
		}
		if(conn!=null){
			conn.close();
		}	    
		return mm;
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
    
    //只初始化那些将要评分的
    for (int j : movie) {
      frequencies.put(j, 0);
      predictions.put(j, 0.0f);
    }
    
    for (int j : itemRating.keySet()) {//项目-评分里的项目，user.put(1, 4.0f);只有1
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
      if (frequencies.get(j) > 0) {//有些会等于0，但是初始化的时候test1和base1都是0？？
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
    /*for (int j : itemRating.keySet()) {//那些已有的就不放进去了
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
    
    //只初始化那些将要评分的
    for (int j : movie) {
      frequencies.put(j, 0);
      predictions.put(j, 0.0f);
    }
    
    for (int j : itemRating.keySet()) {//项目-评分里的项目，user.put(1, 4.0f);只有1
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
      if (frequencies.get(j) > 0) {//有些会等于0，但是初始化的时候test1和base1都是0？？
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

  //这里要构建的是项目-项目-评分差和矩阵、项目-项目-共同用户数矩阵
  public void buildDiffMatrix() {
    mDiffMatrix = TreeBasedTable.create();
    mFreqMatrix = TreeBasedTable.create();
    // first iterate through users
    for (Map<Integer, Float> user : mData.rowMap().values()) {//所有item-rating
      // then iterate through user data
      for (Map.Entry<Integer, Float> entry : user.entrySet()) {
        if (!mDiffMatrix.containsRow(entry.getKey())) {
          mDiffMatrix.put(entry.getKey(), 0,0f);//先给一维赋值，二维的值还没有
          mFreqMatrix.put(entry.getKey(), 0,0);
        }
        for (Map.Entry<Integer, Float> entry2 : user.entrySet()) {//给二维赋值
          int oldcount = 0;
          if (mFreqMatrix.row(entry.getKey()).containsKey(entry2.getKey()))//如果二维那个值有了
            oldcount = mFreqMatrix.row(entry.getKey()).get(entry2.getKey()).intValue();//共同评分数
          
          float olddiff = 0.0f;
          if (mDiffMatrix.row(entry.getKey()).containsKey(entry2.getKey()))
            olddiff = mDiffMatrix.row(entry.getKey()).get(entry2.getKey()).floatValue();//平均评分差值
          
          float observeddiff = entry.getValue() - entry2.getValue();//两个item评分之差
          mFreqMatrix.row(entry.getKey()).put(entry2.getKey(), oldcount + 1);////共同评分数，加上上次的
          mDiffMatrix.row(entry.getKey()).put(entry2.getKey(), olddiff + observeddiff);//两个item评分之差的和
        }
      }
    }
    for (int j : mDiffMatrix.rowKeySet()) {
      for (int i : mDiffMatrix.row(j).keySet()) {
        float oldvalue = mDiffMatrix.row(j).get(i).floatValue();
        int count = mFreqMatrix.row(j).get(i).intValue();
        mDiffMatrix.row(j).put(i, oldvalue / count);//item i和item j的平均评分差
      }
    }
  }
}


