package test.soeasy.macrolevel.soleus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;
import java.util.List;

import mimickit.amas.CooperativeAgent;
import mimickit.model.MNDischargeRates;
import mimickit.model.SOEASYEnvironment;
import mimickit.model.SOEASYParameters;
import mimickit.model.neuron.Neuron;
import mimickit.model.viewer.Viewer;
import mimickit.util.DoubleArrayList;
import mimickit.util.SimulationReport;
import rast.core.AbstractTestAgent;
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
public class TestAgent extends AbstractTestAgent {

	private Neuron afferentNeuron;

	private Neuron motorNeuron;

	private Viewer wiringViewer;

	//private DoubleArrayList sensoryNeuronSpikeVector;

	private SimulationReport report;

	public TestAgent() {
		report = new SimulationReport("Macro-Level Soleus Test");
		//
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
	//	this.sensoryNeuronSpikeVector = new DoubleArrayList();
	}

	/** STATE MONITORING METHODS **/

//	/**
//	 * Collects "potential" information from InterNeuron.
//	 */
//	@Watch(watcheeClassName = "soeasy.Neuron", watcheeFieldNames = "potential", query = "colocated", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
//	public void monitorSensoryNeuronResponses(Neuron watcheeNeuron) {
//		if (watcheeNeuron.equals(afferentNeuron)) {
//			if (watcheeNeuron.getPotential() > Spike.FIRING_THRESHOLD) {
//				double currentTime = SimulationEnvironment.getInstance()
//						.getCurrentTick();
//				if (sensoryNeuronSpikeVector.size() == 0) {
//					sensoryNeuronSpikeVector.add(currentTime);
//				} else {
//					double previousSpike = sensoryNeuronSpikeVector
//							.lastElement();
//					if ((currentTime - previousSpike) > Spike.AMPLITUDE) {
//						sensoryNeuronSpikeVector.add(currentTime);
//					}
//				}
//			}
//		}
//	}

	/** TEST METHODS **/

	@ScheduledMethod(start = 1001, interval = 0, priority = ScheduleParameters.LAST_PRIORITY)
	public void testPeriodicExcitationOfMotorNeuron() {
		MNDischargeRates dataSet = SOEASYEnvironment.getInstance().getDataSet();
		DoubleArrayList motorResponseList = dataSet.getMNDischarges(0);
		int motorResponseSize = motorResponseList.size();
		assertEquals(9, motorResponseSize, 1);
	}

	@ScheduledMethod(start = 111000000, interval = 200000, priority = ScheduleParameters.LAST_PRIORITY)
	public void printNeuronCount() {
		double currentTick = SOEASYParameters.getInstance()
				.getCurrentTick();
		int numberOfNeurons = SOEASYEnvironment.getInstance()
				.getNumberOfNeuronAgents();
		report.addNumberOfExcitatoryNeurons(currentTick, numberOfNeurons);
		List<CooperativeAgent> agents = SOEASYEnvironment.getInstance().getAgents();
		Iterator<CooperativeAgent> iterator = agents.iterator();
		while (iterator.hasNext()) {
			CooperativeAgent agent = iterator.next();
			double criticality = agent.getCriticality();
			// if (agent.toString().equals("InterNeuron0") && (criticality !=
			// 0.0)) {
			// criticality = agent.getCriticality();
			// System.out.println(agent + " criticality: " + criticality);
			// }
			report.addValue(agent, "Criticality", currentTick, criticality);
		}
		 System.out.println(currentTick + " : number of neurons = "
		 + numberOfNeurons);
//		 
//		 double numberOfActions = SimulationEnvironment.getInstance().getNumberOfActions();
//		 System.out.println(currentTick + " : number of actiond = " + numberOfActions);
	}

}
