/*
 * Algorithm.java
 * (this file is part of MYRA)
 * 
 * Copyright 2008-2015 Fernando Esteban Barril Otero
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package myra.datamining;
import java.io.BufferedReader;
import java.io.BufferedWriter;

import static myra.datamining.Attribute.Type.NOMINAL;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

//import weka.classifiers.trees.J48;
//import weka.core.Instances;
import static myra.Config.CONFIG;
import myra.Config.ConfigKey;
import myra.Cost;
import myra.Option;
import myra.Option.BooleanOption;
import myra.classification.ClassificationModel;
import myra.rule.Rule;
import myra.rule.RuleList;
import myra.util.Logger;

/**
 * Base class for implementing data mining algorithms.
 * 
 * @since 4.5
 * 
 * @author Fernando Esteban Barril Otero
 */
public abstract class Algorithm {
	 /**
     * The config key for the input file.
     */
    public final static ConfigKey<String> INPUT_FILE = new ConfigKey<>();
    
    /**
     * The config key for the test file.
     */
    public final static ConfigKey<String> TEST_FILE = new ConfigKey<>();
    /**
     * The config key for the training file.
     */
    public final static ConfigKey<String> TRAINING_FILE = new ConfigKey<>();
    /**
     * The config key for the incremental file.
     */
    public final static ConfigKey<String> INCREMENTAL_FILE = new ConfigKey<>();
    /**
     * The config key for the export file.
     */
    public final static ConfigKey<String> EXPORT_FILE = new ConfigKey<>();

    /**
     * The config key for the random number generator.
     */
    public final static ConfigKey<Random> RANDOM_GENERATOR = new ConfigKey<>();

    /**
     * The config key for the random seed.
     */
    public final static ConfigKey<Long> RANDOM_SEED = new ConfigKey<>();

    /**
     * The width of the output console.
     */
    private final int CONSOLE_WIDTH = 80;
    private static final String DATA = "@data";

    /**
     * Returns the algorithm command-line options. The default implementation
     * includes options for {@link #TEST_FILE} and {@link #TRAINING_FILE}.
     * 
     * @return the algorithm command-line options.
     */
    protected Collection<Option<?>> options() {
	ArrayList<Option<?>> options = new ArrayList<Option<?>>();

	options.add(new Option<String>(INPUT_FILE,
				       "f",
				       "Path of the input file"));
options.add(new Option<String>(TRAINING_FILE,
				       "p1",
				       "%  training percentage"));
options.add(new Option<String>(INCREMENTAL_FILE,
				       "p2",
				       "%  incermintal percentage"));
options.add(new Option<String>(TEST_FILE,
				       "p3",
				       "%  Test percentage"));

	options.add(new Option<String>(TEST_FILE,
				       "t",
				       "Path of the (optional) test file"));

	options.add(new Option<String>(EXPORT_FILE,
				       "-export",
				       "Path of the file to save the model",
				       false,
				       "file"));

	// random seed
	options.add(new Option<Long>(RANDOM_SEED,
				     "s",
				     "Random %s value (default current time)",
				     false,
				     "seed") {
	    @Override
	    public void set(String value) {
		Long seed = Long.parseLong(value);

		CONFIG.set(RANDOM_SEED, seed);
		CONFIG.set(RANDOM_GENERATOR, new Random(seed));
	    }
	});

	return options;
    }

    /**
     * Process the command-line arguments.
     * 
     * @param args
     *            the array representing the command-line arguments.
     * 
     * @return a map of parameter [key, value] pairs from the command-line.
     */
    protected Map<String, String> processCommandLine(String[] args) {
	Map<String, String> parameters =
		new LinkedHashMap<String, String>(args.length, 1.0f);
	String current = null;

	for (int i = 0; i < args.length; i++) {
	    if (args[i].startsWith("-")) {
		if (current != null) {
		    parameters.put(current.substring(1), null);
		    current = null;
		}

		current = args[i];
	    } else {
		if (current == null) {
		    throw new IllegalArgumentException("Missing switch option for value: "
			    + args[i]);
		}

		parameters.put(current.substring(1), args[i]);
		current = null;
	    }
	}

	if (current != null) {
	    parameters.put(current.substring(1), null);
	}

	// set options values

	for (Option<?> option : options()) {
	    if (parameters.containsKey(option.getModifier())) {
		option.set(parameters.get(option.getModifier()));
	    }
	}

	return parameters;
	
    }

