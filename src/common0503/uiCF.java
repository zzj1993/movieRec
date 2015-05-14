package common0503;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

public class uiCF {
	//1、欧几里得距离
	public double sim_item_Euclidean(Table<Integer, Integer, Integer> rating,int ui1,int ui2){
		Map<Integer,Integer> rate1 = rating.row(ui1);
		Map<Integer,Integer> rate2 = rating.row(ui2);

		Set<Integer> set1 = rate1.keySet();
		Set<Integer> set2 = rate2.keySet();
		SetView<Integer> comSet = Sets.intersection(set1, set2);//找到共同评分用户
//		for (Integer integer : comUser)
//		    System.out.println(integer);		
		
		if(comSet.size()==0)//相同评价用户个数
			return -1;

		//所有偏好之和，所有都用double型，防止相除时得到整数
		int score1,score2,count=0;
		double distance=0.0;
		double result=0.0;
		//通过计算两用户的共同评分项目计算相似度
		for(int set:comSet){			
			score1 = rate1.get(set);//所有所用评分之和
			score2 = rate2.get(set);
			distance += Math.pow(score1-score2, 2);
			count++;
		}
		result = 1.0/(1+Math.sqrt(distance)/Math.sqrt(count));
		return result;
	}
	
	//2、余弦相似度
	public double sim_item_cosine(Table<Integer, Integer, Integer> rating,int ui1,int ui2){
		Map<Integer,Integer> rate1 = rating.row(ui1);
		Map<Integer,Integer> rate2 = rating.row(ui2);

		Set<Integer> set1 = rate1.keySet();
		Set<Integer> set2 = rate2.keySet();
		SetView<Integer> comSet = Sets.intersection(set1, set2);//找到共同评分用户
//		for (Integer integer : comUser)
//		    System.out.println(integer);		
		
		if(comSet.size()==0)//相同评价用户个数
			return -1;

		//所有偏好之和，所有都用double型，防止相除时得到整数
		int score1,score2;
		double sum_score12=0.0,sum_score22=0.0;
		double result=0.0;
		double sum12=0;
		//通过计算两用户的共同评分项目计算相似度
		for(int set:comSet){			
			score1 = rate1.get(set);//所有所用评分之和
			score2 = rate2.get(set);
			sum12 += score1*score2;
			sum_score12 += Math.pow(score1, 2);
			sum_score22 += Math.pow(score2, 2);		
		}
		result = sum12/(Math.sqrt(sum_score12)*Math.sqrt(sum_score22));
		return result;
	}
	//3、调整的余弦相似度
	public double sim_item_modicos(Table<Integer, Integer, Integer> rating,int ui1,int ui2){
		Map<Integer,Integer> rate1 = rating.row(ui1);
		Map<Integer,Integer> rate2 = rating.row(ui2);

		Set<Integer> set1 = rate1.keySet();
		Set<Integer> set2 = rate2.keySet();
		SetView<Integer> comSet = Sets.intersection(set1, set2);//找到共同评分用户
//		for (Integer integer : comUser)
//		    System.out.println(integer);		
		
		if(comSet.size()==0)//相同评价用户个数
			return -1;

		
		//所有偏好之和，所有都用double型，防止相除时得到整数
		int score1,score2,sum_score1=0,sum_score2=0;
		double sum_score12=0.0,sum_score22=0.0;
		double result=0.0;
		double sum12=0;
		
		Average avg = new Average();
		double avgItem1 = avg.getAverage(rating, ui1);
		double avgItem2 = avg.getAverage(rating, ui2);;
		//通过计算两用户的共同评分项目计算相似度
		for(int set:comSet){			
			score1 = rate1.get(set);//所有所用评分之和
			score2 = rate1.get(set);
			sum_score1 += score1;
			sum_score2 += score2;
			
			sum12 += score1*score2;
			sum_score12 += Math.pow(score1, 2);
			sum_score22 += Math.pow(score2, 2);		
		}
		double fenzi = sum12-avgItem2*sum_score1;//注意坟墓不能为0，附属不能开平方
		double fenmu = Math.sqrt(Math.abs(sum_score12-avgItem1*sum_score1))*Math.sqrt(Math.abs(sum_score22-avgItem2*sum_score2));
		if(fenmu==0)
			return 0;
		else{
			result = fenzi/fenmu;
			return result;
		}			
	}
	
