package test.soeasy.mesolevel.creationOfSynapseExcBy2ndLevelNeighbour0x;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Vector;

import mimickit.model.SOEASYEnvironment;
import mimickit.model.SOEASYParameters;
import mimickit.model.Spike;
import mimickit.model.neuron.InnervatedNeuron;
import mimickit.model.neuron.Neuron;
import mimickit.model.neuron.Synapse;
import rast.AbstractTestAgent;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;

/**
 * 
 * @author Önder Gürcan
 * 
 */
public class TestAgent extends AbstractTestAgent {

	// agents to be tested
	private InnervatedNeuron neuron1;
	private Neuron neuronP;

	// monitored data
	private Vector<Double> neuronPSpikeVector;

	/** INITIALIZATION METHODS **/

	public TestAgent() {
		this.neuron1 = (InnervatedNeuron) SOEASYEnvironment.getInstance().getAgent(
				"Neuron-1");
		this.neuronP = (Neuron) SOEASYEnvironment.getInstance().getAgent("Neuron-P");
		this.neuronPSpikeVector = new Vector<Double>();
	}

	/** BEHAVIOR TRIGGERING METHODS **/

	/**
	 * At every 100 ms, excite neuron1, neuron2 and neuron3
	 */
	@ScheduledMethod(start = 1, interval = 100, priority = ScheduleParameters.LAST_PRIORITY)
	public void triggerExcitationOfNeuron1() {
		neuron1.innervate();
	}

	/** STATE MONITORING METHODS **/

	/**
	 * Collects "potential" information from Neuron-P.
	 */
	@Watch(watcheeClassName = "soeasy.Neuron", watcheeFieldNames = "potential", query = "colocated", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void monitorInterNeuronResponses(Neuron watcheeNeuron) {
		if (watcheeNeuron.getPotential() >= Spike.FIRING_THRESHOLD) {
			if (watcheeNeuron.equals(neuronP)) {
				double currentTime = SOEASYParameters.getInstance()
						.getCurrentTick();
				if (neuronPSpikeVector.size() == 0) {
					neuronPSpikeVector.add(currentTime);
				} else {
					double previousSpike = neuronPSpikeVector.lastElement();
					if ((currentTime - previousSpike) > Spike.SPIKE_DURATION) {
						neuronPSpikeVector.add(currentTime);
					}
				}
			}
		}
	}

	/** TEST METHODS **/

	/**
	 * Neuron-P must be unable to excite in the early phase..
	 */
	@ScheduledMethod(start = 50, interval = 0, priority = ScheduleParameters.LAST_PRIORITY)
	public void testNonExcitationOfPostsynapticNeuron() {
		// Neuron-P should be able to excite
		assertEquals(0, neuronPSpikeVector.size());
	}

	/**
	 * After a long time, all presynaptic neurons and axons must be tuned, so
	 * that the postsynaptic neuron can excite.
	 */
	@ScheduledMethod(start = ScheduleParameters.END, interval = 0, priority = ScheduleParameters.LAST_PRIORITY)
	public void testTuningOfPresynapticNeurons() {
		int numberOfNeurons = SOEASYEnvironment.getInstance()
				.getNumberOfNeuronAgents();
		assertEquals(7, numberOfNeurons);
		List<Synapse> preSynapses = neuronP.getPreSynapses();
		assertTrue(preSynapses.size() > 2);
		//
		// Neuron-P should be able to excite
		assertTrue(neuronPSpikeVector.size() > 0);
	}

}