    /**
     * Entry point for the execution. This is a template method for the
     * subclasses. It calls the following methods:
     * 
     * <ul>
     * <li>{@link #train(Dataset)}</li>
     * <li>{@link #evaluate(Dataset, Model)}</li>
     * <li>{@link #test(Dataset, Model)} (if a test file has been provided)</li>
     * </ul>
     * 
     * @param args
     *            command-line arguments.
     * 
     * @throws Exception
     *             If an error occurs &mdash; e.g., I/O error.
     */
    /**
     * @param args
     * @throws Exception
     */
    public void run(String[] args) throws Exception {
	// sets property defaults // sets the random seed to be the current time.
    	defaults();     			
 	// reads command-line arguments 
    	Map<String, String> parameters = processCommandLine(args);			
	if (CONFIG.isPresent(INPUT_FILE)) {
           String path=CONFIG.get(INPUT_FILE);
    	    	
            // if the partitioning percent as given by the user
             	if (CONFIG.isPresent(TRAINING_FILE)&&CONFIG.isPresent(INCREMENTAL_FILE)&&CONFIG.isPresent(TEST_FILE)) {

            read(path,new Double(CONFIG.get(TRAINING_FILE))/100,new Double(CONFIG.get(INCREMENTAL_FILE))/100,new Double(CONFIG.get(TEST_FILE))/100);
                }else{
               	                
                            read(path,0.4,0.3,0.3);                     
                }
           CONFIG.set(TRAINING_FILE,path+"1"); 
           CONFIG.set(INCREMENTAL_FILE,path+"2") ;
           CONFIG.set(TEST_FILE,path+"3") ;
       
 // train 1 // start reading D1 which is available in 
	    ARFFReader reader = new ARFFReader();
	    Dataset dataset = reader.read(CONFIG.get(TRAINING_FILE));
	    // print the name of the algorithm , time, date, files path, options, relation ... seed
	  //  and return parameters
	    logRuntime(dataset, parameters); 
	    
	    // find the rules using ACO
	 System.out.println("_______Training_________");
	    Model model = train(dataset);
	
	    // print the rules ، تطبع الرولز وعدد الرولز والترمز
	    Logger.log("%n");
	    Logger.log(model.toString(dataset));
	    Logger.log("%n");
	  
	    // print the number of rules
	 // Logger.log(Integer.toString(model.getRuleList().length)); 
	    
            Rule[] Ruleold =model.getRuleList();
            ArrayList<Rule>ruleOldArrayList = new  ArrayList<>();
            // save the rules in an arraylist
            System.out.println("ٍRule quality:");

             for (int i = 0; i < Ruleold.length; i++) {
               if (Ruleold[i].getQuality()!=null&&Ruleold[i].terms().length>0){
            	   int a = i+1;
                   System.out.println("Rule "+a+" -----> "+Ruleold[i].getQuality().adjusted());
            	   ruleOldArrayList.add(Ruleold[i]);
                } 	}
	  // compute the accuracy for training file 1 and the rule coverage
             evaluate(dataset, model); 
	  
            /////////////////////train2/////////////////////////
        ARFFReader reader2 = new ARFFReader();
	    Dataset dataset2 = reader2.read(CONFIG.get(INCREMENTAL_FILE));

	    //logRuntime(dataset2, parameters);
	    Logger.log("%n");
		System.out.println("_______Incremental_________");
	    Model model2 = train(dataset2);
	    Logger.log("%n");
	    Logger.log(model2.toString(dataset2));
	    Logger.log("%n");
            
         Rule[] RuleNew =model2.getRuleList();
            ArrayList<Rule>rulenewArrayList =  new  ArrayList<>();
            ArrayList<Rule>ruleemptyArrayList =  new  ArrayList<>();
            
            System.out.println("ٍRule quality:");
            
            for (int i = 0; i < RuleNew.length; i++) {
               if (RuleNew[i].getQuality()!=null&&RuleNew[i].terms().length>0){
            	   int a=i+1;
               System.out.println("Rule "+a+" -----> "+RuleNew[i].getQuality().adjusted());
                 rulenewArrayList.add(RuleNew[i]);
            }
               else{
               ruleemptyArrayList.add(RuleNew[i]);
                } 
            }
            evaluate(dataset2, model2);
            
            
            /////////////////////Output/////////////////////////
             double similarity, quality;
            double w = 0.5;
            Rule ruleo, rulen; 
            RuleList discovered1 = new RuleList(); 
            ArrayList<Rule> outputList =  new  ArrayList<>();

            Logger.log("%n");
            System.out.println("_______Output Rules_________");
       
            for (int h = 0; h < rulenewArrayList.size(); h++) {
	            int e=h+1;
            	rulen=rulenewArrayList.get(h);
            	System.out.println("____Start with new rule "+e+" ___"+rulen.toString(dataset2));
            	for (int i=0; i < ruleOldArrayList.size(); i++ ) {
	            	Attribute target = dataset.attributes()[dataset.classIndex()];
	                Attribute target2 = dataset.attributes()[dataset2.classIndex()];
	               	ruleo=ruleOldArrayList.get(i);
	               	int q = i+1;
	               	if(rulen.getConsequent().toString(target2) == ruleo.getConsequent().toString(target)){	               		
	            	   System.out.println("_____ old rule "+q+" ____"+ruleo.toString(dataset));
	            	   similarity =  getSimilarity(rulen,ruleo);
	            	   System.out.println("Similarity = "+similarity);
	            		       
		                if (similarity ==1 && rulen.terms().length < ruleo.terms().length ){
		                	if (!outputList.contains(ruleo)){
		                	outputList.add(ruleo);}
		                	
		                //	System.out.println(" The old rule is added as it is longer than the new rule");
			               // System.out.println("Rule quality = "+ruleo.getQuality().adjusted());
		                }
		                else if (similarity ==1 && rulen.terms().length == ruleo.terms().length){
		                	quality=(w*rulen.getQuality().adjusted())+(w*similarity);
		                	if (!outputList.contains(rulen)){
			                	outputList.add(rulen);}
		                //	System.out.println("The new rule is added and its quality is updated");
		                //	System.out.println("the new quality ="+quality);
		                }
		                else if (similarity <1 && similarity >0 ){
		                	if (!outputList.contains(rulen)){
			                	outputList.add(rulen);}
		                //	System.out.println(" The new rule is added");		                			          
		                }
		                 // if simimlarity =0
		                else {
		                	if (!outputList.contains(rulen)){
			                	outputList.add(rulen);}
		               

		                	// System.out.println("The similarity is zero, so the new rule is added");
		                	 }	
		                }   else  
		                {	 	if (!outputList.contains(rulen)){
		                	outputList.add(rulen);}
		                if (!outputList.contains(ruleo)){
		                	outputList.add(ruleo);}
		                		//System.out.println("No rules have the same class label so both are added");
		                }       
            			        
            			}
            	} 
            			for(int i =0 ; i<outputList.size(); i++){			
            				discovered1.add(outputList.get(i));  }
            														  
            		discovered1.add(ruleemptyArrayList.get(0));    
            		RuleList r1 = new RuleList();
            	//	r1.removeDuplicates(discovered1);
                    System.out.println ("R1 rules are  "+incrementalModel(r1).toString(dataset2));

            	/*	for (int d =0 ; d <= outputList.size(); d++){
            		          if(outputList.get(d+1) == outputList.get(d)){
            		        	  outputList.remove(d+1);
            		          }
            		          
            		       //   discovered1.add(outputList.get(d));

            		}
            		for (int b =0; b < outputList.size(); b++ ){
            		      		
            		      		discovered1.add(outputList.get(b));
            		      	}*/
            		          
                	Model modelA = incrementalModel(discovered1);

            		//Model modelA = incrementalMod(outputList);
          Logger.log("%n");
          Logger.log("%n");
          System.out.println ("The new rule list has "+discovered1.size());

          System.out.println (incrementalModel(discovered1).toString(dataset2));
        //  evaluate(dataset2, new ClassificationModel(modelA));
	
   	    	
	               	            	              	 
     //       if (Ruleold.getClass().getName()==RuleNew.getClass().getName()) {  
      /*      Attribute target = dataset.attributes()[dataset.classIndex()];
            Attribute target2 = dataset.attributes()[dataset2.classIndex()];
           if ( rulenewArrayList.getConsequent().toString(target) == ruleOldArrayList.getConsequent().toString(target2)){*/
  //     Model modelf=incremental_result(rulenewArrayList, ruleOldArrayList,ruleemptyArrayList);
	//  evaluate(dataset2, new ClassificationModel(modelf));
      
	    // if a test file is provided, evaluates the model on the test
	    // data and logs the confusion matrix	         
       ARFFReader reader3 = new ARFFReader();
	    
       Dataset dataset3 = reader3.read(CONFIG.get(TEST_FILE));
	    if (CONFIG.isPresent(TEST_FILE)) {
		dataset3 = reader3.read(CONFIG.get(TEST_FILE));

		Logger.log("%n=== Evaluation on test set ===%n%n");

	test(dataset3, new ClassificationModel(modelA));
	   
	    }
    }}
//	  Logger.log("%nRunning time (seconds): %.2f%n", elapsed);

