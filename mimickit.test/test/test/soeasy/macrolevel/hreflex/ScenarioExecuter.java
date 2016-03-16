package test.soeasy.macrolevel.hreflex;

import rast.core.AbstractScenarioExecuter;

public class ScenarioExecuter extends AbstractScenarioExecuter {

	public void runTest() throws Exception {
		executeTestScenario(getScenarioDir() + "parameters.txt", 7000001);		
	}
	
}
