package test.soeasy.microlevel.runningNeuron.nominal.isiTest01;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
import mimickit.util.RandomVariateGenFactory;
import mimickit.util.StaticPSF;
import rast.core.AbstractTestAgent;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import umontreal.iro.lecuyer.randvar.RandomVariateGen;

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
	RestingNeuron muscle;

	private DoubleArrayList motorNeuronSpikeVector;
	private DoubleArrayList muscleSpikeVector;

	/**
	 * Acceptable difference between experimental and simulated frequency.
	 */
	private final double frequencyDelta = 1.0;

	/**
	 * Starting times of actions.merc
	 * 
	 */
	private final double oneMillionOne = 3000001;
	private ISIGenerator isiDistribution;

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
		this.isiDistribution = motorNeuron.getIsiDistribution();
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
		RandomVariateGen randomGenSimulated = RandomVariateGenFactory
				.getGenerator(isiVector);
		assertNotNull(randomGenSimulated);
		RandomVariateGen randomGenExperimental = isiDistribution.getRandomGen();
		assertNotNull(randomGenExperimental);
		// distribution should be the same
		assertEquals(randomGenExperimental.getClass().getName(),
				randomGenSimulated.getClass().getName());

		// alpha parameters of the two distribution shoul be close
		double alphaExperimental = randomGenExperimental.getDistribution()
				.getParams()[0];
		double alphaSimulated = randomGenSimulated.getDistribution()
				.getParams()[0];
		assertEquals(alphaExperimental, alphaSimulated, 3.0);

		// gamma parameters of the two distribution shoul be close
		double gammaExperimental = randomGenExperimental.getDistribution()
				.getParams()[1];
		double gammaSimulated = randomGenSimulated.getDistribution()
				.getParams()[1];
		assertEquals(gammaExperimental, gammaSimulated, 0.3);
	}

	/**
	 * Tests the axonal delay of the axon between the motor neuron and the
	 * muscle. For each muscle response, axonal delay should be 10.0 (+1.0).
	 */
	@ScheduledMethod(start = ScheduleParameters.END, interval = 0, priority = ScheduleParameters.LAST_PRIORITY)
	public void testAxonalDelay() {
		// for each muscle response, axonal delay should be around 10.0
		for (int index = 0; index < muscleSpikeVector.size(); index++) {
			double axonalDelay = muscleSpikeVector.get(index)
					- motorNeuronSpikeVector.get(index);
			assertEquals(10.0, axonalDelay, 1.0);
		}
	}

	/**
	 * This method tests the spikes occurred at muscle caused by motor neuron
	 * activity. Theoretically the statistical distribution of simulated spike
	 * train should be similar to the statistical distribution of experimental
	 * spike train.
	 */
	@ScheduledMethod(start = ScheduleParameters.END, interval = 0, priority = ScheduleParameters.LAST_PRIORITY)
	public void testInterspikeIntervalDistributionOfMuscle() {
		assertTrue(muscleSpikeVector.size() > 0);
		muscleSpikeVector.writeToFile("./data/experimental/motoneuron_only_simulated.txt");
		// calculate interspike intervals
		DoubleArrayList isiVector = ISIGenerator
				.calculateInterspikeInterval(muscleSpikeVector);
		RandomVariateGen randomGenSimulated = RandomVariateGenFactory
				.getGenerator(isiVector);
		assertNotNull(randomGenSimulated);

		RandomVariateGen randomGenExperimental = isiDistribution.getRandomGen();
		assertNotNull(randomGenExperimental);

		// distribution should be the same
		assertEquals(randomGenExperimental.getClass().getName(),
				randomGenSimulated.getClass().getName());

		// alpha parameters of the two distribution shoul be close
		double alphaExperimental = randomGenExperimental.getDistribution()
				.getParams()[0];
		double alphaSimulated = randomGenSimulated.getDistribution()
				.getParams()[0];
		assertEquals(alphaExperimental, alphaSimulated, 3.0);

		// gamma parameters of the two distribution shoul be close
		double gammaExperimental = randomGenExperimental.getDistribution()
				.getParams()[1];
		double gammaSimulated = randomGenSimulated.getDistribution()
				.getParams()[1];
		assertEquals(gammaExperimental, gammaSimulated, 0.3);		
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
		double prestimulusMeanExp = cuSumExp.getPrestimulusMean();
		double prestimulusMeanSim = cuSumSim.getPrestimulusMean();
		assertEquals(prestimulusMeanExp, prestimulusMeanSim, frequencyDelta);

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
