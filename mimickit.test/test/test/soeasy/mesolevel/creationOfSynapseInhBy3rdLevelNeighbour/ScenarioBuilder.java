package test.soeasy.mesolevel.creationOfSynapseInhBy3rdLevelNeighbour;

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
		Neuron neuron1 = getAgentFactory().createInnervatedNeuron(null,
				"Neuron-1", 1, null);
		// Neuron-2
		Neuron neuron2 = getAgentFactory().createRestingNeuron(null,
				"Neuron-2", true, 20, 0.5, 0.15, null);
		// Neuron-3
		Neuron neuron3 = getAgentFactory().createRestingNeuron(null,
				"Neuron-3", true, 1, 0.5, 0.15, null);
		// Neuron-4 - inhibitory
		Neuron neuron4 = getAgentFactory().createRestingNeuron(null,
				"Neuron-4", false, 1, 0.5, 0.15, null);
		// Neuron-5
		Neuron neuron5 = getAgentFactory().createRestingNeuron(null,
				"Neuron-5", true, 1, 0.5, 0.15, null);

		//
		neuron1.makeSynapseWith(neuron2);
		//
		neuron1.makeSynapseWith(neuron3);
		//
		neuron1.makeSynapseWith(neuron4);
		//
		neuron2.makeSynapseWith(neuron5);
	}

}
