package mimickit.model.neuron;

import java.util.List;

import mimickit.amas.Action;
import mimickit.amas.CooperativeAgent;
import mimickit.amas.Feedback;
import mimickit.model.FDepolarization;
import mimickit.model.SOEASYEnvironment;

/**
 * This action is executed at every 3000 ms after the creation of the neuron
 * agent to control whether the neuron agent has been depolarized during a long
 * period of time or not.
 * 
 * @author Önder Gürcan
 * 
 */
public class ACCheckDepolarization extends Action {

	public ACCheckDepolarization(CooperativeAgent ownerAgent) {
		super(ownerAgent);
	}

	@Override
	public void execute() {
		Neuron owner = (Neuron) getOwnerAgent();
		double lastSpikeTime = owner.getLastSpikeTime();
		double currentTick = getSimulationEnvironment().getCurrentTick();		
		double diff = currentTick - lastSpikeTime;
		
		double depolarionTimeOut = 3000.0;
		double deathTimeOut = depolarionTimeOut * 20;
		
		if ((diff > depolarionTimeOut) && (diff < (deathTimeOut))) {
			List<Neuron> preNeurons = owner.getPreNeurons();
			if (!preNeurons.isEmpty()) {
				Feedback fDepolarization = new FDepolarization(owner,
						Feedback.INCREASE);
				owner.sendFeedback(preNeurons, fDepolarization);
			}
		} else if (diff >= (deathTimeOut)) {
			// if this agent is not active for long time,
			// it is to time to kill itself.
			SOEASYEnvironment.getInstance().removeNeuron(owner);
		}
	}
}
