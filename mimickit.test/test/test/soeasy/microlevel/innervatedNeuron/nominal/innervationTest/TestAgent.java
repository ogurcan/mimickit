package test.soeasy.microlevel.innervatedNeuron.nominal.innervationTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Vector;

import mimickit.model.SOEASYEnvironment;
import mimickit.model.neuron.InnervatedNeuron;
import mimickit.model.neuron.Neuron;
import mimickit.model.neuron.RunningNeuron;
import mimickit.model.neuron.SpikeObserver;
import rast.AbstractTestAgent;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * The aim of this test is three-fold: (1) to test the periodic innervation of
 * sensory neuron via wiring viewer, (2) to test whether there is a blind time
 * space in data caused by extracellular fluid or not, and (3) to test both the
 * excitatory and inhibitory effect of sensory neuron on motor neuron by
 * checking H-Reflex in PSTH.
 * 
 * @author Önder Gürcan
 * 
 */
public class TestAgent extends AbstractTestAgent implements SpikeObserver {

	private InnervatedNeuron sensoryNeuron;
	private RunningNeuron motorNeuron;

	private Vector<Double> sensoryNeuronSpikeVector;

	/** INITIALIZATION METHOD **/

	public TestAgent() {
		this.sensoryNeuron = (InnervatedNeuron) SOEASYEnvironment.getInstance()
				.getAgent("SensoryNeuron");
		this.sensoryNeuron.addSpikeObserver(this);
		
		this.motorNeuron = (RunningNeuron) SOEASYEnvironment.getInstance().getAgent(
				"MotorNeuron");
		//
		this.sensoryNeuronSpikeVector = new Vector<Double>();
	}

	/** TEST METHODS **/

	/**
	 * This method tests the spikes occured at sensory neuron caused by sensory
	 * neuron viewer with a normal period of 1000 to 3000 ms. Theoretically, all
	 * the interspike intervals of the sensory neuron should be between 1000 and
	 * 3000 ms.
	 */
	@ScheduledMethod(start = 1000000, interval = 0, priority = ScheduleParameters.LAST_PRIORITY)
	public void testPeriodicInnervationOfSensoryNeuronViaWiringViewer() {
		assertTrue(sensoryNeuronSpikeVector.size() > 0);
		// calculate interspike intervals
		Vector<Double> isiVector = motorNeuron.getIsiDistribution()
				.calculateInterspikeInterval(sensoryNeuronSpikeVector, 1000,
						3000);
		assertNotNull(isiVector);
		// all intervals must be between 1000 and 3000.
		assertEquals(sensoryNeuronSpikeVector.size() - 1, isiVector.size());
	}

	@Override
	public void update(Neuron spikingNeuron, double spikeTime) {
		sensoryNeuronSpikeVector.add(spikeTime);
	}

}
