package test;

import java.io.IOException;
import java.sql.SQLException;

import slopeone.SlopeOne;
import slopeone.Compare;

import svd.SVDMain;
import svd.SVDCompare;
public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		long t1 = System.currentTimeMillis();
		SlopeOne.test_slopeone();
		Compare.MAE_RMSE_recall_precision();//weight
		Compare.MAE_RMSE_recall_precision1();//weightless
		
		SVDMain.test_svd();
		SVDCompare.MAE_RMSE_recall_precision();
		long t2 = System.currentTimeMillis();
		System.out.println((t2-t1)+"ms");
	}

}
