package evaluate;

import java.io.IOException;
import java.sql.SQLException;

import evaluate.EvaluateItemBased;
import evaluate.EvaluateSVDPP;
import evaluate.EvaluateSlopeOne;
import evaluate.EvaluateUserBased;

public class Test {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		long t1 = System.currentTimeMillis();
		EvaluateItemBased item = new EvaluateItemBased();
		EvaluateUserBased user = new EvaluateUserBased();
		EvaluateSlopeOne slope = new EvaluateSlopeOne();
		EvaluateSVDPP svdpp = new EvaluateSVDPP();
		EvaluateSVD svd = new EvaluateSVD();
		
		long t2 = System.currentTimeMillis();
		System.out.println((t2-t1)+"ms");
	}

}
