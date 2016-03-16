package test.mimickit;

import java.util.List;
import java.util.Vector;

import mimickit.amas.CooperativeAgent;


public class CooperativeTestAgent extends CooperativeAgent  {
	
	public CooperativeTestAgent(CooperativeAgent creatorAgent) {
		super(creatorAgent, "Test");
	}

	public CooperativeTestAgent() {
		super(null, "Test");
	}
	
	@Override
	protected void defineActions() {
		// do nothing
	}

	@Override
	final public double getCriticality() {
		return 0.0;
	}

	@Override
	final protected List<CooperativeAgent> getNeighbourAgents() {
		return new Vector<CooperativeAgent>();
	}

	@Override
	protected List<? extends CooperativeAgent> getInputAgents() {
		return new Vector<CooperativeAgent>();
	}
	
	@Override
	protected boolean isBackwardPropagated(CooperativeAgent senderAgent) {
		return false;
	}

}
