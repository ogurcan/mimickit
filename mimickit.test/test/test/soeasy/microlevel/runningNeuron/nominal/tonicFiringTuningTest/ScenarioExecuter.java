package test.soeasy.microlevel.runningNeuron.nominal.tonicFiringTuningTest;

import rast.core.AbstractScenarioExecuter;


/**
 * The aim of this test is: to test the handling of
 * FInstantFrequency NCS by motorneuron.
 * 
 * Initially the motoneuron emit spikes according to a desired distribution
 * which generates high frequencies. The test agent periodically checks the
 * instant frequencies and sends the suitable feedback (FInstantFrequency,
 * INCREASE, DECREASE or GOOD) until the neuron emits spikes with desired
 * instant frequencies.
 *  
 * @author Önder Gürcan
 * 
 */
public class ScenarioExecuter extends AbstractScenarioExecuter {

	public void runTest() throws Exception {
		executeTestScenario(5000001);
	}
	
}
