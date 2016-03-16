package test.soeasy.mesolevel.effectOnMotoneuronTest;

import mimickit.model.SOEASYEnvironment;
import mimickit.model.neuron.InnervatedNeuron;
import mimickit.model.neuron.RestingNeuron;
import mimickit.model.neuron.RunningNeuron;
import mimickit.model.viewer.ACExciteSensoryNeuronsRandomly;
import mimickit.model.viewer.Viewer;
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
			double axonalDelay = 17.5;

			InnervatedNeuron sensoryNeuron = SOEASYEnvironment.getInstance().createInnervatedNeuron(null,
					"AfferentNeuron", true, 10, axonalDelay, null);

			ISIGenerator isiDistribution = new ISIGenerator(DataFile.EXP_MOTONEURON_ONLY);
			RunningNeuron motorNeuron = SOEASYEnvironment.getInstance().createRunningNeuron(null,
					"MotorNeuron", axonalDelay, isiDistribution, null);

			RestingNeuron muscle = SOEASYEnvironment.getInstance().createMuscle();

			sensoryNeuron.makeSynapseWith(motorNeuron);

			motorNeuron.makeSynapseWith(muscle);				

			Viewer viewer = SOEASYEnvironment.getInstance().createViewer(motorNeuron, muscle, null);
			viewer.scheduleOneTimeAction(1000, 0,
					new ACExciteSensoryNeuronsRandomly(viewer));
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}	
}
