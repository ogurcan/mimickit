package test.soeasy.microlevel.restingNeuron.nominal.conductInhibition02;

import static org.junit.Assert.assertEquals;
import mimickit.model.SOEASYEnvironment;
import mimickit.model.SOEASYParameters;
import mimickit.model.Spike;
import mimickit.model.neuron.RestingNeuron;
import mimickit.model.neuron.Synapse;
import rast.core.AbstractTestAgent;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * 
 * @author Önder Gürcan
 * 
 */
public class TestAgent extends AbstractTestAgent {

	private RestingNeuron restingNeuron;

	private Synapse synapse;

	private double restingPotential;

	/** INITIALIZATION METHOD **/

	public TestAgent() {
		restingNeuron = (RestingNeuron) SOEASYEnvironment.getInstance().getAgent(
				"RestingNeuron");
		synapse = restingNeuron.getPreSynapses().get(0);

		restingPotential = Spike.FIRING_THRESHOLD - 1.0;
	}

	/** TEST METHODS **/	

	@ScheduledMethod(start = 1, interval = 0.1, priority = ScheduleParameters.LAST_PRIORITY)
	public void checkMembranePotentialOfRestingNeuron() {
		double currentTick = SOEASYParameters.getInstance()
				.getCurrentTick();
		if ((currentTick >= 10.0) && (currentTick < (10.0 + Spike.SPIKE_DURATION))) {
			// a potential increase should occur from 10.0 to 14.0
			double expected = restingPotential
					+ synapse.getTotalSynapticStrength();
			double actual = restingNeuron.getPotential();
			assertEquals(expected, actual, 0.0);
		} else {
			// otherwise, the membrane potential should equal to resting
			// potential.
			assertEquals(restingPotential, restingNeuron.getPotential(), 0.0);
		}
	}
	
	/**
	 * Conduct a spike at 5. Since the axonal delay is 5 ms, a potential
	 * difference must occur at tick 10.
	 */
	@ScheduledMethod(start = 5, interval = 0, priority = ScheduleParameters.FIRST_PRIORITY)
	public void conductionStart() {
		assertEquals(restingPotential, restingNeuron.getPotential(), 0.0);		
		synapse.conductSpike(true);
		assertEquals(restingPotential, restingNeuron.getPotential(), 0.0);
	}
}
