package mimickit.model.viewer;

import mimickit.amas.Action;
import mimickit.amas.CooperativeAgent;
import mimickit.amas.Feedback;
import mimickit.model.FInstantFrequency;
import mimickit.model.MNDischargeRates;
import mimickit.model.SOEASYEnvironment;
import mimickit.model.SOEASYParameters;
import mimickit.util.StaticPSF;

import org.jfree.data.Range;

public class ACCheckInstantFrequency extends Action {

	private Viewer viewer;

	private StaticPSF simulatedPSF;

	private double latency = 0.0;

	private MNDischargeRates dataSet;

	public ACCheckInstantFrequency(CooperativeAgent ownerAgent) {
		super(ownerAgent);
	}

	@Override
	public void execute() {
		this.viewer = (Viewer) getOwnerAgent();
		// if the Viewer agent is allowed to send feedbacks
		if (SOEASYParameters.getInstance().isSendingFeedback()) {
			checkInstantFrequency();
		}
	}

	private void checkInstantFrequency() {
		SOEASYParameters parameters = SOEASYParameters.getInstance();
		this.dataSet = SOEASYEnvironment.getInstance().getDataSet();
		this.simulatedPSF = dataSet.getSimulatedPSF();

		latency = simulatedPSF.getLatencyOfLastMnDischarge();

		int referenceTriggerCount = dataSet.getReferenceTriggerCount();
		int simulatedTriggerCount = dataSet.getSimulatedTriggers().size();
		double mod = (simulatedTriggerCount - referenceTriggerCount) % 30;

		if (!Double.isNaN(latency)
				&& (simulatedPSF.getDischargeRates().size() > 1) && (mod == 0)) {
			// if the response occurs in a reasonable time...
			Range trainingRange = parameters.getTrainingRange(); 
			if (trainingRange.contains(latency)) {
				evaluateSmoothedDerivative();
			}
		}
	}

	private void evaluateSmoothedDerivative() {
		int psfComparison = dataSet.comparePSFCUSUMsAt(latency);

		if (psfComparison == 1) {
			sendFrequencyFeedback(Feedback.DECREASE);
		} else if (psfComparison == -1) {
			sendFrequencyFeedback(Feedback.INCREASE);
		} else {
			sendFrequencyFeedback(Feedback.GOOD);
		}
	}

	private void sendFrequencyFeedback(String feedbackType) {
		Feedback feedback = new FInstantFrequency(viewer, feedbackType);
		feedback.setDetail("(latency=" + latency + ")");
		viewer.sendFeedback(viewer.getMotorNeuron(), feedback);
		viewer.getLastOutgoingFeedbacksByLatency().put(latency, feedback);
	}
}
