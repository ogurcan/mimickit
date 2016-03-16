package mimickit.amas;

import mimickit.model.SOEASYParameters;
import repast.simphony.engine.schedule.IAction;

public abstract class Action implements IAction {

	private CooperativeAgent ownerAgent;

	private ReceivedFeedback feedback;

	public Action(CooperativeAgent ownerAgent) {
		this.ownerAgent = ownerAgent;
	}

	@Override
	abstract public void execute();

	/**
	 * Used for Java Reflection.
	 * 
	 * @param feedback
	 */
	protected void setFeedback(ReceivedFeedback feedback) {
		this.feedback = feedback;
	}

	public ReceivedFeedback getReceivedFeedback() {
		return feedback;
	}

	public CooperativeAgent getOwnerAgent() {
		return ownerAgent;
	}

	protected SOEASYParameters getSimulationEnvironment() {
		return SOEASYParameters.getInstance();
	}

}
