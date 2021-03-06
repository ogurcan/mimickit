package test.soeasy.mesolevel.effectOnMotoneuronTest04;

import static org.junit.Assert.assertEquals;
import mimickit.model.SOEASYEnvironment;
import mimickit.model.SOEASYParameters;
import mimickit.model.Spike;
import mimickit.model.neuron.InnervatedNeuron;
import mimickit.model.neuron.Neuron;
import mimickit.model.neuron.RunningNeuron;
import mimickit.util.DoubleArrayList;
import rast.core.AbstractTestAgent;
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

	private InnervatedNeuron sensoryNeuron;

	private RunningNeuron motorNeuron;

	private DoubleArrayList sensoryNeuronSpikeVector;

	private DoubleArrayList motorNeuronSpikeVector;

	/** INITIALIZATION METHOD **/

	public TestAgent() {
		this.sensoryNeuron = (InnervatedNeuron) SOEASYEnvironment.getInstance()
				.getAgent("AfferentNeuron");
		this.motorNeuron = (RunningNeuron) SOEASYEnvironment.getInstance().getAgent(
				"MotorNeuron");
		this.sensoryNeuronSpikeVector = new DoubleArrayList();
		this.motorNeuronSpikeVector = new DoubleArrayList();
	}

	/** STATE MONITORING METHODS **/

	/**
	 * Collects "potential" information from InterNeuron.
	 */
	@Watch(watcheeClassName = "soeasy.Neuron", watcheeFieldNames = "potential", query = "colocated", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void monitorSensoryNeuronResponses(Neuron watcheeNeuron) {
		if (watcheeNeuron.equals(sensoryNeuron)) {
			if (watcheeNeuron.getPotential() > Spike.FIRING_THRESHOLD) {
				double currentTime = SOEASYParameters.getInstance()
						.getCurrentTick();
				if (sensoryNeuronSpikeVector.size() == 0) {
					sensoryNeuronSpikeVector.add(currentTime);
				} else {
					double previousSpike = sensoryNeuronSpikeVector
							.lastElement();
					if ((currentTime - previousSpike) > Spike.SPIKE_DURATION) {
						sensoryNeuronSpikeVector.add(currentTime);
					}
				}
			}
		} else if (watcheeNeuron.equals(motorNeuron)) {
			if (watcheeNeuron.getPotential() > Spike.FIRING_THRESHOLD) {
				double currentTime = SOEASYParameters.getInstance()
						.getCurrentTick();
				if (motorNeuronSpikeVector.size() == 0) {
					motorNeuronSpikeVector.add(currentTime);
				} else {
					double previousSpike = motorNeuronSpikeVector.lastElement();
					if ((currentTime - previousSpike) > Spike.SPIKE_DURATION) {
						motorNeuronSpikeVector.add(currentTime);
					}
				}
			}
		}
	}

	/**
	 * Stimulate the motoneuron at a time in-which you cannot excite.
	 */
	@ScheduledMethod(start = 130, interval = 0, priority = ScheduleParameters.FIRST_PRIORITY)
	public void innervateSensoryNeuron1() {
		sensoryNeuron.innervate();
	}

	/**
	 * Stimulate the motoneuron at a time in-which you can excite.
	 */
	@ScheduledMethod(start = 144, interval = 0, priority = ScheduleParameters.FIRST_PRIORITY)
	public void innervateSensoryNeuron2() {
		sensoryNeuron.innervate();
	}

	/** TEST METHODS **/

	@ScheduledMethod(start = 254.0, interval = 0, priority = ScheduleParameters.LAST_PRIORITY)
	public void testSpikes1() {
		double firstSpike = motorNeuronSpikeVector.get(0);
		assertEquals(100.0, firstSpike, 1.0);
		double secondSpike = motorNeuronSpikeVector.get(1);
		assertEquals(145.0, secondSpike, 1.0);
		double thirdSpike = motorNeuronSpikeVector.get(2);
		assertEquals(249.0, thirdSpike, 1.0);
	}

	/**
	 * Stimulate the motoneuron at a time in-which you can not excite.
	 */
	@ScheduledMethod(start = 287, interval = 0, priority = ScheduleParameters.FIRST_PRIORITY)
	public void innervateSensoryNeuron3() {
		sensoryNeuron.innervate();
	}
	
	@ScheduledMethod(start = 354.0, interval = 0, priority = ScheduleParameters.LAST_PRIORITY)
	public void testSpikes2() {
		double firstSpike = motorNeuronSpikeVector.get(0);
		assertEquals(100.0, firstSpike, 1.0);
		double secondSpike = motorNeuronSpikeVector.get(1);
		assertEquals(145.0, secondSpike, 1.0);
		double thirdSpike = motorNeuronSpikeVector.get(2);
		assertEquals(249.0, thirdSpike, 1.0);
		double fourthSpike = motorNeuronSpikeVector.get(3);
		assertEquals(353.0, fourthSpike, 1.0);
	}


}
