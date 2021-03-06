package test.soeasy.microlevel.restingNeuron.nominal.integrateAndFireTestWithoutInhibitoryContributor;

import mimickit.model.neuron.RestingNeuron;
import test.mimickit.SOEasyScenarioBuilder;

/**
 * 
 * @author Önder Gürcan
 * 
 */
public class ScenarioBuilder extends SOEasyScenarioBuilder {

	@Override
	protected void createAgents() {
		RestingNeuron restingNeuron = getAgentFactory().createRestingNeuron(
				null, "RestingNeuron", true, 1, 0, 0.55, null);

		FakeNeuron preNeuron = new FakeNeuron();
		// synapse1
		getAgentFactory().createSynapse(null, preNeuron, restingNeuron, 1.0);
		// synapse2
		getAgentFactory().createSynapse(null, preNeuron, restingNeuron, 6.0);
		// synapse3
		getAgentFactory().createSynapse(null, preNeuron, restingNeuron, 10.0);
		// synapse4
		getAgentFactory().createSynapse(null, preNeuron, restingNeuron, 11.0);
		// synapse5
		getAgentFactory().createSynapse(null, preNeuron, restingNeuron, 12.0);
		// synapse6
		getAgentFactory().createSynapse(null, preNeuron, restingNeuron, 13.0);
		// synapse7
		getAgentFactory().createSynapse(null, preNeuron, restingNeuron, 14.0);
		// synapse8
		getAgentFactory().createSynapse(null, preNeuron, restingNeuron, 15.0);
		// synapse9
		getAgentFactory().createSynapse(null, preNeuron, restingNeuron, 16.0);	
	}
}
