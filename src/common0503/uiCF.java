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
	//1��ŷ����þ���
	public double sim_item_Euclidean(Table<Integer, Integer, Integer> rating,int ui1,int ui2){
		Map<Integer,Integer> rate1 = rating.row(ui1);
		Map<Integer,Integer> rate2 = rating.row(ui2);

		Set<Integer> set1 = rate1.keySet();
		Set<Integer> set2 = rate2.keySet();
		SetView<Integer> comSet = Sets.intersection(set1, set2);//�ҵ���ͬ�����û�
//		for (Integer integer : comUser)
//		    System.out.println(integer);		
		
		if(comSet.size()==0)//��ͬ�����û�����
			return -1;

		//����ƫ��֮�ͣ����ж���double�ͣ���ֹ���ʱ�õ�����
		int score1,score2,count=0;
		double distance=0.0;
		double result=0.0;
		//ͨ���������û��Ĺ�ͬ������Ŀ�������ƶ�
		for(int set:comSet){			
			score1 = rate1.get(set);//������������֮��
			score2 = rate2.get(set);
			distance += Math.pow(score1-score2, 2);
			count++;
		}
		result = 1.0/(1+Math.sqrt(distance)/Math.sqrt(count));
		return result;
	}
	
	//2���������ƶ�
	public double sim_item_cosine(Table<Integer, Integer, Integer> rating,int ui1,int ui2){
		Map<Integer,Integer> rate1 = rating.row(ui1);
		Map<Integer,Integer> rate2 = rating.row(ui2);

		Set<Integer> set1 = rate1.keySet();
		Set<Integer> set2 = rate2.keySet();
		SetView<Integer> comSet = Sets.intersection(set1, set2);//�ҵ���ͬ�����û�
//		for (Integer integer : comUser)
//		    System.out.println(integer);		
		
		if(comSet.size()==0)//��ͬ�����û�����
			return -1;

		//����ƫ��֮�ͣ����ж���double�ͣ���ֹ���ʱ�õ�����
		int score1,score2;
		double sum_score12=0.0,sum_score22=0.0;
		double result=0.0;
		double sum12=0;
		//ͨ���������û��Ĺ�ͬ������Ŀ�������ƶ�
		for(int set:comSet){			
			score1 = rate1.get(set);//������������֮��
			score2 = rate2.get(set);
			sum12 += score1*score2;
			sum_score12 += Math.pow(score1, 2);
			sum_score22 += Math.pow(score2, 2);		
		}
		result = sum12/(Math.sqrt(sum_score12)*Math.sqrt(sum_score22));
		return result;
	}
	//3���������������ƶ�
	public double sim_item_modicos(Table<Integer, Integer, Integer> rating,int ui1,int ui2){
		Map<Integer,Integer> rate1 = rating.row(ui1);
		Map<Integer,Integer> rate2 = rating.row(ui2);

		Set<Integer> set1 = rate1.keySet();
		Set<Integer> set2 = rate2.keySet();
		SetView<Integer> comSet = Sets.intersection(set1, set2);//�ҵ���ͬ�����û�
//		for (Integer integer : comUser)
//		    System.out.println(integer);		
		
		if(comSet.size()==0)//��ͬ�����û�����
			return -1;

		
		//����ƫ��֮�ͣ����ж���double�ͣ���ֹ���ʱ�õ�����
		int score1,score2,sum_score1=0,sum_score2=0;
		double sum_score12=0.0,sum_score22=0.0;
		double result=0.0;
		double sum12=0;
		
		Average avg = new Average();
		double avgItem1 = avg.getAverage(rating, ui1);
		double avgItem2 = avg.getAverage(rating, ui2);;
		//ͨ���������û��Ĺ�ͬ������Ŀ�������ƶ�
		for(int set:comSet){			
			score1 = rate1.get(set);//������������֮��
			score2 = rate1.get(set);
			sum_score1 += score1;
			sum_score2 += score2;
			
			sum12 += score1*score2;
			sum_score12 += Math.pow(score1, 2);
			sum_score22 += Math.pow(score2, 2);		
		}
		double fenzi = sum12-avgItem2*sum_score1;//ע���Ĺ����Ϊ0���������ܿ�ƽ��
		double fenmu = Math.sqrt(Math.abs(sum_score12-avgItem1*sum_score1))*Math.sqrt(Math.abs(sum_score22-avgItem2*sum_score2));
		if(fenmu==0)
			return 0;
		else{
			result = fenzi/fenmu;
			return result;
		}			
	}
	
	//4��pearson���ƶ�
	public double sim_item_pearson(Table<Integer, Integer, Integer> rating,int ui1,int ui2){
		Map<Integer,Integer> rate1 = rating.row(ui1);
		Map<Integer,Integer> rate2 = rating.row(ui2);

		Set<Integer> set1 = rate1.keySet();
		Set<Integer> set2 = rate2.keySet();
		SetView<Integer> comSet = Sets.intersection(set1, set2);//�ҵ���ͬ�����û�
//		for (Integer integer : comUser)
//		    System.out.println(integer);		
		
		if(comSet.size()==0)//��ͬ�����û�����
			return -1;
		//����ƫ��֮�ͣ����ж���double�ͣ���ֹ���ʱ�õ�����
		double sum1=0,sum2=0;
		//��ƽ��֮��
		double sum1Sq=0,sum2Sq=0;
		//��˻�֮�� ��XiYi
		double sumMulti = 0;
		double num1=0.0;
		double num2=0.0;
		double result=0.0;		
		
		//ͨ���������û��Ĺ�ͬ������Ŀ�������ƶ�
		for(int set:comSet){
			sum1 += rate1.get(set);//������������֮��
			sum2 += rate2.get(set);
			
			sum1Sq += Math.pow(rate1.get(set), 2);//��ƽ��֮��
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
	
	//��ȡ��item�����Ƶ�K����Ŀ,s�����ƶȶ�������
	public List<Map.Entry<Integer, Double>> topKMatches(
			Table<Integer, Integer, Integer> itemUserRating,
			Table<Integer, Integer, Integer> rating2,
			int ID1,int ID2,int k,int s) throws ClassNotFoundException, SQLException{
		//�ҳ�mm��userID���۹�������item		
		Map<Integer, Integer> uimap = rating2.row(ID2);
		
		//��Ŀ-���ƶ�-ʱ��������ظ�
		Map<Integer,Double> uiSim = new HashMap<Integer,Double>();
		double sim=0.0;
		//����������
		for(int ui:uimap.keySet()){//item������userID�Ѿ����۹�����Ŀ�����ƶ�						
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
					
				if(sim>0){//���ƶ�>0ʱ�ŷŽ�ȥ������û������					
					uiSim.put(ui,sim);
				}
			}
		}	
		List<Map.Entry<Integer, Double>> uiSim_sort = new ArrayList<Map.Entry<Integer, Double>>(uiSim.entrySet());
	    Collections.sort(uiSim_sort, new Comparator<Map.Entry<Integer, Double>>() {//����value����
	        public int compare(Map.Entry<Integer, Double> o1,
	          Map.Entry<Integer, Double> o2) {//��������list(0)
	         double result = o2.getValue() - o1.getValue();
	         if(result > 0)
	         	return 1;
	         else if(result == 0)
	         	return 0;
	         else 
	         	return -1;
	        }
	       });
	
	    //���ƶȴ�С�������У�����Ҫȡ��K��
		if(uiSim_sort.size()<=k){//���С��k��ֻѡ����Щ���Ƽ�
			return uiSim_sort;
		}else{//�������k��ѡ��������ߵ��û�
			uiSim_sort = uiSim_sort.subList(0, k);
			return uiSim_sort;
		}
	}
}
