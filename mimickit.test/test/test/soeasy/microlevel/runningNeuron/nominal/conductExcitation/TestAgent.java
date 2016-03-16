package test.soeasy.microlevel.runningNeuron.nominal.conductExcitation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import mimickit.model.SOEASYEnvironment;
import mimickit.model.SOEASYParameters;
import mimickit.model.Spike;
import mimickit.model.neuron.RunningNeuron;
import mimickit.model.neuron.Synapse;
import rast.AbstractTestAgent;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * 
 * @author Önder Gürcan
 * 
 */
public class TestAgent extends AbstractTestAgent {

	private RunningNeuron runningNeuron;

	private Synapse synapse;

	/** INITIALIZATION METHOD **/

	public TestAgent() {
		SOEASYEnvironment agentFactory = SOEASYEnvironment.getInstance();
		runningNeuron = (RunningNeuron) agentFactory
				.getAgent(ScenarioBuilder.POST_SYNAPTIC_NEURON);
		synapse = runningNeuron.getPreSynapses().get(0);
	}

	/** TEST METHODS **/

	/* The 1st spike will occur at tick 97. See isiTest04. */

	/**
	 * Conduct a spike at 87.0 ms. Since axonal delay is 3 ms, this spike will
	 * arrive at the post-synaptic neuron at 90.0.
	 */
	@ScheduledMethod(start = 87.0, interval = 0, priority = ScheduleParameters.LAST_PRIORITY)
	public void conductSpike() {
		synapse.conductSpike(true);
		assertFalse(runningNeuron.isExcited());
	}

	/**
	 * Since axonal delay is 3.0, the spike given at 87.0 will arrive to the
	 * running neuron at 90.0.
	 * 
	 * From 90.0 to 94.0, there will be an increase in the membrane potential.
	 * 
	 * At 94.0, there must be a decrase in the potential since the effect of the
	 * synapse disappears and the running neuron goes on increasing its potential.
	 * 
	 * 
	 */
	@ScheduledMethod(start = 1, interval = 0.1, priority = ScheduleParameters.LAST_PRIORITY)
	public void checkMembranePotentialOfRunningNeuron() {
		double currentTick = SOEASYParameters.getInstance().getCurrentTick();
		double referencePotential = 0.0;
		if ((currentTick >= 93.9) && (currentTick < 94.0)) {
			referencePotential = runningNeuron.getPotential();
		} else if ((currentTick >= 94.0) && (currentTick < 94.1)) {
			// after the effect of the spike, the membrane potential should
			// decrease
			assertTrue(referencePotential > runningNeuron.getPotential());
		} else if ((currentTick >= 96.0)
				&& (currentTick < (96.0 + Spike.SPIKE_DURATION))) {
			// a potential increase should occur from 10.0 to 13.0
			assertTrue(runningNeuron.isExcited());
		} else {
			// otherwise, the membrane potential should equal to resting
			// potential.
			assertFalse(runningNeuron.isExcited());
		}
	}

}
