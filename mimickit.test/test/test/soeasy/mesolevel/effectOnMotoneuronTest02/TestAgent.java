package test.soeasy.mesolevel.effectOnMotoneuronTest02;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import mimickit.model.SOEASYEnvironment;
import mimickit.model.neuron.InnervatedNeuron;
import mimickit.model.neuron.Neuron;
import mimickit.model.neuron.RunningNeuron;
import mimickit.model.neuron.SpikeObserver;
import mimickit.util.CuSum;
import mimickit.util.DoubleArrayList;
import mimickit.util.ISIGenerator;
import mimickit.util.StaticPSF;
import rast.AbstractTestAgent;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * 
 * @author Önder Gürcan
 * 
 */
public class TestAgent extends AbstractTestAgent implements SpikeObserver {

	private InnervatedNeuron sensoryNeuron;

	private RunningNeuron motorNeuron;

	private DoubleArrayList sensoryNeuronSpikeVector;

	private DoubleArrayList motorNeuronSpikeVector;

	/** INITIALIZATION METHOD **/

	public TestAgent() {
		this.sensoryNeuron = (InnervatedNeuron) SOEASYEnvironment.getInstance()
				.getAgent("AfferentNeuron");
		this.sensoryNeuron.addSpikeObserver(this);

		this.motorNeuron = (RunningNeuron) SOEASYEnvironment.getInstance().getAgent(
				"MotorNeuron");
		this.motorNeuron.addSpikeObserver(this);

		this.sensoryNeuronSpikeVector = new DoubleArrayList();
		this.motorNeuronSpikeVector = new DoubleArrayList();
	}

	@ScheduledMethod(start = 1130, interval = 0, priority = ScheduleParameters.LAST_PRIORITY)
	public void innervateSensoryNeuron() {
		sensoryNeuron.innervate();
	}

	/** TEST METHODS **/

	@ScheduledMethod(start = ScheduleParameters.END, interval = 0, priority = ScheduleParameters.LAST_PRIORITY)
	public void testMotorNeuronBehaviour() {
		DoubleArrayList interspikeIntervals = ISIGenerator
				.calculateInterspikeInterval(motorNeuronSpikeVector);

		int effectedISICount = 0;
		int normalISICount = 0;
		for (int i = 0; i < interspikeIntervals.size(); i++) {
			double isi = interspikeIntervals.get(i);
			if (isi < 104.0) {
				effectedISICount++;
			} else if (isi == 104.0) {
				normalISICount++;
			} else {
				fail();
			}
		}

		assertEquals(1, effectedISICount);
		assertEquals(interspikeIntervals.size(), effectedISICount
				+ normalISICount);
	}

	@ScheduledMethod(start = ScheduleParameters.END, interval = 0, priority = ScheduleParameters.LAST_PRIORITY)
	public void testInhibitoryEffectOfSensoryNeuronOnMotorNeuron() {
		// assertTrue(motorNeuronSpikeVector.size() >= 9000);

		// double lastSpike = motorNeuronSpikeVector.lastElement();
		// assertTrue(lastSpike > 999900.0);

		StaticPSF psf = new StaticPSF(
				sensoryNeuronSpikeVector, motorNeuronSpikeVector, false);
		psf.getDischargeRatesXYChart(-400, 500).view(800, 600);
		CuSum cuSum = new CuSum(psf, 1, 4.0);
		cuSum.getXYChart().view(800, 600);
		// TODO add face test here!!!
	}

	@Override
	public void update(Neuron spikingNeuron, double spikeTime) {
		if (spikingNeuron.equals(sensoryNeuron)) {
			this.sensoryNeuronSpikeVector.add(spikeTime);
		} else if (spikingNeuron.equals(motorNeuron)) {
			this.motorNeuronSpikeVector.add(spikeTime);
		}
	}
}
