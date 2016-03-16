package test.soeasy.macrolevel.hreflex;

import java.util.List;
import java.util.Vector;

import mimickit.model.SOEASYEnvironment;
import mimickit.model.neuron.InnervatedNeuron;
import mimickit.model.neuron.Neuron;
import mimickit.model.neuron.RunningNeuron;
import mimickit.model.viewer.ACExciteSensoryNeuronsRandomly;
import mimickit.model.viewer.Viewer;
import mimickit.util.DataFile;
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
			PeriStimulusTimeHistogram psth = new PeriStimulusTimeHistogram(
					DataFile.EXP_CV221_TRIGGER, DataFile.EXP_CV221_MNDISCHARGE);
			Reflex hReflex = psth.getHReflex();
			double hReflexLatency = hReflex.getLatency();
			System.out.println("h-reflex latency: " + hReflexLatency);
			double axonalDelay = hReflexLatency / 2;

			int numberOfAfferents = 20;
			InnervatedNeuron afferentNeuron = getAgentFactory()
					.createInnervatedNeuron(null, "AfferentNeuron", true,
							numberOfAfferents, axonalDelay, null);

			ISIGenerator isiGenerator = new ExactISIGenerator(
					DataFile.EXP_CV221_TRIGGER, DataFile.EXP_CV221_MNDISCHARGE);
			RunningNeuron motorNeuron = getAgentFactory().createRunningNeuron(
					null, "MotorNeuron", axonalDelay, isiGenerator, null);

			List<Neuron> preNeurons = new Vector<Neuron>();
			preNeurons.add(afferentNeuron);
			// preNeurons.add(motorNeuron);
			getAgentFactory().createRestingNeuron(motorNeuron, false,
					preNeurons, motorNeuron, null);
			getAgentFactory().createRestingNeuron(motorNeuron, false,
					preNeurons, motorNeuron, null);
			getAgentFactory().createRestingNeuron(motorNeuron, false,
					preNeurons, motorNeuron, null);
			getAgentFactory().createRestingNeuron(motorNeuron, false,
					preNeurons, motorNeuron, null);
			getAgentFactory().createRestingNeuron(motorNeuron, false,
					preNeurons, motorNeuron, null);
			getAgentFactory().createRestingNeuron(motorNeuron, false,
					preNeurons, motorNeuron, null);

			Neuron muscle = getAgentFactory().createMuscle();

			afferentNeuron.makeSynapseWith(motorNeuron);

			motorNeuron.makeSynapseWith(muscle);

			Viewer viewer = SOEASYEnvironment.getInstance().createViewer(
					motorNeuron, muscle, null);

			viewer.scheduleOneTimeAction(500, 0,
					new ACExciteSensoryNeuronsRandomly(viewer));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
