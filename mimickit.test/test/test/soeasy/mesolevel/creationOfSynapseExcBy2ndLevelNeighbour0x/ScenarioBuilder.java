package test.soeasy.mesolevel.creationOfSynapseExcBy2ndLevelNeighbour0x;

import mimickit.model.neuron.InnervatedNeuron;
import mimickit.model.neuron.Neuron;
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
		// Neuron-P
		Neuron neuronP = getAgentFactory().createRestingNeuron(null, "Neuron-P", true, 1, 0.5, 15.0, null);
		// Neuron-1
		InnervatedNeuron neuron1 = getAgentFactory().createInnervatedNeuron(null, "Neuron-1", 10, null);
		// Neuron-2
		RestingNeuron neuron2 = getAgentFactory().createRestingNeuron(null, "Neuron-2", true, 10, 0.5, 1.5, null);
		// Neuron-3
		RestingNeuron neuron3 = getAgentFactory().createRestingNeuron(null, "Neuron-3", true, 10, 0.5, 1.5, null);
		// Neuron-4
		RestingNeuron neuron4 = getAgentFactory().createRestingNeuron(null, "Neuron-4", true, 10, 0.5, 1.5, null);
		// Neuron-5
		RestingNeuron neuron5 = getAgentFactory().createRestingNeuron(null, "Neuron-5", true, 10, 0.5, 3.0, null);
		// Neuron-6
		RestingNeuron neuron6 = getAgentFactory().createRestingNeuron(null, "Neuron-6", true, 10, 0.5, 3.0, null);
		//
		neuron1.makeSynapseWith(neuron2);
		neuron1.makeSynapseWith(neuron3);
		neuron1.makeSynapseWith(neuron4);
		//
		neuron2.makeSynapseWith(neuron5);
		neuron2.makeSynapseWith(neuron6);
		//
		neuron3.makeSynapseWith(neuron5);
		//
		neuron4.makeSynapseWith(neuron6);
		//
		neuron5.makeSynapseWith(neuronP);
		//
		neuron6.makeSynapseWith(neuronP);
	}

}
