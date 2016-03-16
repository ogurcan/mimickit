package test.soeasy.mesolevel.scenario10;

import mimickit.model.neuron.InnervatedNeuron;
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
		// Neuron-P
		Neuron neuronP = getAgentFactory().createRestingNeuron(null,
				"Neuron-P", true, 1, 0.5, 0.70, null);
		// Neuron-1
		InnervatedNeuron neuron1 = getAgentFactory().createInnervatedNeuron(
				null, "Neuron-1", true, 1, 0.5, null);
		//
		neuron1.makeSynapseWith(neuronP);
		//
		// Neuron-2
		InnervatedNeuron neuron2 = getAgentFactory().createInnervatedNeuron(
				null, "Neuron-2", false, 1, 0.5, null);
		//
		neuron2.makeSynapseWith(neuronP);
		// Neuron-3
		InnervatedNeuron neuron3 = getAgentFactory().createInnervatedNeuron(
				null, "Neuron-3", true, 1, 0.5, null);
		//
		neuron3.makeSynapseWith(neuronP);
	}

}
