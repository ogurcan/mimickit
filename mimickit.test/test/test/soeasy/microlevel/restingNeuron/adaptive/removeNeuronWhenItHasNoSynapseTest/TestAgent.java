package test.soeasy.microlevel.restingNeuron.adaptive.removeNeuronWhenItHasNoSynapseTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import mimickit.model.SOEASYEnvironment;
import mimickit.model.neuron.RestingNeuron;
import rast.AbstractTestAgent;
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

	/** TEST METHODS **/

	@ScheduledMethod(start = 10, interval = 0, priority = ScheduleParameters.LAST_PRIORITY)
	public void testExistenceInTheBeginning() {
		SOEASYEnvironment agentFactory = SOEASYEnvironment.getInstance();
		RestingNeuron restingNeuron = (RestingNeuron) agentFactory
				.getAgent("RestingNeuron");
		int numberOfNeuronAgents = agentFactory.getNumberOfNeuronAgents();
		// the resting neuron should not be alive
		assertNotNull(restingNeuron);
		assertEquals(1, numberOfNeuronAgents);
	}

	@ScheduledMethod(start = ScheduleParameters.END, interval = 0, priority = ScheduleParameters.LAST_PRIORITY)
	public void testExistence() {
		SOEASYEnvironment agentFactory = SOEASYEnvironment.getInstance();
		RestingNeuron restingNeuron = (RestingNeuron) agentFactory
				.getAgent("RestingNeuron");		
		// the resting neuron should not be alive
		assertNull(restingNeuron);
		
		int numberOfNeuronAgents = agentFactory.getNumberOfNeuronAgents();
		assertEquals(0, numberOfNeuronAgents);
	}
}
