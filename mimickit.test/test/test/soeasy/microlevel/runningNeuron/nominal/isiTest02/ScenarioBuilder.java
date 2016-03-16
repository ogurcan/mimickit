package test.soeasy.microlevel.runningNeuron.nominal.isiTest02;

import static mimickit.util.DataFile.EXP_SOLEUS_MNDISCHARGE;
import static mimickit.util.DataFile.EXP_SOLEUS_TRIGGER;
import mimickit.model.SOEASYEnvironment;
import mimickit.model.neuron.Neuron;
import mimickit.model.neuron.RestingNeuron;
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
			double axonalDelayMotorNeuron = 10.0;

			ISIGenerator isiDistribution = new ISIGenerator(
					EXP_SOLEUS_TRIGGER,
					EXP_SOLEUS_MNDISCHARGE);
			Neuron motorNeuron = SOEASYEnvironment.getInstance()
					.createRunningNeuron(null, "Motoneuron",
							axonalDelayMotorNeuron, isiDistribution, null);

			RestingNeuron muscle = SOEASYEnvironment.getInstance().createMuscle();

			motorNeuron.makeSynapseWith(muscle);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
