package test.soeasy.mesolevel.scenario10;

import rast.AbstractScenarioExecuter;


/**
 * The aim of this test is to test handling of LowDepolarizationNCS when there
 * are more than one presynaptic neurons.
 * 
 * 
 * @author Önder Gürcan
 * 
 */
public class ScenarioExecuter extends AbstractScenarioExecuter {

	public void runTest() throws Exception {
		executeTestScenario(1000001);
	}
	
}
