package test.soeasy.mesolevel.creationOfSynapseExcBy3rdLevelNeighbour;

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
		// Neuron-I
		Neuron neuronI = getAgentFactory().createInnervatedNeuron(null,
				"Neuron-I", 1, null);
		// Neuron-1
		Neuron neuron1 = getAgentFactory().createRestingNeuron(null,
				"Neuron-1", true, 1, 0.5, 0.15, null);
		// Neuron-2
		Neuron neuron2 = getAgentFactory().createRestingNeuron(null,
				"Neuron-2", true, 1, 0.5, 0.15, null);
		// Neuron-3
		Neuron neuron3 = getAgentFactory().createRestingNeuron(null,
				"Neuron-3", true, 1, 0.5, 0.15, null);
		// Neuron-4 - inhibitory
		Neuron neuron4 = getAgentFactory().createRestingNeuron(null,
				"Neuron-4", false, 1, 0.5, 0.15, null);
		// Neuron-5
		Neuron neuron5 = getAgentFactory().createRestingNeuron(null,
				"Neuron-5", true, 1, 0.5, 0.65, null);

		//
		neuronI.makeSynapseWith(neuron1);
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