	/*    if (CONFIG.isPresent(EXPORT_FILE)) {
		FileWriter writer = null;
		try {
		    writer = new FileWriter(new File(CONFIG.get(EXPORT_FILE)));
//	    writer.write(model.export(dataset));
		} catch (IOException e) {
		    Logger.log("%nCould not export model: %s", e.getMessage());
		} finally {
		    if (writer != null) {
			writer.close();
		    }
		}

		Logger.close();
	    } 
	 else {
	    usage();
	} */
    

    /**
     * Trains the algorithm for the current dataset.
     * 
     * @param dataset
     *            the current dataset.
     * 
     * @return the model created.
     */
    protected abstract Model train(Dataset dataset);

    
   protected abstract Model train2(Dataset dataset);
    /**
     * Evaluates the model on the training data.
     * 
     * @param dataset
     *            the current dataset.
     * @param model
     *            the model.
     */
    protected abstract void evaluate(Dataset dataset, Model model);

    /**
     * Applies the model to the test data.
     * 
     * @param dataset
     *            the test data.
     * @param model
     *            the model.
     */
    protected abstract void test(Dataset dataset, Model model);
    protected abstract Model trainStep2(Dataset dataset, Model model);

    /**
     * Returns the algorithm description.
     * 
     * @return the algorithm description.
     */
    protected abstract String description();

