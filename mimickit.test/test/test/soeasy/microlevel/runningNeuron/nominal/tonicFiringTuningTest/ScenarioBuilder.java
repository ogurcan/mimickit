package test.soeasy.microlevel.runningNeuron.nominal.tonicFiringTuningTest;

import mimickit.util.DataFile;
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

			ISIGenerator isiDistribution = new ISIGenerator(DataFile.EXP_MOTONEURON_ONLY);
			getAgentFactory().createRunningNeuron(null, "Motoneuron",
					axonalDelayMotorNeuron, isiDistribution, null);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
