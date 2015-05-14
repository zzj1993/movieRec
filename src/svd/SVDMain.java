package svd;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import util.DBUtil;

public class SVDMain {
	public static void main(String[] args) throws ClassNotFoundException, IOException, SQLException {
		String trainfile = "dataset/u1.base";
		String testfile = "dataset/u1.test";
		String separator = "\t";
		String outputfile = "dataset/result.txt";
		int dim = 0;
		float gama = 0.006f;
		float alpha = 0.03f;
		int nIter = 100;
//		Trainer trainer = new SVDPlusPlusTrainer(dim, false);
		Trainer trainer = new SVDTrainer(dim, false);
		try {
			trainer.loadFile(trainfile, testfile, separator);
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
}