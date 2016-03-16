package test.soeasy.mesolevel.compareEffectsOnMotoneuronWithVariableFrequency02;

import mimickit.model.neuron.InnervatedNeuron;
import mimickit.model.neuron.RunningNeuron;
import mimickit.util.DataFile;
import mimickit.util.ExactISIGenerator;
import mimickit.util.ISIGenerator;
import test.mimickit.SOEasyScenarioBuilder;

/**
 * 
 * @author Önder Gürcan
 * 
 */
public class ScenarioBuilder extends SOEasyScenarioBuilder {

	@Override
	protected void createAgents() {
		try {
			double axonalDelay = 1.0;
			ISIGenerator isiDistribution = new ExactISIGenerator(DataFile.EXP_SOLEUS_TRIGGER, DataFile.EXP_SOLEUS_MNDISCHARGE);

			InnervatedNeuron sensoryNeuron1 = getAgentFactory()
					.createInnervatedNeuron(null, "AfferentNeuron1", true, 20,
							axonalDelay, null);
			RunningNeuron motorNeuron1 = getAgentFactory()
					.createRunningNeuron(null, "MotorNeuron1", axonalDelay,
							isiDistribution, null);
			sensoryNeuron1.makeSynapseWith(motorNeuron1);
			
			InnervatedNeuron sensoryNeuron2 = getAgentFactory()
					.createInnervatedNeuron(null, "AfferentNeuron2", true, 30,
							axonalDelay, null);
			RunningNeuron motorNeuron2 = getAgentFactory()
					.createRunningNeuron(null, "MotorNeuron2", axonalDelay,
							isiDistribution, null);
			sensoryNeuron2.makeSynapseWith(motorNeuron2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
