package test.soeasy.mesolevel.creationOfSynapseExcBy2ndLevelNeighbour01;

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

	// agents to be tested
	private InnervatedNeuron neuronI;
	private Neuron neuron1, neuron2, neuron3;

	/** INITIALIZATION METHODS **/

	public TestAgent() {
		SOEASYEnvironment agentFactory = SOEASYEnvironment.getInstance();
		this.neuronI = (InnervatedNeuron) agentFactory
				.getAgent(ScenarioBuilder.NEURON_I);
		this.neuron1 = (Neuron) agentFactory.getAgent(ScenarioBuilder.NEURON_1);
		this.neuron2 = (Neuron) agentFactory.getAgent(ScenarioBuilder.NEURON_2);
		this.neuron3 = (Neuron) agentFactory.getAgent(ScenarioBuilder.NEURON_3);
	}

	/** TEST METHODS **/

	/**
	 * Initially, there is only one real link from Neuron-1 towards Neuron-2.
	 */
	@ScheduledMethod(start = 1, interval = 0, priority = ScheduleParameters.LAST_PRIORITY)
	public void testInitialCondition() {
		// Neuron-1
		assertEquals(1, neuron1.getPreSynapses().size());
		assertEquals(1, neuron1.getPostSynapses().size());
		// Neuron-2
		assertEquals(1, neuron2.getPreSynapses().size());
		assertEquals(1, neuron2.getPostSynapses().size());
		// Neuron-3
		assertEquals(1, neuron3.getPreSynapses().size());
		assertEquals(0, neuron3.getPostSynapses().size());
	}

	/**
	 * Conduct a spike at 5. Since the axonal delay is 5 ms, a potential
	 * difference must occur at tick 10.
	 */
	@ScheduledMethod(start = 5, interval = 100, priority = ScheduleParameters.LAST_PRIORITY)
	public void conductionStart() {
		neuronI.innervate();
	}

	/**
	 * After a long time, all pre-synaptic neurons and axons must be tuned, so
	 * that the post-synaptic neuron can excite.
	 */
	@ScheduledMethod(start = ScheduleParameters.END, interval = 0, priority = ScheduleParameters.LAST_PRIORITY)
	public void testCreationOfANewSynapse() {
		int numberOfNeurons = SOEASYEnvironment.getInstance()
				.getNumberOfNeuronAgents();
		assertEquals(4, numberOfNeurons);
		//
		/** Neuron-1 **/
		assertEquals(1, neuron1.getPreSynapses().size());
		assertEquals(2, neuron1.getPostSynapses().size());
		//
		/** Neuron-2 **/
		assertEquals(1, neuron2.getPreSynapses().size());
		assertEquals(1, neuron2.getPostSynapses().size());
		//
		/** Neuron-3 **/
		assertEquals(2, neuron3.getPreSynapses().size());
		assertEquals(0, neuron3.getPostSynapses().size());
	}

}
