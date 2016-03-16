package test.soeasy.microlevel.restingNeuron.adaptive.removeNeuronWhenItHasNoSynapseTest;

import mimickit.model.neuron.RestingNeuron;
import test.mimickit.SOEasyScenarioBuilder;

/**
 * This scenario aims to test the conduction of a spike to a "non-excited" resting
 * neuron agent. 
 * 
 * @author Önder Gürcan
 * 
 */
public class ScenarioBuilder extends SOEasyScenarioBuilder {

	@Override
	protected void createAgents() {			
		RestingNeuron presynapticNeuron = getAgentFactory()
				.createRestingNeuron(null, "RestingNeuron", true, 1, 0,
						1.0, null);		

		FakeNeuron postsynapticNeuron = new FakeNeuron();
		
		double axonalDelay = 5.0;
		
		getAgentFactory().createSynapse(null, presynapticNeuron,
				postsynapticNeuron, axonalDelay);
	}
}