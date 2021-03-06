package test.soeasy.mesolevel.creationOfSynapseExcBy2ndLevelNeighbour02;

import static org.junit.Assert.assertEquals;
import mimickit.model.SOEASYEnvironment;
import mimickit.model.neuron.InnervatedNeuron;
import mimickit.model.neuron.Neuron;
import rast.core.AbstractTestAgent;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * 
 * @author Önder Gürcan
 * 
 */
public class TestAgent extends AbstractTestAgent {

	// agents under test
	private InnervatedNeuron n1;
	private Neuron n2, n3, n4;

	/** INITIALIZATION METHODS **/

	public TestAgent() {
		this.n1 = (InnervatedNeuron) SOEASYEnvironment.getInstance().getAgent("N1");
		this.n2 = (Neuron) SOEASYEnvironment.getInstance().getAgent("N2");
		this.n3 = (Neuron) SOEASYEnvironment.getInstance().getAgent("N3");
		this.n4 = (Neuron) SOEASYEnvironment.getInstance().getAgent("N4");
	}

	@ScheduledMethod(start = 1, interval = 100, priority = ScheduleParameters.LAST_PRIORITY)
	public void triggerExcitationOfNeuron1() {
		n1.innervate();
	}

	/** TEST METHODS **/

	/**
	 * Initially, there is only one real link from Neuron-1 towards Neuron-2.
	 */
	@ScheduledMethod(start = 1, interval = 0, priority = ScheduleParameters.LAST_PRIORITY)
	public void testInitialCondition() {
		// N1
		assertEquals(0, n1.getPreSynapses().size());
		assertEquals(2, n1.getPostSynapses().size());
		// N2
		assertEquals(1, n2.getPreSynapses().size());
		assertEquals(1, n2.getPostSynapses().size());
		// N3
		assertEquals(1, n3.getPreSynapses().size());
		assertEquals(1, n3.getPostSynapses().size());
		// N4
		assertEquals(2, n4.getPreSynapses().size());
		assertEquals(0, n4.getPostSynapses().size());
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
		// N1
		assertEquals(0, n1.getPreSynapses().size());
		assertEquals(3, n1.getPostSynapses().size());
		// N2
		assertEquals(1, n2.getPreSynapses().size());
		assertEquals(1, n2.getPostSynapses().size());
		// N3
		assertEquals(1, n3.getPreSynapses().size());
		assertEquals(1, n3.getPostSynapses().size());
		// N4
		assertEquals(3, n4.getPreSynapses().size());
		assertEquals(0, n4.getPostSynapses().size());
	}

}
