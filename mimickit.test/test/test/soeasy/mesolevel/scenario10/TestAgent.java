package test.soeasy.mesolevel.scenario10;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
	private Synapse synapse1;
	private InnervatedNeuron neuron2;
	private Synapse synapse2;
	private InnervatedNeuron neuron3;
	private Synapse synapse3;
	private Neuron neuronP;

	// monitored data
	private Vector<Double> neuronPSpikeVector;

	/** INITIALIZATION METHODS **/

	public TestAgent() {
		this.neuron1 = (InnervatedNeuron) SOEASYEnvironment.getInstance().getAgent(
				"Neuron-1");
		this.synapse1 = neuron1.getPostSynapses().get(0);
		this.neuron2 = (InnervatedNeuron) SOEASYEnvironment.getInstance().getAgent(
				"Neuron-2");
		this.synapse2 = neuron2.getPostSynapses().get(0);
		this.neuron3 = (InnervatedNeuron) SOEASYEnvironment.getInstance().getAgent(
				"Neuron-3");
		this.synapse3 = neuron3.getPostSynapses().get(0);
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
		neuron2.innervate();
		neuron3.innervate();
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
	@ScheduledMethod(start = 15, interval = 0, priority = ScheduleParameters.LAST_PRIORITY)
	public void testNonExcitationOfPostsynapticNeuron() {
		double strength1 = synapse1.getTotalSynapticStrength();
		double strength2 = synapse2.getTotalSynapticStrength();
		double strength3 = synapse3.getTotalSynapticStrength();
		double totalStrength = strength1 + strength2 + strength3;
		assertTrue(totalStrength < 0.70);
		// Neuron-P should be able to excite
		assertEquals(0, neuronPSpikeVector.size());
	}

	/**
	 * After a long time, all pre-synaptic neurons and synapses must be tuned,
	 * so that the post-synaptic neuron can excite.
	 */
	@ScheduledMethod(start = ScheduleParameters.END, interval = 0, priority = ScheduleParameters.LAST_PRIORITY)
	public void testTuningOfPresynapticNeurons() {
		double strength1 = synapse1.getTotalSynapticStrength();
		double strength2 = synapse2.getTotalSynapticStrength();
		double strength3 = synapse3.getTotalSynapticStrength();
		double totalStrength = strength1 + strength2 + strength3;
		//
		// total strength should be equal to or very close to 25.0
		assertEquals(0.70, totalStrength, 0.5);
		// Neuron-P should be able to excite
		assertTrue(neuronPSpikeVector.size() > 0);
	}

}
