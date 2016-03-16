package test.soeasy.macrolevel.soleus;

import mimickit.model.MNDischargeRates;
import mimickit.model.SOEASYEnvironment;
import mimickit.model.neuron.InnervatedNeuron;
import mimickit.model.neuron.Neuron;
import mimickit.model.neuron.RunningNeuron;
import mimickit.model.viewer.ACExciteSensoryNeuronsRandomly;
import mimickit.model.viewer.Viewer;
import mimickit.util.ExactISIGenerator;
import mimickit.util.ISIGenerator;
import mimickit.util.PeriStimulusTimeHistogram;
import mimickit.util.Reflex;
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
			MNDischargeRates dataSet = SOEASYEnvironment.getInstance().getDataSet();			

			PeriStimulusTimeHistogram psth = new PeriStimulusTimeHistogram(
					dataSet.getReferenceTriggerDataFile(),
					dataSet.getReferenceMNDischargeDataFile());
			Reflex hReflex = psth.getHReflex();
			double hReflexLatency = hReflex.getLatency();
			hReflexLatency++;
			System.out.println("h-reflex latency: " + hReflexLatency);
			double axonalDelay = (hReflexLatency) / 2;

			int numberOfAfferents = 18;
			InnervatedNeuron afferentNeuron = getAgentFactory()
					.createInnervatedNeuron(null, "AfferentNeuron", true,
							numberOfAfferents, axonalDelay, null);

			ISIGenerator isiGenerator = new ExactISIGenerator(
					dataSet.getReferenceTriggerDataFile(),
					dataSet.getReferenceMNDischargeDataFile());
			RunningNeuron motorNeuron = getAgentFactory().createRunningNeuron(
					null, "MotorNeuron", axonalDelay, isiGenerator, null);

			Neuron muscle = getAgentFactory().createMuscle();

			afferentNeuron.makeSynapseWith(motorNeuron);

			motorNeuron.makeSynapseWith(muscle);

			Viewer viewer = SOEASYEnvironment.getInstance()
					.createViewer(motorNeuron, muscle);

			viewer.scheduleOneTimeAction(1000, 0,
					new ACExciteSensoryNeuronsRandomly(viewer));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
