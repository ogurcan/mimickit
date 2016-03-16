package test.soeasy.microlevel.runningNeuron.nominal.isiTest01;

import mimickit.model.SOEASYEnvironment;
import mimickit.model.neuron.Neuron;
import mimickit.model.neuron.RestingNeuron;
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
			double axonalDelayMotorNeuron = 10.0;

			ISIGenerator isiDist = new ExactISIGenerator(DataFile.EXP_MOTONEURON_ONLY);
			Neuron motorNeuron = SOEASYEnvironment.getInstance().createRunningNeuron(
					null, "Motoneuron", axonalDelayMotorNeuron, isiDist, null);

			RestingNeuron muscle = SOEASYEnvironment.getInstance().createMuscle();

			motorNeuron.makeSynapseWith(muscle);
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}
}