    /**
     * Sets the default property values. The default implementation sets the
     * random seed to be the current time.
     */
    protected void defaults() {
	Long seed = System.currentTimeMillis();

	CONFIG.set(RANDOM_SEED, seed);
	CONFIG.set(RANDOM_GENERATOR, new Random(seed));
    }

    /**
     * Prints the usage information. This consists in listing all the options
     * available.
     */
    protected void usage() {
	TreeMap<String, Option<?>> map =
		new TreeMap<String, Option<?>>(new Comparator<String>() {
		    @Override
		    public int compare(String o1, String o2) {
			if (o1.startsWith("--")) {
			    if (o2.startsWith("--")) {
				return o1.compareTo(o2);
			    } else {
				return 1;
			    }
			} else if (o2.startsWith("--")) {
			    return -1;
			}

			return o1.compareTo(o2);
		    }
		});
	int longest = 0;

	// sorts options by modified

	for (Option<?> option : options()) {
	    if (option.getKey() != TEST_FILE
		    && option.getKey() != TRAINING_FILE) {
		String modifier = null;

		if (option.hasArgument()) {
		    modifier = String
			    .format("-%s",
				    option.getModifier() + String
					    .format(" <%s>    ",
						    option.getArgument()));
		} else {
		    modifier = String.format("-%s    ", option.getModifier());
		}

		map.put(modifier, option);
		longest = Math.max(longest, modifier.length());
	    }
	}

	StringBuffer buffer = new StringBuffer();

	// prints options' details

	for (String modifier : map.keySet()) {
	    buffer.append(String.format("  %-" + longest + "s", modifier));

	    String[] description = map.get(modifier).toString().split(" ");
	    int available = CONSOLE_WIDTH - (longest + 2);

	    for (String s : description) {
		if (s.length() > available) {
		    buffer.append(String.format("%n  %-" + longest + "s", " "));
		    available = CONSOLE_WIDTH - (longest + 2);
		}

		buffer.append(String.format("%s ", s));
		available -= (s.length() + 1);
	    }

	    buffer.append(String.format("%n%n"));
	}

	Logger.log("Usage: %s -f %s [-t %s] [options]%n%n",
		   getClass().getSimpleName(),
		   "<arff_training_file>",
		   "<arff_test_file>");

	try {
	    Properties messages = new Properties();
	    messages.load(getClass()
		    .getResourceAsStream("/myra-help.properties"));
	    String help = messages.getProperty("usage.message");

	    int available = CONSOLE_WIDTH;

	    for (String s : help.split(" ")) {
		if (s.length() > available) {
		    Logger.log("%n");
		    available = CONSOLE_WIDTH;
		}

		Logger.log("%s ", s);
		available -= (s.length() + 1);
	    }
	} catch (Exception e) {
	    // quietly ignored, the only effect is that the usage
	    // message is not going to be printed
	}

	Logger.log("%n%n%s%n%n", "The following options are available:");
	Logger.log("%s", buffer);
    }

    /**
     * Logs the runtime information.
     * 
     * @param dataset
     *            the training dataset.
     * @param parameters
     *            the command-line parameters.
     */
    // printing the algorithm , dates and time. the name of the files 1,2,3 after splitting
    //(first print)
    
