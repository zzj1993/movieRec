package svd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.DBUtil;


public class SVDTrainer implements Trainer {
	protected boolean isTranspose;//转置
	protected int mUserNum;//用户号
	protected int mItemNum;//项目号
	protected int dim;//二维维度
	protected float[][] p;//用户二维矩阵
	protected float[][] q;//项目二维矩阵
	protected float[] bu;
	protected float[] bi;
	protected float mean;//平均评分
	protected float mMaxRate;
	protected float mMinRate;
	protected String mTestFileName;//测试集
	protected String mSeparator;//分隔符
	protected MathTool mt;
	protected Map<Integer, Integer> mUserId2Map;//userid-usernum
	protected Map<Integer, Integer> mItemId2Map;//itemid-itemnum
	protected List<Node>[] mRateMatrix;//list数组，user-item-rating

	public SVDTrainer(int dim, boolean isTranspose) {
		this.isTranspose = isTranspose;
		this.dim = dim;
		mt = MathTool.getInstance();
		mUserId2Map = new HashMap<>();
		mItemId2Map = new HashMap<>();
	}

	//得到mUserId2Map，mItemId2Map
	private void mapping(String fileName, String separator) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(
				new File(fileName)));
		int userId;
		int itemId;
		int mLineNum = 0;
		String mLine;
		while ((mLine = br.readLine()) != null) {
			String[] splits = mLine.split(separator);
			userId = Integer.valueOf(splits[0]);
			itemId = Integer.valueOf(splits[1]);
			if (isTranspose) {//如果是转置矩阵
				int temp = userId;
				userId = itemId;
				itemId = temp;
			}
			if (!mUserId2Map.containsKey(userId)) {
				mUserNum++;
				mUserId2Map.put(userId, mUserNum);
			}
			if (!mItemId2Map.containsKey(itemId)) {
				mItemNum++;
				mItemId2Map.put(itemId, mItemNum);
			}
			mLineNum++;
		}
	}

	protected void print(String out) {
		System.out.println(out);
	}

	//加载train集合test集
	public void loadFile(String mTrainFileName, String mTestFileName,
			String separator) throws Exception {
		this.mTestFileName = mTestFileName;
		mSeparator = separator;

		//用户号和项目号
		mapping(mTrainFileName, separator);
		mapping(mTestFileName, separator);

		//初始化数组
		p = new float[mUserNum + 1][dim];
		bu = new float[mUserNum + 1];
		for (int i = 1; i <= mUserNum; i++) {
			p[i] = new float[dim];
		}
		q = new float[mItemNum + 1][dim];
		bi = new float[mItemNum + 1];
		
		//user-item-rating
		mRateMatrix = new ArrayList[mUserNum + 1];
		for (int i = 1; i < mRateMatrix.length; i++)//用户号从0开始
			mRateMatrix[i] = new ArrayList<>();

		int userId, itemId, mLineNum = 0;
		float rate = 0;
		String mLine;
		BufferedReader br = new BufferedReader(new FileReader(new File(
				mTrainFileName)));
		while ((mLine = br.readLine()) != null) {
			String[] splits = mLine.split(separator);
			userId = Integer.valueOf(splits[0]);
			itemId = Integer.valueOf(splits[1]);
			if (isTranspose) {//交换两个值user-item-rating变为item-user-rating
				int temp = userId;
				userId = itemId;
				itemId = temp;
			}
			rate = Float.valueOf(splits[2]);
			mLineNum++;
			mRateMatrix[mUserId2Map.get(userId)].add(new Node(mItemId2Map
					.get(itemId), rate));//user-item-rating或item-user-rating
			mean += rate;
			if (rate < mMinRate)
				mMinRate = rate;//得到最小评分
			if (rate > mMaxRate)
				mMaxRate = rate;//最大
		}
		mean /= mLineNum;//itemid或userid平均评分
		init();
	}

	private void init() {
		for (int i = 1; i <= mUserNum; i++)
			for (int j = 0; j < dim; j++)
				p[i][j] = (float) (Math.random() / 10);//[0,1]/10,赋0
		for (int i = 1; i <= mItemNum; i++)
			for (int j = 0; j < dim; j++)
				q[i][j] = (float) (Math.random() / 10);//赋0
	}

	//训练
	public void train(float gama, float lambda, int nIter) {
		double Rmse = 0, mLastRmse = 100000;
		int nRateNum = 0;
		float rui = 0;
		for (int n = 1; n <= nIter; n++) {
			Rmse = 0;
			nRateNum = 0;
			for (int i = 1; i <= mUserNum; i++)
				for (int j = 0; j < mRateMatrix[i].size(); j++) {
					rui = mean
							+ bu[i]
							+ bi[mRateMatrix[i].get(j).getId()]
							+ mt.getInnerProduct(p[i], q[mRateMatrix[i].get(j)
									.getId()]);//最小二乘公式，svd
					if (rui > mMaxRate)//大于最大评分
						rui = mMaxRate;
					else if (rui < mMinRate)
						rui = mMinRate;
					float e = mRateMatrix[i].get(j).getRate() - rui;//eui

					//随机梯度优化
					bu[i] += gama * (e - lambda * bu[i]);
					bi[mRateMatrix[i].get(j).getId()] += gama
							* (e - lambda * bi[mRateMatrix[i].get(j).getId()]);
					for (int k = 0; k < dim; k++) {
						p[i][k] += gama
								* (e * q[mRateMatrix[i].get(j).getId()][k] - lambda
										* p[i][k]);
						q[mRateMatrix[i].get(j).getId()][k] += gama
								* (e * p[i][k] - lambda
										* q[mRateMatrix[i].get(j).getId()][k]);
					}
					Rmse += e * e;
					nRateNum++;
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
		int userId, itemId;
		float rate = 0;
		String mLine;
		double Rmse = 0;
		int nNum = 0;
		Connection conn = DBUtil.getConn();
		PreparedStatement pst = conn.prepareStatement("insert into svd1(userid,movieid,score) values(?,?,?)");
		
		BufferedReader br = new BufferedReader(new FileReader(new File(
				mTestFileName)));//读取测试集
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
			float rui = mean
					+ bu[mUserId2Map.get(userId)]
					+ bi[mItemId2Map.get(itemId)]
					+ mt.getInnerProduct(p[mUserId2Map.get(userId)],
							q[mItemId2Map.get(itemId)]);//得到预测的评分值
			if (mOutputFileName.equals("")) {
				Rmse += (rate - rui) * (rate - rui);
				nNum++;
			} else {
				pst.setInt(1, userId);
				pst.setInt(2, itemId);
				pst.setDouble(3, rui);
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