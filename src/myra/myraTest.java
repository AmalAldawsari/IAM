package myra;

import myra.classification.rule.impl.AntMiner;
import myra.classification.rule.impl.cAntMiner;
//import myra.classification.tree.AntTreeMiner;

public class myraTest {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main (String [] args) throws Exception{
	//String [] dataset = {"-f","/Users/mac/Desktop/Thesis/Datasets/spambase.arff"}; 
	
	//String [] dataset = {"-f","/Users/mac/Desktop/Thesis/Aldawood_dataset/SyngentaOriginal.arff"};
	
	//	String [] dataset = {"-f","/Users/mac/Downloads/classification/ionosphere.arff"};
		//String [] dataset = {"-f","/Users/mac/Downloads/classification/ionosphere.arff"};

	String [] dataset = {"-f","/Users/mac/Desktop/isk_factors_cervical_cancer.arff"};

		cAntMiner am = new cAntMiner();
		   am.run(dataset);	
	
	}
}