	//4、pearson相似度
	public double sim_item_pearson(Table<Integer, Integer, Integer> rating,int ui1,int ui2){
		Map<Integer,Integer> rate1 = rating.row(ui1);
		Map<Integer,Integer> rate2 = rating.row(ui2);

		Set<Integer> set1 = rate1.keySet();
		Set<Integer> set2 = rate2.keySet();
		SetView<Integer> comSet = Sets.intersection(set1, set2);//找到共同评分用户
//		for (Integer integer : comUser)
//		    System.out.println(integer);		
		
		if(comSet.size()==0)//相同评价用户个数
			return -1;
		//所有偏好之和，所有都用double型，防止相除时得到整数
		double sum1=0,sum2=0;
		//求平方之和
		double sum1Sq=0,sum2Sq=0;
		//求乘积之和 ∑XiYi
		double sumMulti = 0;
		double num1=0.0;
		double num2=0.0;
		double result=0.0;		
		
		//通过计算两用户的共同评分项目计算相似度
		for(int set:comSet){
			sum1 += rate1.get(set);//所有所用评分之和
			sum2 += rate2.get(set);
			
			sum1Sq += Math.pow(rate1.get(set), 2);//求平方之和
			sum2Sq += Math.pow(rate2.get(set), 2);
			
			sumMulti += rate1.get(set)*rate2.get(set);
		}		
		num1 = sumMulti - (sum1*sum2/comSet.size());
		num2 = Math.sqrt( (sum1Sq-Math.pow(sum1,2)/comSet.size())*(sum2Sq-Math.pow(sum2,2)/comSet.size()));  
	
		if(num2==0)                                                
			return 0;  
		else{
			result = num1/num2;
			return result;
		}
	}
	
	//获取与item最相似的K个项目,s是相似度度量方法
	public List<Map.Entry<Integer, Double>> topKMatches(
			Table<Integer, Integer, Integer> itemUserRating,
			Table<Integer, Integer, Integer> rating2,
			int ID1,int ID2,int k,int s) throws ClassNotFoundException, SQLException{
		//找出mm中userID评价过的所有item		
		Map<Integer, Integer> uimap = rating2.row(ID2);
		
		//项目-相似度-时间戳，不重复
		Map<Integer,Double> uiSim = new HashMap<Integer,Double>();
		double sim=0.0;
		//计算相似性
		for(int ui:uimap.keySet()){//item与其他userID已经评论过的项目的相似度						
			if(ui!=ID1){
				switch(s){
					case 1:
						sim = sim_item_Euclidean(itemUserRating,ID1,ui);
						break;
					case 2:
						sim = sim_item_cosine(itemUserRating,ID1,ui);
						break;
					case 3:
						sim = sim_item_modicos(itemUserRating,ID1,ui);
						break;
					case 4:
						sim = sim_item_pearson(itemUserRating,ID1,ui);
						break;
				}
					
				if(sim>0){//相似度>0时才放进去，否则没有意义					
					uiSim.put(ui,sim);
				}
			}
		}	
		List<Map.Entry<Integer, Double>> uiSim_sort = new ArrayList<Map.Entry<Integer, Double>>(uiSim.entrySet());
	    Collections.sort(uiSim_sort, new Comparator<Map.Entry<Integer, Double>>() {//根据value排序
	        public int compare(Map.Entry<Integer, Double> o1,
	          Map.Entry<Integer, Double> o2) {//分数放在list(0)
	         double result = o2.getValue() - o1.getValue();
	         if(result > 0)
	         	return 1;
	         else if(result == 0)
	         	return 0;
	         else 
	         	return -1;
	        }
	       });
	
	    //相似度从小到大排列，所以要取后K个
		if(uiSim_sort.size()<=k){//如果小于k，只选择这些做推荐
			return uiSim_sort;
		}else{//如果大于k，选择评分最高的用户
			uiSim_sort = uiSim_sort.subList(0, k);
			return uiSim_sort;
		}
	}
}
