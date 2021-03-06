package test.soeasy.microlevel.runningNeuron.nominal.isiTest03;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import mimickit.model.SOEASYEnvironment;
import mimickit.model.neuron.Neuron;
import mimickit.model.neuron.RestingNeuron;
import mimickit.model.neuron.RunningNeuron;
import mimickit.model.neuron.SpikeObserver;
import mimickit.util.CuSum;
import mimickit.util.DataFile;
import mimickit.util.DoubleArrayList;
import mimickit.util.ISIGenerator;
import mimickit.util.StaticPSF;
import rast.core.AbstractTestAgent;
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

	/**
	 * Acceptable difference between experimental and simulated frequency.
	 */
	private double frequencyDelta = 0.5;

	/**
	 * Starting times of actions.merc
	 * 
	 */
	private final double oneMillionOne = 3000001;

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

		DoubleArrayList motorResponseExp = new DoubleArrayList(
				DataFile.EXP_MOTONEURON_ONLY);
		DoubleArrayList isiVectorExp = ISIGenerator
				.calculateInterspikeInterval(motorResponseExp);

		int isiVectorExpSize = isiVectorExp.size();
		int isiVectorSimSize = isiVector.size();
		assertTrue(isiVectorSimSize > isiVectorExpSize);

		// i=1, because the first experimental ISI is used for initializing
		// the
		// running neuron
		for (int i = 1; i < isiVectorExpSize; i++) {
			// get the i'th experimental ISI
			double experimentalISI = isiVectorExp.get(i);
			// then get the corresponding simulated ISI
			double simulatedISI = isiVector.get(i - 1);
			// both values should be really close
			assertEquals(experimentalISI, simulatedISI, 1.5);
		}
	}

	/**
	 * This method tests the frequency of an unstimulated motor neuron.
	 * Theoretically the mean prestimulus of simulated motor activity frequency
	 * should be similar to the mean prestimulus of experimental motor activity
	 * frequency.
	 */
	@ScheduledMethod(start = ScheduleParameters.END, interval = 0, priority = ScheduleParameters.LAST_PRIORITY)
	public void testFrequencyOfUnstimulatedMotorNeuron() {
		/* first, calculate the PSF of experimental data */
		// read motor response data
		DoubleArrayList motorResponseExp = new DoubleArrayList(
				DataFile.EXP_MOTONEURON_ONLY);
		// calculate a hypothetical stimuli
		DoubleArrayList stimuliVectorExp = new DoubleArrayList();
		for (double stimulation = motorResponseExp.get(0) + 1000; stimulation < motorResponseExp
				.lastElement() - 1000; stimulation = stimulation + 1000) {
			stimuliVectorExp.add(stimulation);
		}
		StaticPSF psfExp = new StaticPSF(
				stimuliVectorExp, motorResponseExp, false);
		CuSum cuSumExp = new CuSum(psfExp, 1, 4.0);

		/* then, calculate the PSF of simulated data */
		// calculate PSF using hypotetical stimuli
		DoubleArrayList stimuliVector = new DoubleArrayList();
		for (double stimulation = 1000; stimulation < oneMillionOne; stimulation = stimulation + 1000) {
			stimuliVector.add(stimulation);
		}
		StaticPSF psf = new StaticPSF(
				stimuliVector, muscleSpikeVector, false);
		CuSum cuSumSim = new CuSum(psf, 1, 4.0);		

		// prestimulus means should be close
		assertEquals(cuSumExp.getPrestimulusMean(),
				cuSumSim.getPrestimulusMean(), frequencyDelta);

		// error-boxes should be similar
		assertEquals(cuSumExp.getErrorBoxUpperBound(),
				cuSumSim.getErrorBoxUpperBound(), 50.0);
		assertEquals(cuSumExp.getErrorBoxLowerBound(),
				cuSumSim.getErrorBoxLowerBound(), 50.0);
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
