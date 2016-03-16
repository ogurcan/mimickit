package test.soeasy.mesolevel.creationOfSynapseExcBy2ndLevelNeighbour03;

import mimickit.model.neuron.Neuron;
import test.mimickit.SOEasyScenarioBuilder;

/**
 * 
 * @author Önder Gürcan
 * 
 */
public class ScenarioBuilder extends SOEasyScenarioBuilder {

	protected static String NEURON_I = "Neuron-I";
	protected static String NEURON_1 = "Neuron-1";
	protected static String NEURON_2 = "Neuron-2";
	protected static String NEURON_3 = "Neuron-3";
	protected static String NEURON_4 = "Neuron-4";
	protected static String NEURON_5 = "Neuron-5";

	@Override
	protected void createAgents() {
		// Neuron-1
		Neuron nI = getAgentFactory().createInnervatedNeuron(null, NEURON_I, 1, null);
		// Neuron-1
		Neuron n1 = getAgentFactory().createRestingNeuron(null, NEURON_1, true,
						1, 0.5, 0.15, null);
		// Neuron-2
		Neuron n2 = getAgentFactory().createRestingNeuron(null, NEURON_2, true,
				1, 0.5, 0.15, null);
		Neuron n3 = getAgentFactory().createRestingNeuron(null, NEURON_3, true,
				1, 0.5, 0.15, null);
		Neuron n4 = getAgentFactory().createRestingNeuron(null, NEURON_4, true,
				1, 0.5, 0.15, null);
		// Neuron-3
		Neuron n5 = getAgentFactory().createRestingNeuron(null, NEURON_5, true,
				1, 0.5, 1.21, null);

		//
		nI.makeSynapseWith(n1);
		n1.makeSynapseWith(n2);
		n1.makeSynapseWith(n3);
		n1.makeSynapseWith(n4);
		//
		n3.makeSynapseWith(n5);
		n4.makeSynapseWith(n5);
	}

}
