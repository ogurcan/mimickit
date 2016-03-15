package mimickit.amas;

import fr.irit.smac.util.avt.AVT;
import fr.irit.smac.util.avt.AVTBuilder;

public class NonCooperativeSituation {
	
	private CooperativeAgent source;
	
	private String problem;
	
	private Feedback lastFeedback;
	
	private AVT annoyanceAVT;

	public NonCooperativeSituation(CooperativeAgent source, Feedback feedback) {
		this.source = source;
		this.problem = feedback.toString();
		this.annoyanceAVT = new AVTBuilder().deltaMin(1.0)
											.deltaMax(Math.pow(10.0, 100.0))
											.deltaIncreaseFactor(10.0)
											.deltaDecreaseFactor(10.0)
											.build();			
		this.annoyanceAVT.getAdvancedAVT().getDeltaManager().getAdvancedDM().setDelta(1.0);
		update(feedback);
	}
	
	public void update(Feedback feedback) {
		this.lastFeedback = feedback;
		this.annoyanceAVT.adjustValue(feedback.getAVTType());
	}
	
	public Feedback getLastFeedback() {
		return lastFeedback;
	}
	
	public boolean isItThisProblem(CooperativeAgent source, Feedback feedback) {
		boolean result = false;
		//
		if ((this.source == source)&&(this.problem.equals(feedback.toString()))) {
			result = true;
		}
		//
		return result;
	}
	
	public CooperativeAgent getSource() {
		return source;
	}
	
	public String getProblem() {
		return problem;
	}
	
	public double getAnnoyance() {
		double delta = this.annoyanceAVT.getAdvancedAVT().getDeltaManager().getDelta();
		return Math.log10(delta);
	}

}