    protected void logRuntime(Dataset dataset, Map<String, String> parameters) {
	String description = description() + " " + version();
	Logger.log("%s", description);

	DateFormat formatter = new SimpleDateFormat("EEE MMM dd yyyy");
	String timestamp = formatter.format(new Date());
Logger.log("%" + (80 - description.length()) + "s%n", timestamp);

	for (int i = 0; i < description.length(); i++) {
	  Logger.log("_");
	}

	formatter = new SimpleDateFormat("HH:mm:ss");
	timestamp = formatter.format(new Date());
	//Logger.log("%" + (80 - description.length()) + "s%n", timestamp);

	if (CONFIG.isPresent(TRAINING_FILE)) {
	//    Logger.log("%nTraining file: %s", CONFIG.get(TRAINING_FILE));
	}
	
	if (CONFIG.isPresent(INCREMENTAL_FILE)) {
//	    Logger.log("%nIncremintal file: %s", CONFIG.get(INCREMENTAL_FILE));
	}

	
	if (CONFIG.isPresent(TEST_FILE)) {
	//    Logger.log("%nTest file: %s", CONFIG.get(TEST_FILE));
	}

	parameters.remove("f"); // training file
	parameters.remove("t"); // test file

	if (!parameters.isEmpty()) {
//	    Logger.log("%nOptions:");

	    for (String option : parameters.keySet()) {
		String value = parameters.get(option);
		Logger.log(" -%s %s",
			   option,
			   (value == null ? "" : parameters.get(option)));
	    }
	}

	// default option values
//	Logger.log("%n%n[Runtime default values]%n");
	//		Logger.log("The option modifiers and their values %n");

	for (Option<?> option : options()) {
	    if (!parameters.containsKey(option.getModifier())
		    && CONFIG.isPresent(option.getKey())
		    && !option.getModifier().equals("f")
		    && !option.getModifier().equals("t")) {

		if (option instanceof BooleanOption
			&& !CONFIG.get(((BooleanOption) option).getKey())) {
		    // we only print the informantion for Boolean Options that
		    // have default values set to true
		} else {
	//	    Logger.log("\t-%s %s%n",
	//		       option.getModifier(),
	//		       option.value());
		}
	    }
	}

	//Logger.log("%n");
	//Logger.log("Relation: %s%n", dataset.getName());
	Logger.log("Instances: %d%n", dataset.size());
	Logger.log("Attributes: %d%n", (dataset.attributes().length - 1));

	if (dataset.getTarget().getType() == NOMINAL) {
	   Logger.log("Classes: %d%n", dataset.classLength());
	}

	if (CONFIG.isPresent(RANDOM_SEED)) {
//	    Logger.log("Random seed: %d%n", CONFIG.get(RANDOM_SEED));
	}

	Logger.log("%n");
    }

    /**
     * Return the current implementation version. This will only work when the
     * code is running from a jar file.
     * 
     * @return the implementaion version or an empty string when no version is
     *         available.
     */
    public static String version() {
	try {
	    Properties properties = new Properties();
	    properties.load(Algorithm.class
		    .getResourceAsStream("/myra-git.properties"));

	    if (properties.containsKey("git.commit.id.describe")) {
		return String
			.format("[%s]",
				properties
					.getProperty("git.commit.id.describe"));

	    }
	} catch (Exception e) {
	    // silently ingored
	}

	return "[NO-GIT]";
    }
    
    
    // get similarity function
    
    /**
     * @param r1
     * @param r2
     * @return
     * @throws IOException 
     */
    public double getSimilarity(Rule r1, Rule r2) throws IOException{
    	
    	
    	
        int intersectionNumber=0;
       
        if (r1.terms().length>=r2.terms().length){
        for (int k = 0; k <r1.terms().length ; k++) {
            for (int j = 0; j <r2.terms().length ; j++) {
                  if (((r2.terms()[j]).condition().attribute==((r1.terms()[j]).condition().attribute))
                          &&((r2.terms()[j]).condition().relation==((r1.terms()[k]).condition().relation))
                          &&((r2.terms()[j]).condition().value[0]==((r1.terms()[k]).condition().value[0]))
                          &&((r2.terms()[j]).condition().value[1]==((r1.terms()[k]).condition().value[1]))
                          ){
                       intersectionNumber++;
                  } } } }
        
        else    if (r1.terms().length<r2.terms().length){
        for (int k = 0; k <r1.terms().length ; k++) {
            for (int j = 0; j <r2.terms().length ; j++) {
                  if (((r1.terms()[k]).condition().attribute==((r2.terms()[j]).condition().attribute))
                          &&((r1.terms()[k]).condition().relation==((r2.terms()[j]).condition().relation))
                          &&((r1.terms()[k]).condition().value[0]==((r2.terms()[j]).condition().value[0]))
                          &&((r1.terms()[k]).condition().value[1]==((r2.terms()[j]).condition().value[1]))
                          ){
                       intersectionNumber++;
                 }}
            } 
        }
    
    
    //	double sim =(1/new Double(r1.terms().length))*new Double(intersectionNumber);
    	
    	double sim =(new Double(intersectionNumber)/new Double(r1.terms().length));

    	return sim;
    }
   
    // the new equation for measuring the quality of the dataset2
       
