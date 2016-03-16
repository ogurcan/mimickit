package test.soeasy.macrolevel.tbialisanterior;

import rast.core.AbstractScenarioExecuter;

public class ScenarioExecuter extends AbstractScenarioExecuter {
	
	public void runTest() throws Exception {
		executeTestScenario(getScenarioDir() + "parameters.txt", 6000001);		
	}
	
}
