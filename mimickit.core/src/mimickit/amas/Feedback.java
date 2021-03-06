package mimickit.amas;

import java.util.ArrayList;
import java.util.List;

import mimickit.model.SOEASYParameters;

abstract public class Feedback {

	public static final String GOOD = "GOOD";
	public static final String INCREASE = "INCREASE";
	public static final String DECREASE = "DECREASE";	

	private CooperativeAgent source;
	private String feedbackType;
	private double creationTime;
	private boolean isHandled;
	//private String details;
	private List<CooperativeAgent> sendersHistory;
	private String detail;	
	
	public Feedback(CooperativeAgent source, String feedbackType) {
		this.source = source;		
		this.feedbackType = feedbackType;		
		this.creationTime = SOEASYParameters.getInstance()
				.getCurrentTick();
		this.isHandled = false;
		//this.details = details;
		this.sendersHistory = new ArrayList<CooperativeAgent>();		
	}
	
	public void setSource(CooperativeAgent source) {
		this.source = source;
	}

	public CooperativeAgent getSource() {
		return source;
	}

	public String getType() {
		return feedbackType;
	}
	
	public String getInverseType() {
		if (getType().equals(GOOD)) {
			return Feedback.GOOD;
		} else if (getType().equals(INCREASE)) {
			return Feedback.DECREASE;
		} else if (getType().equals(DECREASE)) {
			return Feedback.INCREASE;
		} else {
			return null;
		}
	}

	public fr.irit.smac.util.avt.Feedback getAVTType() {
		if (getType().equals(GOOD)) {
			return fr.irit.smac.util.avt.Feedback.GOOD;
		} else if (getType().equals(INCREASE)) {
			return fr.irit.smac.util.avt.Feedback.GREATER;
		} else if (getType().equals(DECREASE)) {
			return fr.irit.smac.util.avt.Feedback.LOWER;
		} else {
			return null;
		}
	}

	public boolean isGood() {
		return getType().equals(GOOD);
	}

	public boolean isIncrease() {
		return getType().equals(INCREASE);
	}

	public boolean isDecrease() {
		return getType().equals(DECREASE);
	}

	public double getCreationTime() {
		return this.creationTime;
	}	

	@Override
	public String toString() {
		return this.getClass().getName() + "." + "(" + this.getSource() + ")." + this.detail;
	}

	protected void handle() {
		this.isHandled = true;
	}

	public boolean isHandled() {
		return isHandled;
	}

	public boolean isSentPreviouslyBy(CooperativeAgent cooperativeAgent) {
		return this.sendersHistory.contains(cooperativeAgent);
	}

	public void addSender(CooperativeAgent senderAgent) {
		this.sendersHistory.add(senderAgent);
	}
	
	public void setDetail(String detail) {
		this.detail = detail;
	}
}
