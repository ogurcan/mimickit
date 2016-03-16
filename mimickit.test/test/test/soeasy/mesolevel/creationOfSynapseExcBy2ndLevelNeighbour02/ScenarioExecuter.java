package test.soeasy.mesolevel.creationOfSynapseExcBy2ndLevelNeighbour02;

import mimickit.util.SimulationLogger;

import org.apache.log4j.FileAppender;
import org.apache.log4j.PatternLayout;

import rast.core.AbstractScenarioExecuter;


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
		FileAppender appender = new FileAppender(new PatternLayout(), getDataDir() + "deneme.log");
		SimulationLogger.getInstance().addAppender(appender);
		executeTestScenario(getScenarioDir() + "parameters.txt", 90000);					
	}
	
}
