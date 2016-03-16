package test.soeasy.mesolevel.creationOfSynapseExcBy2ndLevelNeighbour03;

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
		executeTestScenario(getScenarioDir() + "parameters.txt", 90000);		
	}
	
}
