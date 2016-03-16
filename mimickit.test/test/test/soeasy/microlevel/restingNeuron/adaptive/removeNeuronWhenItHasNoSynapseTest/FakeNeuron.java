package test.soeasy.microlevel.restingNeuron.adaptive.removeNeuronWhenItHasNoSynapseTest;

import mimickit.amas.Action;
import mimickit.amas.CooperativeAgent;
import mimickit.amas.Feedback;
import mimickit.model.FDepolarization;
import mimickit.model.SOEASYParameters;
import mimickit.model.neuron.Neuron;
import repast.simphony.engine.schedule.ScheduleParameters;

public class FakeNeuron extends Neuron {

	public FakeNeuron(CooperativeAgent creatorAgent, String identifier,
			boolean isExcitatory, int numberOfNeurons, double axonalDelay,
			double ahpLevel) {
		super(creatorAgent, identifier, isExcitatory, numberOfNeurons,
				axonalDelay, ahpLevel, null);
	}

	public FakeNeuron() {
		this(null, "FakeNeuron", true, 1, 1.0, 0.0);
	}

	@Override
	public double getCriticality() {
		return 100.0;
	}

	@Override
	protected void defineActions() {
		SOEASYParameters.schedule(
				ScheduleParameters.createRepeating(1, 10.0), new Action(this) {
					@Override
					public void execute() {
						Feedback feedback = new FDepolarization(
								FakeNeuron.this, Feedback.DECREASE);
						sendToAllPreNeurons(feedback);
					}
				});
	}

}