    public Model incremental_result(ArrayList<Rule> rulesnew,ArrayList<Rule> rulesold,ArrayList<Rule>ruleemptyArrayList) throws Exception {
            double similarity;
            double quality;
            double w=0.5;
            RuleList discovered1 = new RuleList();
            Rule ruleo,rulen;
            
            ARFFReader reader = new ARFFReader();
            Dataset dataset = reader.read(CONFIG.get(TRAINING_FILE));
    
            ARFFReader reader2 = new ARFFReader() ;
            Dataset dataset2 = reader2.read(CONFIG.get(INCREMENTAL_FILE)); 
            
          //  int sizeRules=Math.min(rulesold.size(),rulesnew.size());
          //  for (int i = 0; i < sizeRules; i++) { 
          //  System.out.println("rulesnew.size()="+rulesnew.size());
          //  System.out.println("rulesold.size()="+rulesold.size());
   
             for (int i = 0; i < rulesnew.size(); i++) {
                 for (int h = 0; h < rulesold.size(); h++) {

                		ruleo=rulesold.get(h);
		                rulen=rulesnew.get(i); 
		         
		                Attribute target = dataset.attributes()[dataset.classIndex()];
		                Attribute target2 = dataset2.attributes()[dataset2.classIndex()];
		           //     if ( ruleo.getConsequent().toString() == rulen.getConsequent().toString()){
		                		similarity =  getSimilarity(rulen,ruleo) ;
		                /*		quality=(w*rulen.getQuality().adjusted())+(w*similarity);
		                		System.out.println("-----------Start------------");				                		  		                				                
				                System.out.println("Rule old is "+ruleo.toString(dataset));
				                System.out.println("Rule new is "+rulen.toString(dataset2));
				                System.out.println("Similarity = "+similarity);
				                System.out.println("Rule quality = "+quality);
				                System.out.println("-----------End------------"); */
				               
				               if ( similarity ==1 && rulen.terms().length > ruleo.terms().length  ){
				                	discovered1.add(rulen);
				                	System.out.println("Rule old is "+ruleo.toString(dataset));
					                System.out.println("Rule new is "+rulen.toString(dataset2));
					                //System.out.println(" The rule is "+rulen.toString(dataset2));
					                System.out.println("Rule quality = "+rulen.getQuality().adjusted());
				                }
				                else if (similarity ==1 && rulen.terms().length < ruleo.terms().length ){
				                	discovered1.add(ruleo);
				                	System.out.println("Rule old is "+ruleo.toString(dataset));
					                System.out.println("Rule new is "+rulen.toString(dataset2));
				                	// System.out.println("The rule is "+ruleo.toString(dataset));
					                System.out.println("Rule quality = "+ruleo.getQuality().adjusted());
				                }
				                else if (similarity ==1 && rulen.terms().length == ruleo.terms().length){
				                quality=(w*rulen.getQuality().adjusted())+(w*similarity);
				                System.out.println("Rule old is "+ruleo.toString(dataset));
				                System.out.println("Rule new is "+rulen.toString(dataset2));
				                //System.out.println("The rule is "+rulen.toString(dataset2));
				                System.out.println("the quality ="+quality);
				                discovered1.add(rulen);
				                }
				                if (similarity ==0) {
				                	 discovered1.add(rulen);
				                	 discovered1.add(ruleo);
				                	 
				                	 System.out.println("Rule old is "+ruleo.toString(dataset));
						                System.out.println("Rule new is "+rulen.toString(dataset2));
						                //System.out.println("The rule is "+ruleo.toString(dataset));
						                System.out.println("Rule quality = "+ruleo.getQuality().adjusted());
						                System.out.println("The rule is "+rulen.toString(dataset2));
						                System.out.println("Rule quality = "+ruleo.getQuality().adjusted());
				                }
		              
				                //  if (quality>=0.5) 
				                 //Cost cost=new Cost.Minimise(quality);
				               // rulen.setQuality(cost);		                            
		                     //   discovered1.add(rulen);		                	 
		             //  }  
                 }}            
            discovered1.add(ruleemptyArrayList.get(0));
			return discovered1;}
       
