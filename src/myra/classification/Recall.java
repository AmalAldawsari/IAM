package myra.classification;

	import myra.Cost;
import myra.Cost.Maximise;
import myra.datamining.Dataset;

	public class Recall extends Measure {

		 @Override
		    public Cost evaluate(Dataset dataset, ClassificationModel model) {
			// fill is to create a matrix
		    	int[][] matrix = Measure.fill(dataset, model);

		    	double TN= 0;
				double  TP =0; 
				double  FN =0; 
				double  FP =0; 
				double precision= 0;
				double recall= 0 ; 
				for (int i = 0; i < matrix.length; i++) {
				    for (int j = 0; j < matrix.length; j++) {
					if (i ==1 &&  j==1) {
				    TN += matrix[i][j];
					} else if (i==2 && j==2) {
						    TP += matrix[i][j];
						}
						else  if (i==2 && j==2) {
						    TP += matrix[i][j];
						} else if (i==1 && j==2) {
							    FN += matrix[i][j];
							} else if (i==2 && j==1) {
								    FP += matrix[i][j];
								}
							
				//	 precision = TP / (TP + FP) ;
					 
					//total += matrix[i][j];
				    }}
				recall = TP / (TP + FN) ;
					
				return new Maximise((double)recall); 
		    }
		}


	

