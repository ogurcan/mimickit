package test.soeasy.microlevel.runningNeuron.nominal.isiTest04;

import mimickit.model.SOEASYEnvironment;
import mimickit.model.neuron.Neuron;
import mimickit.util.FixedISIGenerator;
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

			ISIGenerator isiGenerator = new FixedISIGenerator(100.0);
			Neuron motorNeuron = SOEASYEnvironment.getInstance()
					.createRunningNeuron(null, "Motoneuron",
							axonalDelayMotorNeuron, isiGenerator, null);

			Neuron muscle = SOEASYEnvironment.getInstance().createMuscle();

			motorNeuron.makeSynapseWith(muscle);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