    public  Model incrementalModel (RuleList incrementalRule) throws Exception{
    	return incrementalRule;
    }
    public  Model incrementalMod (ArrayList incrementalRule) throws Exception{
    	RuleList discovered1 =  new RuleList(); 
    	for (int i =0; i < incrementalRule.size(); i++ ){
    		Rule rule = (Rule) incrementalRule.get(i);
    		discovered1.add(rule);
    	}
    		
    	return  discovered1;
    }
    public  Model arrayModel (ArrayList<Rule>  incrementalRule) throws Exception{
    	RuleList discovered1 = new RuleList();
    	for (int z=0; z<incrementalRule.size(); z++) {
    		if (incrementalRule.get(z+1) != incrementalRule.get(z)){
    		    
    			discovered1.add(incrementalRule.get(z));
    	 }}
    	return discovered1;
    }
    public Model incremental(ArrayList<Rule> rulesnew,ArrayList<Rule> rulesold) throws Exception {
        double similarity;
        double quality;
        double w=0.5;
        RuleList discovered1 = new RuleList();
        Rule ruleo,rulen;
        
        ARFFReader reader = new ARFFReader();
        Dataset dataset = reader.read(CONFIG.get(TRAINING_FILE));
        ARFFReader reader2 = new ARFFReader() ;
        Dataset dataset2 = reader2.read(CONFIG.get(INCREMENTAL_FILE)); 
        
         for (int i = 0; i < rulesnew.size(); i++) {
             for (int h = 0; h < rulesold.size(); h++) {
            		ruleo=rulesold.get(h);
	                rulen=rulesnew.get(i); 	         
	                Attribute target = dataset.attributes()[dataset.classIndex()];
	                Attribute target2 = dataset2.attributes()[dataset2.classIndex()];
	                if ( ruleo.getConsequent().toString(target) == rulen.getConsequent().toString(target2)){
	  		                
	                		System.out.println("-----------Start------------");			                
	  		                similarity =  getSimilarity(rulen,ruleo) ;			                
			                System.out.println("Rule old is "+ruleo.toString(dataset));
			                System.out.println(" Rule new is "+rulen.toString(dataset2));
			                System.out.println("Similarity = "+similarity);
			                quality=(w*rulen.getQuality().adjusted())+(w*similarity);
			                System.out.println("Rule quality = "+quality);
			                System.out.println("-----------End------------");
			               
			           /*     if ( similarity ==1 && rulen.terms().length > ruleo.terms().length){
			                	discovered1.add(rulen);
			                	System.out.println(" The rule is "+rulen.toString(dataset2));
				                System.out.println("Rule quality = "+rulen.getQuality().adjusted());
			                }
			                else if (similarity ==1 && rulen.terms().length < ruleo.terms().length){
			                	discovered1.add(ruleo);
				                System.out.println("The rule is "+ruleo.toString(dataset));
				                System.out.println("Rule quality = "+ruleo.getQuality().adjusted());
			                }
			                else if (similarity ==1 && rulen.terms().length == ruleo.terms().length){
			                quality=(w*rulen.getQuality().adjusted())+(w*similarity);
			                System.out.println("The rule is "+rulen.toString(dataset2));
			                System.out.println("the quality ="+quality);
			                discovered1.add(rulen);
			                }
			                if (similarity ==0) {
			                	 discovered1.add(rulen);
			                	 discovered1.add(ruleo);
			                	 System.out.println("The rule is "+ruleo.toString(dataset));
					                System.out.println("Rule quality = "+ruleo.getQuality().adjusted());
					                System.out.println("The rule is "+rulen.toString(dataset2));
					                System.out.println("Rule quality = "+ruleo.getQuality().adjusted());
			                } */
	              
			                //  if (quality>=0.5) 
			                 Cost cost=new Cost.Minimise(quality);
			                rulen.setQuality(cost);
	                            
	                        discovered1.add(rulen);
	                	 
            
          	}  	} }
		return discovered1;}
        
      //  discovered1.add(ruleemptyArrayList.get(0));

	
   

           
       /*         if (ruleo.terms().length>=rulen.terms().length){
                for (int k = 0; k <rulen.terms().length ; k++) {
                    for (int j = 0; j <ruleo.terms().length ; j++) {
                          if (((rulen.terms()[k]).condition().attribute==((ruleo.terms()[j]).condition().attribute))
                                  &&((rulen.terms()[k]).condition().relation==((ruleo.terms()[j]).condition().relation))
                                  &&((rulen.terms()[k]).condition().value[0]==((ruleo.terms()[j]).condition().value[0]))
                                  &&((rulen.terms()[k]).condition().value[1]==((ruleo.terms()[j]).condition().value[1]))
                                  ){
                               intersectionNumber++;
                          } } } }
                     
               else    if (ruleo.terms().length<rulen.terms().length){
                for (int k = 0; k <ruleo.terms().length ; k++) {
                    for (int j = 0; j <rulen.terms().length ; j++) {
                          if (((ruleo.terms()[k]).condition().attribute==((rulen.terms()[j]).condition().attribute))
                                  &&((ruleo.terms()[k]).condition().relation==((rulen.terms()[j]).condition().relation))
                                  &&((ruleo.terms()[k]).condition().value[0]==((rulen.terms()[j]).condition().value[0]))
                                  &&((ruleo.terms()[k]).condition().value[1]==((rulen.terms()[j]).condition().value[1]))
                                  ){
                               intersectionNumber++;
                          } } } }
                       	 
                similarity=(1/new Double(rulen.terms().length))*new Double(intersectionNumber);
               System.out.println("similarity---------------->"+similarity);
                quality=(w*ruleo.getQuality().adjusted())+(w*similarity);
                                System.out.println("quality---------------->"+quality);
                               Cost cost=new Cost.Minimise(quality);
                                 rulen.setQuality(cost);
                                        discovered.add(rulen);
                
                            System.out.println("Rule quality  "+quality) ;
   }

        
            discovered.add(ruleemptyArrayList.get(0));
                            return discovered;  } */
       

