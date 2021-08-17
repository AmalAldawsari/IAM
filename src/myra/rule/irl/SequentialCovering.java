/*
 * SequentialCovering.java
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

package myra.rule.irl;

import static myra.Config.CONFIG;
import static myra.datamining.Dataset.NOT_COVERED;
import static myra.rule.Assignator.ASSIGNATOR;
import myra.Config.ConfigKey;
import myra.datamining.Dataset;
import myra.datamining.Model;
import myra.datamining.Dataset.Instance;
import myra.Scheduler;
import myra.rule.Graph;
import myra.rule.Rule;
import static myra.rule.Rule.DEFAULT_RULE;
import myra.rule.RuleList;

/**
 * This class represents a trditional sequential covering strategy to create a
 * list of rules.
 * 
 * @author Fernando Esteban Barril Otero
 */
public class SequentialCovering {
    /**
     * The config key for the number of uncovered instances.
     */
    public final static ConfigKey<Integer> UNCOVERED = new ConfigKey<Integer>();
    
    public Model train(Dataset dataset) {
	final int uncovered = CONFIG.get(UNCOVERED);
	Instance[] instances = Instance.newArray(dataset.size());
	Instance.markAll(instances, NOT_COVERED);
	Graph graph = new Graph(dataset);
	RuleList discovered = new RuleList();
	int available = dataset.size();

	Scheduler<Rule> scheduler = Scheduler.newInstance(1);

	while (available >= uncovered) {
	    FindRuleActivity activity =
		    new FindRuleActivity(graph, instances, dataset);

	    // discovers one rule using an ACO procedure

	    scheduler.setActivity(activity);
	    scheduler.run();

	    Rule best = activity.getBest();
	    best.apply(dataset, instances);

	    // adds the rule to the list
	    discovered.add(best);

	    // marks the instances covered by the current rule as
	    // COVERED, so they are not available for the next
	    // iterations
	    available = Dataset.markCovered(instances);
	}

	if (!discovered.hasDefault()) {
	    // adds a default rule to the list

	    if (available == 0) {
		Instance.markAll(instances, NOT_COVERED);
	    }

	    Rule rule = Rule.newInstance();
	    rule.apply(dataset, instances);
	    CONFIG.get(ASSIGNATOR).assign(dataset, rule, instances);
	    discovered.add(rule);
	}
     // System.out.println(CONFIG.get(DEFAULT_RULE));
     //   System.out.println("");
     //   System.out.println("We have "+discovered.rules().length+" Rules including the default one");
      //  System.out.println("Rule Quality:");

      /*  for (int i = 0; i < discovered.rules().length; i++) {
               Rule rule=discovered.rules()[i];
               if (rule.getQuality()!=null){
            	   System.out.println("Rule "+i+" ---> "+rule.getQuality().adjusted());
               }
        } */
	return discovered;
	
    }
    
    public Model train2(Dataset dataset) {
    	final int uncovered1 = CONFIG.get(UNCOVERED);
    	Instance[] instances1 = Instance.newArray(dataset.size());
    	Instance.markAll(instances1, NOT_COVERED);
    	Graph graph1 = new Graph(dataset);
    	RuleList discovered1 = new RuleList();
    	int available = dataset.size();

    	Scheduler<Rule> scheduler = Scheduler.newInstance(1);

    	while (available >= uncovered1) {
    	    FindRuleActivity activity1 =
    		    new FindRuleActivity(graph1, instances1, dataset);

    	    // discovers one rule using an ACO procedure

    	    scheduler.setActivity(activity1);
    	    scheduler.run();

    	    Rule best = activity1.getBest();
    	    best.apply(dataset, instances1);

    	    // adds the rule to the list
    	    discovered1.add(best);

    	    // marks the instances covered by the current rule as
    	    // COVERED, so they are not available for the next
    	    // iterations
    	    available = Dataset.markCovered(instances1);
    	}

    	if (!discovered1.hasDefault()) {
    	    // adds a default rule to the list

    	    if (available == 0) {
    		Instance.markAll(instances1, NOT_COVERED);
    	    }

    	    Rule rule = Rule.newInstance();
    	    rule.apply(dataset, instances1);
    	    CONFIG.get(ASSIGNATOR).assign(dataset, rule, instances1);
    	    discovered1.add(rule);
    	}
    	//System.out.println(CONFIG.get(DEFAULT_RULE));
        for (int i = 0; i < discovered1.rules().length; i++) {
            Rule rule=discovered1.rules()[i];
            if (rule.getQuality()!=null){
              //  System.out.println("RuleQuality:");
         	 //  System.out.println(i+"====="+rule.getQuality().adjusted());
            	rule.getQuality().adjusted();
            }}
        //     System.out.println(CONFIG.get(DEFAULT_RULE));
    	//     System.out.println(discovered.rules()[0].getQuality());
    	return discovered1;
        }
    	
    
    // Decision Tree
    public Model DT(Dataset dataset) {
    	final int uncovered = CONFIG.get(UNCOVERED);
    	Instance[] instances = Instance.newArray(dataset.size());
    	Instance.markAll(instances, NOT_COVERED);
    	Graph graph = new Graph(dataset);
    	RuleList discovered = new RuleList();
    	int available = dataset.size();

    	Scheduler<Rule> scheduler = Scheduler.newInstance(1);

    	while (available >= uncovered) {
    	    FindRuleActivity activity =
    		    new FindRuleActivity(graph, instances, dataset);

    	    // discovers one rule using an ACO procedure

    	    scheduler.setActivity(activity);
    	    scheduler.run();

    	    Rule best = activity.getBest();
    	    best.apply(dataset, instances);

    	    // adds the rule to the list
    	    discovered.add(best);

    	    // marks the instances covered by the current rule as
    	    // COVERED, so they are not available for the next
    	    // iterations
    	    available = Dataset.markCovered(instances);
    	}

    	if (!discovered.hasDefault()) {
    	    // adds a default rule to the list

    	    if (available == 0) {
    		Instance.markAll(instances, NOT_COVERED);
    	    }

    	    Rule rule = Rule.newInstance();
    	    rule.apply(dataset, instances);
    	    CONFIG.get(ASSIGNATOR).assign(dataset, rule, instances);
    	    discovered.add(rule);
    	}
            System.out.println(CONFIG.get(DEFAULT_RULE));
            for (int i = 0; i < discovered.rules().length; i++) {
                   Rule rule=discovered.rules()[i];
                   if (rule.getQuality()!=null){
                       System.out.println("RuleQuality:");
                	   System.out.println(i+"===="+rule.getQuality().adjusted());
                   }
            }
    	return discovered;
        }
 
    }
     
  
   