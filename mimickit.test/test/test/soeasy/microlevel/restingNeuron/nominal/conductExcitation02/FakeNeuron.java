package test.soeasy.microlevel.restingNeuron.nominal.conductExcitation02;

import mimickit.amas.CooperativeAgent;
import mimickit.model.neuron.Neuron;

public class FakeNeuron extends Neuron {

	public FakeNeuron(CooperativeAgent creatorAgent, String identifier,
			boolean isExcitatory, int numberOfNeurons, double axonalDelay,
			double ahpLevel) {
		super(creatorAgent, identifier, isExcitatory, numberOfNeurons,
				axonalDelay, ahpLevel, null);
	}

	public FakeNeuron(int numberOfNeurons) {
		this(null, "FakeNeuron", true, numberOfNeurons, 1.0, 0.0);
	}

	@Override
	public double getCriticality() {
		return 0;
	}
	
	@Override
	protected void defineActions() {
		// no action
	}
}
