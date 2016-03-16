package test.soeasy.macrolevel.hreflex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import mimickit.model.MNDischargeRates;
import mimickit.model.SOEASYEnvironment;
import mimickit.model.SOEASYParameters;
import mimickit.model.Spike;
import mimickit.model.neuron.Neuron;
import mimickit.model.viewer.Viewer;
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

	private Neuron afferentNeuron;

	private Neuron motorNeuron;

	private Viewer wiringViewer;

	private DoubleArrayList sensoryNeuronSpikeVector;

	private DoubleArrayList tickVector;

	/** INITIALIZATION METHOD **/

	public TestAgent() {
		afferentNeuron = (Neuron) SOEASYEnvironment.getInstance().getAgent(
				"AfferentNeuron");
		assertNotNull(afferentNeuron);
		motorNeuron = (Neuron) SOEASYEnvironment.getInstance().getAgent(
				"MotorNeuron");
		assertNotNull(motorNeuron);
		wiringViewer = (Viewer) SOEASYEnvironment.getInstance().getAgent(
				"WiringViewer");
		assertNotNull(wiringViewer);
		//
		this.sensoryNeuronSpikeVector = new DoubleArrayList();
		this.tickVector = new DoubleArrayList();
	}

	/** STATE MONITORING METHODS **/

	/**
	 * Collects "potential" information from InterNeuron.
	 */
	@Watch(watcheeClassName = "soeasy.Neuron", watcheeFieldNames = "potential", query = "colocated", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void monitorSensoryNeuronResponses(Neuron watcheeNeuron) {
		if (watcheeNeuron.equals(afferentNeuron)) {
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
		}
	}

	/** TEST METHODS **/

	@ScheduledMethod(start = 1001, interval = 0, priority = ScheduleParameters.LAST_PRIORITY)
	public void testPeriodicExcitationOfMotorNeuron() {
		MNDischargeRates dataSet = SOEASYEnvironment.getInstance().getDataSet();
		DoubleArrayList motorResponseList = dataSet.getMNDischarges(0);
		int motorResponseSize = motorResponseList.size();
		assertEquals(9, motorResponseSize, 1);
	}

	@ScheduledMethod(start = 11, interval = 10000, priority = ScheduleParameters.LAST_PRIORITY)
	public void testOrganizationOfTheNetwork() {
		// afferent neuron
		assertEquals(0, afferentNeuron.getPreSynapses().size());
		assertEquals(1, afferentNeuron.getPostSynapses().size());
		// motor neuron
		assertEquals(1, motorNeuron.getPreSynapses().size());
		assertEquals(1, motorNeuron.getPostSynapses().size());
	}

	@ScheduledMethod(start = 0, interval = 10)
	public void observeExecutionClock() {
		tickVector.add(SOEASYParameters.getInstance().getCurrentTick());
	}

}
