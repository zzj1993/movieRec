package common0503;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Table;

public class Average {
	//计算平均分，user或item
	public double getAverage(Table<Integer, Integer, Integer> rating,int id){		
		double count=0.0,sum=0.0;
		Map<Integer, Integer> score = rating.row(id);
		for(Entry<Integer, Integer> entry:score.entrySet()){
			sum += entry.getValue();
		}
		count = score.size();	
		if(count==0)
			return 0;
		else
			return (double)sum/count;
	}
}