    private String[] split(String line) {
	String[] words = new String[0];
	int index = 0;

	while (index < line.length()) {
	    StringBuffer word = new StringBuffer();

	    boolean copying = false;
	    boolean quotes = false;
	    boolean brackets = false;

	    int i = index;

	    for (; i < line.length(); i++) {
		char c = line.charAt(i);

		if (!copying && !Character.isWhitespace(c)) {
		    copying = true;
		}

		if (c == '"' || c == '\'') {
		    quotes ^= true;
		} else if (c == '{' || c == '}') {
		    brackets ^= true;
		}

		if (copying) {
		    if (Character.isWhitespace(c) && !quotes && !brackets) {
			index = i + 1;
			break;
		    }

		    word.append(c);

		    // if (!(c == '"' || c == '\''))
		    // {
		    // word.append(c);
		    // }
		}
	    }

	    if (i >= line.length()) {
		// we reached the end of the line, need to stop the while loop
		index = i;
	    }

	    if (word.length() > 0) {
		words = Arrays.copyOf(words, words.length + 1);
		words[words.length - 1] = word.toString();
	    }
	}

	return words;
    }


    private boolean isComment(String line) {
	if (line.startsWith("%") || line.startsWith("#")) {
	    return true;
	}

	return false;
    }
 // read the dataset 
    /**
     * @param inputPath
     * @param p1
     * @param p2
     * @param p3
     * @throws IOException
     */
    public void read(String  inputPath ,double p1,double p2,double p3) throws IOException {
        FileReader fileReader =new FileReader(inputPath);
	BufferedReader reader = new BufferedReader(fileReader);
                int totalLinenumber=0;
	String line = null;
 // loop to read all the lines in the dataset
	while ((line = reader.readLine()) != null) {
          totalLinenumber++;
       }
	reader.close();
	// printing the dataset path after partitioning
	// PrintWriter :Prints formatted representations of objects to a text-output stream
PrintWriter writer = new PrintWriter(new File(inputPath+"1"));
writer.print("");
writer.close();
writer = new PrintWriter(new File(inputPath+"2"));
writer.print("");
writer.close();
writer = new PrintWriter(new File(inputPath+"3"));
writer.print("");
writer.close();
        FileWriter fstream = new FileWriter(inputPath+"1", true);
        BufferedWriter out = new BufferedWriter(fstream);
            FileWriter fstream2 = new FileWriter(inputPath+"2", true);
        BufferedWriter out2 = new BufferedWriter(fstream2);
    FileWriter fstream3 = new FileWriter(inputPath+"3", true);
        BufferedWriter out3 = new BufferedWriter(fstream3);

            fileReader =new FileReader(inputPath);

         reader = new BufferedReader(fileReader);
      
        line = null;
 double cusor=0;
 double part1=0;
 double part2=0;
 double part3=0;

	while ((line = reader.readLine()) != null) {
            cusor++;
            if (line!=null&&!line.equals("")){
	    String[] split = split(line);
            		split[0] = split[0].toLowerCase();
if (part1==0){
                    out.write(line);
                    out.newLine();
                    out2.write(line);
                    out2.newLine(); 
                    out3.write(line);
                    out3.newLine();
}
		// are we dealing with an attribute?
// start with the first line in data
		 if (split[0].startsWith(DATA)) {
 // cuser = 0
			 // equation to split the dataset without redundant using records
                 double startdata=totalLinenumber-cusor;
                 part1= (cusor+(startdata*p1));
                 part2= ((startdata*p2)+part1);
                 part3=((startdata*p3)+part2);
                
                 } 
                 else {
                     if (cusor<part1){
                      out.write(line);
                    out.newLine();
                     }else if (cusor<part2){
                       out2.write(line);
                    out2.newLine();
                     
                     }
                     else if (cusor<part3){
                       out3.write(line);
                    out3.newLine();
                     
                     }
                 }
                 
	    }
	}

	reader.close();
	out.close();
	out2.close();
	out3.close();

    }

        
        
        
        
}