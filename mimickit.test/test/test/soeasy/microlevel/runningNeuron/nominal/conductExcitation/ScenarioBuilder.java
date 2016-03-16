package test.soeasy.microlevel.runningNeuron.nominal.conductExcitation;

import mimickit.model.neuron.RunningNeuron;
import mimickit.util.FixedISIGenerator;
import mimickit.util.ISIGenerator;
import test.mimickit.SOEasyScenarioBuilder;

/**
 * This scenario aims to test the conduction of a spike to a "non-excited"
 * running neuron agent.
 * 
 * @author Önder Gürcan
 * 
 */
public class ScenarioBuilder extends SOEasyScenarioBuilder {

	protected static String POST_SYNAPTIC_NEURON = "RunningNeuron";

	@Override
	protected void createAgents() {
		// Fixed ISI value is set to 10, so at every 0.5 tick the membrane potential
		// will be increased by 0.5 mV.
		ISIGenerator isiGenerator = new FixedISIGenerator(100.0);
		RunningNeuron postsynapticNeuron = getAgentFactory()
				.createRunningNeuron(null, POST_SYNAPTIC_NEURON, 0,
						isiGenerator, null);

		FakeNeuron presynapticNeuron = new FakeNeuron();

		double axonalDelay = 3.0;
		getAgentFactory().createSynapse(presynapticNeuron, presynapticNeuron,
				postsynapticNeuron, axonalDelay);
	}
}
