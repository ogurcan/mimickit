package mimickit.amas;

public class ReceivedFeedback {
	
	private Feedback feedback;
	
	public ReceivedFeedback(Feedback feedback, boolean isBackwardPropagated) {
		this.feedback = feedback;
	}

	public Feedback getFeedback() {
		return feedback;
	}	

}
