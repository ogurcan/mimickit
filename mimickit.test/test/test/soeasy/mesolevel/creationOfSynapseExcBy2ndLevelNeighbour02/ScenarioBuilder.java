package test.soeasy.mesolevel.creationOfSynapseExcBy2ndLevelNeighbour02;

import mimickit.model.neuron.Neuron;
import test.mimickit.SOEasyScenarioBuilder;

/**
 * 
 * @author Önder Gürcan
 * 
 */
public class ScenarioBuilder extends SOEasyScenarioBuilder {

	@Override
	protected void createAgents() {
		// Neuron-1
		Neuron n1 = getAgentFactory().createInnervatedNeuron(null, "N1", 1, null);
		// Neuron-2
		Neuron n2 = getAgentFactory().createRestingNeuron(null, "N2", true, 1, 0.5, 0.15, null);
		Neuron n3 = getAgentFactory().createRestingNeuron(null, "N3", true, 1, 0.5, 0.15, null);
		// Neuron-3
		Neuron n4 = getAgentFactory().createRestingNeuron(null, "N4", true, 1, 0.5, 1.21, null);
		
		//
		n1.makeSynapseWith(n2);
		n1.makeSynapseWith(n3);
		//
		n2.makeSynapseWith(n4);
		n3.makeSynapseWith(n4);
	}

}
