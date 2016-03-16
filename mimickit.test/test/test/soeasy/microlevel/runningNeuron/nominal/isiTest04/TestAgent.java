package test.soeasy.microlevel.runningNeuron.nominal.isiTest04;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import mimickit.model.SOEASYEnvironment;
import mimickit.model.neuron.Neuron;
import mimickit.model.neuron.RestingNeuron;
import mimickit.model.neuron.RunningNeuron;
import mimickit.model.neuron.SpikeObserver;
import mimickit.util.DoubleArrayList;
import mimickit.util.ISIGenerator;
import rast.AbstractTestAgent;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * The aim of this test is three-fold: (1) to test the activity of the
 * motorneuron itself, (2) to test whether the axon is working fine or not, and
 * (3) to test the behavior of the muscle.
 * 
 * @author Önder Gürcan
 * 
 */
public class TestAgent extends AbstractTestAgent implements SpikeObserver {

	// functional agents
	RunningNeuron motorNeuron;
	Neuron muscle;

	private DoubleArrayList motorNeuronSpikeVector;
	private DoubleArrayList muscleSpikeVector;

	/** INITIALIZATION METHOD **/

	public TestAgent() {
		this.motorNeuron = (RunningNeuron) SOEASYEnvironment.getInstance().getAgent(
				"Motoneuron");
		this.motorNeuron.addSpikeObserver(this);

		this.muscle = (RestingNeuron) SOEASYEnvironment.getInstance().getAgent(
				"Muscle");
		this.muscle.addSpikeObserver(this);
		
		this.motorNeuronSpikeVector = new DoubleArrayList();
		this.muscleSpikeVector = new DoubleArrayList();
	}

	/** TEST METHODS **/

	/**
	 * This method tests the ISI distribution of motor neuron. Theoretically the
	 * statistical distribution of simulated spike train should be similar to
	 * the statistical distribution of experimental spike train.
	 */
	@ScheduledMethod(start = ScheduleParameters.END, priority = ScheduleParameters.LAST_PRIORITY)
	public void testInterspikeIntervalDistributionOfMotorNeuron() {
		// motorneuron has to generate some spikes.
		assertTrue(motorNeuronSpikeVector.size() > 0);

		// calculate interspike intervals
		DoubleArrayList isiVector = ISIGenerator
				.calculateInterspikeInterval(motorNeuronSpikeVector);

		assertTrue(isiVector.size() > 0);
		for (int i = 0; i < isiVector.size(); i++) {
			assertEquals(100.0, isiVector.get(i), 1.0);
		}
	}
	
	@Override
	public void update(Neuron spikingNeuron, double spikeTime) {
		if (spikingNeuron.equals(muscle)) {
			this.muscleSpikeVector.add(spikeTime);
		} else if (spikingNeuron.equals(motorNeuron)) {
			this.motorNeuronSpikeVector.add(spikeTime);
		}
	}
}
