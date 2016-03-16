package test.soeasy.mesolevel.compareEffectsOnMotoneuronWithFixedFrequency;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import mimickit.amas.Action;
import mimickit.amas.CooperativeAgent;
import mimickit.model.SOEASYEnvironment;
import mimickit.model.SOEASYParameters;
import mimickit.model.neuron.InnervatedNeuron;
import mimickit.model.neuron.Neuron;
import mimickit.model.neuron.RunningNeuron;
import mimickit.model.neuron.SpikeObserver;
import mimickit.util.DoubleArrayList;
import mimickit.util.ISIGenerator;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import test.mimickit.CooperativeTestAgent;

/**
 * 
 * @author Önder Gürcan
 * 
 */
public class TestAgent extends CooperativeTestAgent implements SpikeObserver {

	private InnervatedNeuron afferentNeuron1, afferentNeuron2;

	private RunningNeuron motorNeuron1, motorNeuron2;

	private DoubleArrayList spikesOfAfferentNeuron1, spikesOfAfferentNeuron2;

	private DoubleArrayList spikesOfMotorNeuron1, spikesOfMotorNeuron2;

	private int stimulationCount;

	/** INITIALIZATION METHOD **/

	public TestAgent() {
		SOEASYEnvironment agentFactory = SOEASYEnvironment.getInstance();
		this.afferentNeuron1 = (InnervatedNeuron) agentFactory
				.getAgent("AfferentNeuron1");
		this.afferentNeuron1.addSpikeObserver(this);
		
		this.afferentNeuron2 = (InnervatedNeuron) agentFactory
				.getAgent("AfferentNeuron2");
		this.afferentNeuron2.addSpikeObserver(this);
		
		this.motorNeuron1 = (RunningNeuron) agentFactory
				.getAgent("MotorNeuron1");
		this.motorNeuron1.addSpikeObserver(this);
		
		this.motorNeuron2 = (RunningNeuron) agentFactory
				.getAgent("MotorNeuron2");
		this.motorNeuron2.addSpikeObserver(this);
		
		this.spikesOfAfferentNeuron1 = new DoubleArrayList();
		this.spikesOfAfferentNeuron2 = new DoubleArrayList();
		this.spikesOfMotorNeuron1 = new DoubleArrayList();
		this.spikesOfMotorNeuron2 = new DoubleArrayList();
		this.stimulationCount = 0;
	}

	@Override
	public void update(Neuron spikingNeuron, double spikeTime) {
		if (spikingNeuron.equals(afferentNeuron1)) {
			this.spikesOfAfferentNeuron1.add(spikeTime);
		} else if (spikingNeuron.equals(afferentNeuron2)) {
			this.spikesOfAfferentNeuron2.add(spikeTime);
		} if (spikingNeuron.equals(motorNeuron1)) {
			this.spikesOfMotorNeuron1.add(spikeTime);
		} else if (spikingNeuron.equals(motorNeuron2)) {
			this.spikesOfMotorNeuron2.add(spikeTime);
		}
	}

	/**
	 * Stimulate the motoneuron at a time in-which you cannot excite.
	 */
	@ScheduledMethod(start = 1, interval = 0, priority = ScheduleParameters.FIRST_PRIORITY)
	public void innervateAfferentNeurons() {
		double currentTick = SOEASYParameters.getInstance()
				.getCurrentTick();
		// generate random stimulation between 1 second and 3 seconds.
		int delayForNextStimulation = ((int) (Math.random() * 100)) + 10;
		ACExciteAfferentNeuronsRandomly action = new ACExciteAfferentNeuronsRandomly(
				this);
		SOEASYParameters.schedule(
				ScheduleParameters.createOneTime(currentTick
						+ delayForNextStimulation, 0, 0), action);
	}

	/** TEST METHODS **/

	@ScheduledMethod(start = ScheduleParameters.END, interval = 0, priority = ScheduleParameters.LAST_PRIORITY)
	public void testInterspikeIntervals() {
		DoubleArrayList interspikeIntervals1 = ISIGenerator
				.calculateInterspikeInterval(spikesOfMotorNeuron1);
		interspikeIntervals1.sort();
		double minISI = interspikeIntervals1.get(0);
		double maxISI = interspikeIntervals1.lastElement();
		assertTrue(minISI >= 85);
		assertEquals(104.0, maxISI, 1.0);
		
		DoubleArrayList interspikeIntervals2 = ISIGenerator
				.calculateInterspikeInterval(spikesOfMotorNeuron2);
		interspikeIntervals2.sort();
		double minISI2 = interspikeIntervals2.get(0);
		double maxISI2 = interspikeIntervals2.lastElement();
		assertTrue(minISI2 >= 70);
		assertEquals(104.0, maxISI2, 1.0);
	}

	@ScheduledMethod(start = ScheduleParameters.END, interval = 0, priority = ScheduleParameters.FIRST_PRIORITY)
	public void printInfo() {
		DoubleArrayList interspikeIntervals1 = ISIGenerator
				.calculateInterspikeInterval(spikesOfMotorNeuron1);
		interspikeIntervals1.sort();
		int advancedISICount1 = interspikeIntervals1.indexOf(104);
		int totalISICount1 = interspikeIntervals1.size();
		double advancedISIRate1 = ((double) advancedISICount1 / totalISICount1) * 100;
		double stimulationSuccessRate1 = ((double) advancedISICount1 / stimulationCount) * 100;		
		
		DoubleArrayList interspikeIntervals2 = ISIGenerator
				.calculateInterspikeInterval(spikesOfMotorNeuron2);
		interspikeIntervals2.sort();
		int advancedISICount2 = interspikeIntervals2.indexOf(104);
		int totalISICount2 = interspikeIntervals2.size();
		double advancedISIRate2 = ((double) advancedISICount2 / totalISICount2) * 100;
		double stimulationSuccessRate2 = ((double) advancedISICount2 / stimulationCount) * 100;		
		
		assertTrue(advancedISIRate2 > advancedISIRate1);
		assertTrue(stimulationSuccessRate2 > stimulationSuccessRate1);
	}

	/**
	 * Excites (triggers) the sensory neuron randomly at every 1 to 100 time
	 * steps. randomly.
	 */
	public class ACExciteAfferentNeuronsRandomly extends Action {

		public ACExciteAfferentNeuronsRandomly(CooperativeAgent ownerAgent) {
			super(ownerAgent);
		}

		@Override
		public void execute() {
			// excite the sensory neuron directly.
			afferentNeuron1.innervate();
			afferentNeuron2.innervate();
			stimulationCount++;

			// record given input signal for further comparison with outputs
			double currentTime = SOEASYParameters.getInstance()
					.getCurrentTick();

			// generate random stimulation between 1 second and 3 seconds.
			int delayForNextStimulation = ((int) (Math.random() * 100)) + 10;
			ACExciteAfferentNeuronsRandomly action = new ACExciteAfferentNeuronsRandomly(
					TestAgent.this);
			SOEASYParameters.schedule(
					ScheduleParameters.createOneTime(currentTime
							+ delayForNextStimulation, 0, 0), action);
		}

	} // end of class of ACExciteSensoryNeuron

}
