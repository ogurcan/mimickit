package test.soeasy.microlevel.runningNeuron.nominal.isiTest02;

import static mimickit.util.DataFile.EXP_SOLEUS_MNDISCHARGE;
import static mimickit.util.DataFile.EXP_SOLEUS_TRIGGER;
import static org.junit.Assert.assertEquals;
import mimickit.model.SOEASYEnvironment;
import mimickit.model.SOEASYParameters;
import mimickit.model.neuron.Neuron;
import mimickit.model.neuron.RestingNeuron;
import mimickit.model.neuron.RunningNeuron;
import mimickit.model.neuron.SpikeObserver;
import mimickit.util.CuSum;
import mimickit.util.DoubleArrayList;
import mimickit.util.StaticPSF;
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
	RestingNeuron muscle;

	private DoubleArrayList motorNeuronSpikeVector;
	private DoubleArrayList muscleSpikeVector;

	/**
	 * Acceptable difference between experimental and simulated frequency.
	 */
	private double frequencyDelta = 1.0;

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
	 * This method tests the frequency of an stimulated motor neuron.
	 * Theoretically the mean prestimulus of simulated motor activity frequency
	 * should be similar to the mean prestimulus of experimental motor activity
	 * frequency.
	 */
	@ScheduledMethod(start = ScheduleParameters.END, priority = ScheduleParameters.LAST_PRIORITY)
	public void testFrequencyOfUnstimulatedMotorNeuronWithStimulatedMotorData() {
		/* first, calculate the PSF of experimental data */
		// read motor response data
		StaticPSF psfExp = new StaticPSF(
				EXP_SOLEUS_TRIGGER, EXP_SOLEUS_MNDISCHARGE, false);
		CuSum cuSumExp = new CuSum(psfExp, 1, 4.0);

		/* then, calculate the PSF of simulated data */
		// calculate PSF using hypothetical stimuli
		double currentTick = SOEASYParameters.getInstance()
				.getCurrentTick();
		DoubleArrayList triggers = new DoubleArrayList();
		for (double trigger = 0.0; trigger < currentTick; trigger = trigger + 1000) {
			triggers.add(trigger);
		}
		StaticPSF psfSim = new StaticPSF(
				triggers, muscleSpikeVector, false);
		CuSum cuSumSim = new CuSum(psfSim, 1, 4.0);

		// prestimulus means should be close
		assertEquals(cuSumExp.getPrestimulusMean(),
				cuSumSim.getPrestimulusMean(), frequencyDelta);		
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
