package mimickit.model.neuron;

import mimickit.amas.Action;
import mimickit.amas.CooperativeAgent;
import mimickit.amas.Feedback;
import mimickit.model.FDepolarization;

/**
 * This action is executed 5 ms after the depolarization of the neuron agent to
 * send feedbacks to the presynaptic neuron agents.
 * 
 * @author Önder Gürcan
 * 
 */
public class ACSendSpikeFeedbacks extends Action {

	public ACSendSpikeFeedbacks(CooperativeAgent ownerAgent) {
		super(ownerAgent);
	}

	@Override
	public void execute() {
		Neuron owner = (Neuron) getOwnerAgent();
		//
		Feedback fDepGood = new FDepolarization(owner, Feedback.GOOD);
		owner.sendToMostContributedNeurons(fDepGood);
	}

}
