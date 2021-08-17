package myra.classification;

import myra.Cost;
import myra.Cost.Maximise;
import myra.datamining.Dataset;

public class precision extends Measure   {

	 @Override
	  
	 public Cost evaluate(Dataset dataset, ClassificationModel model) {
		// fill is to create a matrix
	    	int[][] matrix = Measure.fill(dataset, model);

	    	double TN= 0;
			double  TP =0; 
			double  FN =0; 
			double  FP =0; 
			double precision= 0;
			for (int i = 0; i < matrix.length; i++) {
			    for (int j = 0; j < matrix.length; j++) {
				if (i==j) {
			    TN += matrix[i][j];
					}
				//	else  if (i==2 && j==2) {
					//    TP += matrix[i][j];
					//} 
			    else if (i<j) {
						    FP += matrix[i][j];
						} else if (i>j) {
							    FN += matrix[i][j];
							}
						
				
				//total += matrix[i][j];
			    }}
			System.out.println(TN);
			System.out.println(FN);
			System.out.println(FP);

			precision = TP / (TP + FP) ;

			return new Maximise((double)precision); 
	    }

}
