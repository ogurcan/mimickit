package mimickit.scenario.singlemotorunit;

import mimickit.model.SOEASYEnvironment;
import mimickit.model.SOEASYParameters;
import mimickit.model.neuron.InnervatedNeuron;
import mimickit.model.neuron.Neuron;
import mimickit.model.neuron.RunningNeuron;
import mimickit.model.viewer.ACExciteSensoryNeuronsRandomly;
import mimickit.model.viewer.Viewer;
import mimickit.scenario.AbstractScenarioBuilder;
import mimickit.util.ExactISIGenerator;
import mimickit.util.ISIGenerator;

/**
 * 
 * @author Önder Gürcan
 * 
 */
public class ScenarioBuilder extends AbstractScenarioBuilder {

	@Override
	protected void createFunctionalAgents() {
		try {
			SOEASYParameters parameters = SOEASYParameters.getInstance();

			double pathwayBeginning = SOEASYParameters.getInstance()
					.getParameter(SOEASYParameters.PATHWAY_BEGINNING);

			double axonalDelayAfferentNeuron = Math.round(pathwayBeginning / 2);
			double axonalDelayMotoneuron = pathwayBeginning - axonalDelayAfferentNeuron;

			int numberOfAfferents = (int) parameters
					.getParameter(SOEASYParameters.NUMBER_OF_INITIAL_SENSORY_NEURONS);
			InnervatedNeuron afferentNeuron = getAgentFactory()
					.createInnervatedNeuron(null, "AfferentNeuron", true,
							numberOfAfferents, axonalDelayAfferentNeuron, null);

			SOEASYEnvironment environment = SOEASYEnvironment.getInstance();
			ISIGenerator isiGenerator = new ExactISIGenerator(
					environment.getDataSet().getReferenceTriggerDataFile(),
					environment.getDataSet().getReferenceMNDischargeDataFile());
			RunningNeuron motorNeuron = getAgentFactory().createRunningNeuron(
					null, "MotorNeuron", axonalDelayMotoneuron, isiGenerator,
					null);

			Neuron muscle = getAgentFactory().createMuscle();

			afferentNeuron.makeSynapseWith(motorNeuron);

			motorNeuron.makeSynapseWith(muscle);

			Viewer viewer = SOEASYEnvironment.getInstance().createViewer(
					motorNeuron, muscle);

			viewer.scheduleOneTimeAction(1000, 0,
					new ACExciteSensoryNeuronsRandomly(viewer));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
