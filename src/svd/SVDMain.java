package svd;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SVDMain {
	public static void main(String[] args) throws ClassNotFoundException, IOException, SQLException {
/*		if (args.length < 5) {
			System.out
					.println("Usage: \n\t-train trainfile\n\t-test predictfile\n\t-his historyfile\n\t-sep separator\n\t-dim featureLength\n\t-gama gama\n\t-alpha alpha\n\t-iter iternum\n\t-out outputfile");
			return;
		}
		ConsoleHelper helper = new ConsoleHelper(args);
		String trainfile = helper.getArg("-train", "");
		String testfile = helper.getArg("-test", "");
		String hisfile = helper.getArg("-his", "");
		String separator = helper.getArg("-sep", "\t");
		String outputfile = helper.getArg("-out", "");
		int dim = helper.getArg("-dim", 8);
		float gama = helper.getArg("-gama", 0.006f);
		float alpha = helper.getArg("-alpha", 0.03f);
		int nIter = helper.getArg("-iter", 100);
		if (trainfile.equals("")) {
			System.out.println("please input trainfile");
			return;
		} else if (testfile.equals("")) {
			System.out.println("please input testfile");
			return;
		}*/
		String trainfile = "dataset/u1.base";
		String testfile = "dataset/u1.test";
		String separator = "\t";
//		String hisfile = "dataset/result.txt";
		String outputfile = "dataset/result.txt";
		int dim = 0;
		float gama = 0.006f;
		float alpha = 0.03f;
		int nIter = 100;
		Trainer trainer = new SVDPlusPlusTrainer(dim, false);
		try {
			trainer.loadFile(trainfile, testfile, separator);
//			if (!hisfile.equals(""))
//				trainer.loadHisFile(hisfile, separator);
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}
		trainer.train(gama, alpha, nIter);
		try {
			trainer.predict(outputfile, separator);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		exportdata();
	}
	
	public static void test_svd(){
		String trainfile = "dataset/u1.base";
		String testfile = "dataset/u1.test";
		String separator = "\t";
//		String hisfile = "dataset/result.txt";
		String outputfile = "dataset/result.txt";
		int dim = 0;
		float gama = 0.006f;
		float alpha = 0.03f;
		int nIter = 100;
		Trainer trainer = new SVDPlusPlusTrainer(dim, false);
		try {
			trainer.loadFile(trainfile, testfile, separator);
//			if (!hisfile.equals(""))
//				trainer.loadHisFile(hisfile, separator);
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}
		trainer.train(gama, alpha, nIter);
		try {
			trainer.predict(outputfile, separator);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void exportdata() throws IOException, ClassNotFoundException, SQLException{
		BufferedWriter bw = new BufferedWriter(new FileWriter("dataset/u1.test"));
		Connection conn = DBUtil.getConn();
		PreparedStatement pst = conn.prepareStatement("select userid,movieid,score from test");
		ResultSet rs = pst.executeQuery();
		while(rs.next()){
			bw.write(rs.getInt(1)+"\t"+rs.getInt(2)+"\t"+rs.getInt(3)+"\n");
			bw.flush();
		}
		bw.close();
	}
}