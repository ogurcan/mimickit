package test.soeasy.microlevel.restingNeuron.adaptive.removeNeuronWhenItIsNotActiveTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import mimickit.model.SOEASYEnvironment;
import mimickit.model.SOEASYParameters;
import mimickit.model.neuron.RestingNeuron;
import rast.core.AbstractTestAgent;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * 
 * @author Önder Gürcan
 * 
 */
public class TestAgent extends AbstractTestAgent {

	public TestAgent() {

	}

	@ScheduledMethod(start = 10, interval = 10, priority = ScheduleParameters.LAST_PRIORITY)
	public void testExistence() {
		SOEASYEnvironment agentFactory = SOEASYEnvironment.getInstance();
		double currentTick = SOEASYParameters.getInstance()
				.getCurrentTick();
		RestingNeuron restingNeuron = (RestingNeuron) agentFactory
				.getAgent("RestingNeuron");
		int numberOfNeuronAgents = agentFactory.getNumberOfNeuronAgents();
		if (currentTick <= 60000) {
			// the neuron should be alive
			assertNotNull(restingNeuron);
			assertEquals(1, numberOfNeuronAgents);
		} else {
			// the neuron should not be alive
			assertNull(restingNeuron);
			assertEquals(0, numberOfNeuronAgents);
		}
	}

}
