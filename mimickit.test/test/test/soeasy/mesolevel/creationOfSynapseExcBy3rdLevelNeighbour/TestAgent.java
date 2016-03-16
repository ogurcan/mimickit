package test.soeasy.mesolevel.creationOfSynapseExcBy3rdLevelNeighbour;

import static org.junit.Assert.assertEquals;
import mimickit.model.SOEASYEnvironment;
import mimickit.model.neuron.InnervatedNeuron;
import mimickit.model.neuron.Neuron;
import mimickit.model.neuron.Synapse;
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
	private Neuron neuron1, neuron2, neuron3, neuron4, neuron5;

	/** INITIALIZATION METHODS **/

	public TestAgent() {
		this.neuronI = (InnervatedNeuron) SOEASYEnvironment.getInstance().getAgent(
				"Neuron-I");
		this.neuron1 = (Neuron) SOEASYEnvironment.getInstance().getAgent("Neuron-1");
		this.neuron2 = (Neuron) SOEASYEnvironment.getInstance().getAgent("Neuron-2");
		this.neuron3 = (Neuron) SOEASYEnvironment.getInstance().getAgent("Neuron-3");
		this.neuron4 = (Neuron) SOEASYEnvironment.getInstance().getAgent("Neuron-4");
		this.neuron5 = (Neuron) SOEASYEnvironment.getInstance().getAgent("Neuron-5");
	}

	/** TEST METHODS **/

	@ScheduledMethod(start = 1, interval = 100, priority = ScheduleParameters.LAST_PRIORITY)
	public void triggerExcitationOfNeuron1() {
		neuronI.innervate();
	}

	/**
	 * Initially, there is only one real link from Neuron-1 towards Neuron-2.
	 */
	@ScheduledMethod(start = 1, interval = 0, priority = ScheduleParameters.LAST_PRIORITY)
	public void testInitialCondition() {
		// Neuron-1
		assertEquals(1, neuron1.getPreSynapses().size());
		assertEquals(3, neuron1.getPostSynapses().size());
		// Neuron-2
		assertEquals(1, neuron2.getPreSynapses().size());
		assertEquals(1, neuron2.getPostSynapses().size());
		// Neuron-3
		assertEquals(1, neuron3.getPreSynapses().size());
		assertEquals(0, neuron3.getPostSynapses().size());
		// Neuron-4
		assertEquals(1, neuron4.getPreSynapses().size());
		assertEquals(0, neuron4.getPostSynapses().size());
		// Neuron-5
		assertEquals(1, neuron5.getPreSynapses().size());
		assertEquals(0, neuron5.getPostSynapses().size());
	}

	/**
	 * After a some time, a synapse from Neuron-3 to Neuron-5 should be created.
	 */
	@ScheduledMethod(start = ScheduleParameters.END, interval = 0, priority = ScheduleParameters.LAST_PRIORITY)
	public void testCreationOfANewSynapse() {
		// Neuron-3
		assertEquals(1, neuron3.getPostSynapses().size());
		Synapse synapse = neuron3.getPostSynapses().get(0);
		assertEquals(neuron5, synapse.getPostsynapticNeuron());
	}

}
