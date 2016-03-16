package test.soeasy.microlevel.runningNeuron.nominal.isiTest03;

import static mimickit.util.DataFile.EXP_MOTONEURON_ONLY;
import mimickit.model.SOEASYEnvironment;
import mimickit.model.neuron.Neuron;
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
			double axonalDelayMotorNeuron = 10.0;

			ISIGenerator isiDistribution = new ExactISIGenerator(EXP_MOTONEURON_ONLY);
			Neuron motorNeuron = SOEASYEnvironment.getInstance()
					.createRunningNeuron(null, "Motoneuron",
							axonalDelayMotorNeuron, isiDistribution, null);

			Neuron muscle = SOEASYEnvironment.getInstance().createMuscle();

			motorNeuron.makeSynapseWith(muscle);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
