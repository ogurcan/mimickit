package mimickit.model.viewer;

import java.util.Iterator;
import java.util.List;

import mimickit.amas.Action;
import mimickit.amas.CooperativeAgent;
import mimickit.model.SOEASYEnvironment;
import mimickit.model.SOEASYParameters;
import mimickit.model.neuron.InnervatedNeuron;
import repast.simphony.engine.schedule.IAction;
import repast.simphony.engine.schedule.ScheduleParameters;

/**
 * Excites (triggers) the sensory neuron(s) randomly at every 1000 to 3000 time
 * steps. randomly.
 */
public class ACExciteSensoryNeuronsRandomly extends Action {

	public ACExciteSensoryNeuronsRandomly(CooperativeAgent ownerAgent) {
		super(ownerAgent);
	}

	@Override
	public void execute() {
		final SOEASYEnvironment environment = SOEASYEnvironment.getInstance();
		final SOEASYParameters parameters = SOEASYParameters.getInstance();

		exciteAllSensoryNeurons();
		// also charge extra-cellular fluid
		environment.getExtracellularFluid().charge();

		int occurance = (int) parameters
				.getParameter(SOEASYParameters.STIMULUS_OCCURANCE);
		if (occurance > 1) {
			double consecutiveStimulusDelay = parameters.getParameter(SOEASYParameters.CONSECUTIVE_STIMULUS_DELAY);
			double currentTick = parameters.getCurrentTick();
			SOEASYParameters.schedule(ScheduleParameters.createOneTime(
					currentTick + consecutiveStimulusDelay,
					ScheduleParameters.FIRST_PRIORITY, 0), new IAction() {
				@Override
				public void execute() {
					exciteAllSensoryNeurons();
					// also charge extra-cellular fluid
					environment.getExtracellularFluid().charge();
				}
			});
		}

		scheduleNextExcitation();
	}

	private void exciteAllSensoryNeurons() {
		// excite the sensory neuron(s) directly.
		SOEASYEnvironment environment = SOEASYEnvironment.getInstance();
		List<InnervatedNeuron> sensoryNeurons = environment.getSensoryNeurons();
		Iterator<InnervatedNeuron> iterator = sensoryNeurons.iterator();
		while (iterator.hasNext()) {
			iterator.next().innervate();
		}

		// record given input signal for further comparison with outputs
		double currentTick = SOEASYParameters.getInstance().getCurrentTick();
		environment.getDataSet().addSimulatedTrigger(currentTick);
	}

	private void scheduleNextExcitation() {
		Viewer viewer = (Viewer) getOwnerAgent();
		double currentTick = SOEASYParameters.getInstance().getCurrentTick();

		// generate random stimulation between 1 second and 3 seconds.
		int delayForNextStimulation = ((int) (Math.random() * 2000)) + 1000;

		// calculate the the time for the next trigger
		double nextTrigger = currentTick + delayForNextStimulation;

		ACExciteSensoryNeuronsRandomly action = new ACExciteSensoryNeuronsRandomly(
				viewer);
		SOEASYParameters.schedule(ScheduleParameters.createOneTime(nextTrigger,
				ScheduleParameters.FIRST_PRIORITY, 0), action);
	}

}