package svd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import util.DBUtil;

public class SVDPlusPlusTrainer extends SVDTrainer {
	private float y[][];
	private float z[];
	private float sum[];
	private List<Node>[] mHisMatrix;

	public SVDPlusPlusTrainer(int dim, boolean isTranspose) {
		super(dim, isTranspose);
	}

	public void loadFile(String mTrainFileName, String mTestFileName,
			String separater) throws Exception {
		super.loadFile(mTrainFileName, mTestFileName, separater);
		mHisMatrix = mRateMatrix;//继承的写法
	}

	public void train(float gama, float lambda, int nIter) {
		z = new float[dim];
		sum = new float[dim];
		y = new float[mItemNum + 1][dim];
		double Rmse = 0, mLastRmse = 100000;
		int nRateNum = 0;
		float rui = 0;
		for (int n = 1; n <= nIter; n++) {
			Rmse = 0;
			nRateNum = 0;
			for (int i = 1; i <= mUserNum; i++) {
				if (mRateMatrix[i].size() == 0)
					continue;
				float ru = (float) (1 / Math.sqrt(mHisMatrix[i].size()));
				for (int k = 0; k < dim; k++) {
					z[k] = p[i][k];//初始化=pu
					sum[k] = 0;
				}
				for (int k = 0; k < mHisMatrix[i].size(); k++) {
					for (int j = 0; j < dim; j++)
						z[j] += ru * y[mHisMatrix[i].get(k).getId()][j];//公式括号部分
				}
				for (int j = 0; j < mRateMatrix[i].size(); j++) {
					rui = mean
							+ bu[i]
							+ bi[mRateMatrix[i].get(j).getId()]
							+ mt.getInnerProduct(z, q[mRateMatrix[i].get(j)
									.getId()]);//svd++公式
					if (rui > mMaxRate)
						rui = mMaxRate;
					else if (rui < mMinRate)
						rui = mMinRate;
					float e = mRateMatrix[i].get(j).getRate() - rui;//eui

					//bu,bi,p,q
					bu[i] += gama * (e - lambda * bu[i]);
					bi[mRateMatrix[i].get(j).getId()] += gama
							* (e - lambda * bi[mRateMatrix[i].get(j).getId()]);
					for (int k = 0; k < dim; k++) {
						sum[k] += ru * e * q[mRateMatrix[i].get(j).getId()][k];//???
						p[i][k] += gama
								* (e * q[mRateMatrix[i].get(j).getId()][k] - lambda
										* p[i][k]);
						q[mRateMatrix[i].get(j).getId()][k] += gama
								* (e * (p[i][k] + z[k]) - lambda
										* q[mRateMatrix[i].get(j).getId()][k]);
					}
					Rmse += e * e;
					nRateNum++;
				}
				for (int k = 0; k < mHisMatrix[i].size(); k++) {//???
					for (int len = 0; len < dim; len++)
						y[mHisMatrix[i].get(k).getId()][len] += gama
								* (sum[len] - lambda
										* y[mHisMatrix[i].get(k).getId()][len]);
				}
			}
			Rmse = Math.sqrt(Rmse / nRateNum);
			print("n = " + n + " Rmse = " + Rmse);
			if (Rmse > mLastRmse)
				break;
			mLastRmse = Rmse;
			gama *= 0.9;
		}
		print("------training complete!------");
	}

	@Override
	public void predict(String mOutputFileName, String separator)
			throws Exception {
		print("------predicting------");
		Connection conn = DBUtil.getConn();
		PreparedStatement pst = conn.prepareStatement("insert into svd(userid,movieid,score) values(?,?,?)");
		
		int userId, itemId;
		float rate = 0;
		String mLine;
		double Rmse = 0;
		int nNum = 0;
		BufferedReader br = new BufferedReader(new FileReader(new File(
				mTestFileName)));
		BufferedWriter bw = null;
		if (!mOutputFileName.equals(""))
			bw = new BufferedWriter(new FileWriter(new File(mOutputFileName)));
		while ((mLine = br.readLine()) != null) {
			String[] splits = mLine.split(separator);
			userId = Integer.valueOf(splits[0]);
			itemId = Integer.valueOf(splits[1]);
			if (splits.length > 2)
				rate = Float.valueOf(splits[2]);
			if (isTranspose) {
				int temp = userId;
				userId = itemId;
				itemId = temp;
			}
			float ru;
			if (mHisMatrix[mUserId2Map.get(userId)].size() != 0)
				ru = (float) (1 / Math.sqrt(mHisMatrix[mUserId2Map.get(userId)]
						.size()));
			else
				ru = 1;
			for (int k = 0; k < dim; k++) {
				z[k] = p[mUserId2Map.get(userId)][k];
			}
			for (int k = 0; k < mHisMatrix[mUserId2Map.get(userId)].size(); k++) {
				for (int j = 0; j < dim; j++)
					z[j] += ru
							* y[mHisMatrix[mUserId2Map.get(userId)].get(k)
									.getId()][j];
			}
			float rui = mean + bu[mUserId2Map.get(userId)]
					+ bi[mItemId2Map.get(itemId)]
					+ mt.getInnerProduct(z, q[mItemId2Map.get(itemId)]);
			if (mOutputFileName.equals("")) {
				Rmse += (rate - rui) * (rate - rui);
				nNum++;
			} else {
				pst.setInt(1, userId);
				pst.setInt(2, itemId);
				pst.setFloat(3, rui);
				pst.executeUpdate();
				bw.write(userId + separator + itemId + separator + rui + "\n");
				bw.flush();
			}
		}
		print("test file Rmse = " + Math.sqrt(Rmse / nNum));
		br.close();
		if (bw != null)
			bw.close();
		DBUtil.Close();
	}
}