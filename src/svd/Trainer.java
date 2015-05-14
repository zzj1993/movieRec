package svd;

public interface Trainer {
	void loadFile(String mTrainFileName, String mTestFileName, String separator)
			throws Exception;

	void train(float gama, float alpha, int nIter);

	void predict(String mOutputFileName, String separator) throws Exception;
}