package test.soeasy.microlevel.restingNeuron.adaptive.removeNeuronWhenItIsNotActiveTest;

import test.mimickit.SOEasyScenarioBuilder;

/**
 * 
 * @author Önder Gürcan
 * 
 */
public class ScenarioBuilder extends SOEasyScenarioBuilder {

	@Override
	protected void createAgents() {
		getAgentFactory().createRestingNeuron(null, "RestingNeuron", true, 1,
				0, 0.55, null);
	}
}
