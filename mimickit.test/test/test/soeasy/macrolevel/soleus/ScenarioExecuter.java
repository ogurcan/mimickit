package test.soeasy.macrolevel.soleus;

import rast.AbstractScenarioExecuter;

public class ScenarioExecuter extends AbstractScenarioExecuter {

	public void runTest() throws Exception {
		executeTestScenario(getScenarioDir() + "parameters.txt", 1510000001);		
	}	
}