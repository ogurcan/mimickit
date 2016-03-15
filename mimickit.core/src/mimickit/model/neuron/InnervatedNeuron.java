package mimickit.model.neuron;

import mimickit.amas.CooperativeAgent;
import mimickit.model.FInstantFrequency;
import mimickit.model.SOEASYParameters;
import mimickit.model.Spike;
import repast.simphony.engine.schedule.IAction;
import repast.simphony.engine.schedule.ScheduleParameters;

public class InnervatedNeuron extends Neuron {

	public InnervatedNeuron(CooperativeAgent creatorAgent, String identifier,
			boolean isExcitatory, int numberOfNeurons, double axonalDelay,
			double ahpLevel, String cause) {
		super(creatorAgent, identifier, isExcitatory, numberOfNeurons,
				axonalDelay, ahpLevel, cause);
	}

	@Override
	protected void defineActions() {
		super.defineActions();

		defineFeedbackActionPair(FInstantFrequency.class,
				ACHandleFInstantFrequency4RestingNeuron.class);
	}

	/**
	 * Used to stimulate this neuron.
	 */
	public void innervate() {
		if (!isExcited()) {
			excite();
		}
	}

	@Override
	protected void excite() {
		super.excite();

		double currentTime = SOEASYParameters.getInstance().getCurrentTick();

		// make this neuron go back to resting state after
		// "amplitude" time steps.
		SOEASYParameters.schedule(
				ScheduleParameters.createOneTime(currentTime
						+ Spike.SPIKE_DURATION, 0, 0.0), new IAction() {
					public void execute() {
						setPotential(Spike.FIRING_THRESHOLD - getAhpLevel());
					}
				});

	}

	@Override
	public void cooperate() {
		handleFeedbacksNEW();
	}

//	@Override
//	public double getCriticality() {
//		double criticality = 0.0; // 100.0;
//		//
//		double sumUp = 0.0;
//		double sumDown = 0.0;
//		Iterator<Synapse> iterator = getPostSynapses().iterator();
//		while (iterator.hasNext()) {
//			Synapse postSynapse = iterator.next();
//			double synapseCriticality = postSynapse.getCriticality();
//			double deltaCriticality = postSynapse.getDeltaCriticality();
//			sumUp += Math.max(deltaCriticality, synapseCriticality);
//			sumDown++;
//		}
//		if (sumDown != 0.0) {
//			criticality = sumUp / sumDown;
//		}
//
//		if (Double.isNaN(criticality)) {
//			criticality = 0.0;
//		}
//		return criticality;
//	}

}
