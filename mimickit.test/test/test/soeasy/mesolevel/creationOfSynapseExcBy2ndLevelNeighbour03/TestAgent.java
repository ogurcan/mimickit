package test.soeasy.mesolevel.creationOfSynapseExcBy2ndLevelNeighbour03;

import static org.junit.Assert.assertEquals;
import mimickit.model.SOEASYEnvironment;
import mimickit.model.neuron.InnervatedNeuron;
import mimickit.model.neuron.Neuron;
import rast.AbstractTestAgent;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * 
 * @author Önder Gürcan
 * 
 */
public class TestAgent extends AbstractTestAgent {

	// agents under test
	private InnervatedNeuron nI;
	private Neuron n1, n2, n3, n4, n5;

	/** INITIALIZATION METHODS **/

	public TestAgent() {
		SOEASYEnvironment agentFactory = SOEASYEnvironment.getInstance();
		this.nI = (InnervatedNeuron) agentFactory
				.getAgent(ScenarioBuilder.NEURON_I);
		this.n1 = (Neuron) agentFactory.getAgent(ScenarioBuilder.NEURON_1);
		this.n2 = (Neuron) agentFactory.getAgent(ScenarioBuilder.NEURON_2);
		this.n3 = (Neuron) agentFactory.getAgent(ScenarioBuilder.NEURON_3);
		this.n4 = (Neuron) agentFactory.getAgent(ScenarioBuilder.NEURON_4);
		this.n5 = (Neuron) agentFactory.getAgent(ScenarioBuilder.NEURON_5);
	}

	@ScheduledMethod(start = 1, interval = 100, priority = ScheduleParameters.LAST_PRIORITY)
	public void triggerExcitationOfNeuron1() {
		nI.innervate();
	}

	/** TEST METHODS **/

	/**
	 * Initially, there is only one real link from Neuron-1 towards Neuron-2.
	 */
	@ScheduledMethod(start = 1, interval = 0, priority = ScheduleParameters.LAST_PRIORITY)
	public void testInitialCondition() {
		// N1
		assertEquals(1, n1.getPreSynapses().size());
		assertEquals(3, n1.getPostSynapses().size());
		// N2
		assertEquals(1, n2.getPreSynapses().size());
		assertEquals(0, n2.getPostSynapses().size());
		// N3
		assertEquals(1, n3.getPreSynapses().size());
		assertEquals(1, n3.getPostSynapses().size());
		// N4
		assertEquals(1, n4.getPreSynapses().size());
		assertEquals(1, n4.getPostSynapses().size());
		// N5
		assertEquals(2, n5.getPreSynapses().size());
		assertEquals(0, n5.getPostSynapses().size());
	}

	/**
	 * After a long time, all pre-synaptic neurons and axons must be tuned, so
	 * that the post-synaptic neuron can excite.
	 */
	@ScheduledMethod(start = ScheduleParameters.END, interval = 0, priority = ScheduleParameters.LAST_PRIORITY)
	public void testCreationOfANewSynapse() {
		int numberOfNeurons = SOEASYEnvironment.getInstance()
				.getNumberOfNeuronAgents();
		assertEquals(6, numberOfNeurons);
		//
		// N1
		assertEquals(1, n1.getPreSynapses().size());
		assertEquals(3, n1.getPostSynapses().size());
		// N2
		assertEquals(1, n2.getPreSynapses().size());
		assertEquals(1, n2.getPostSynapses().size());
		// N3
		assertEquals(1, n3.getPreSynapses().size());
		assertEquals(1, n3.getPostSynapses().size());
		// N4
		assertEquals(1, n4.getPreSynapses().size());
		assertEquals(1, n4.getPostSynapses().size());
		// N5
		assertEquals(3, n5.getPreSynapses().size());
		assertEquals(0, n5.getPostSynapses().size());
	}

}
