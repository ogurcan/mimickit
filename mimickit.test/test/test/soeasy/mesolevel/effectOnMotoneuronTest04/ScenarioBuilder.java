package test.soeasy.mesolevel.effectOnMotoneuronTest04;

import mimickit.model.SOEASYEnvironment;
import mimickit.model.neuron.InnervatedNeuron;
import mimickit.model.neuron.RunningNeuron;
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
			double axonalDelay = 1.0;

			InnervatedNeuron sensoryNeuron = SOEASYEnvironment.getInstance().createInnervatedNeuron(null,
					"AfferentNeuron", true, 40, axonalDelay, null);

			ISIGenerator isiDistribution = new FixedISIGenerator(104);
			RunningNeuron motorNeuron = SOEASYEnvironment.getInstance().createRunningNeuron(null,
					"MotorNeuron", axonalDelay, isiDistribution, null);

			sensoryNeuron.makeSynapseWith(motorNeuron);						
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}	
}
