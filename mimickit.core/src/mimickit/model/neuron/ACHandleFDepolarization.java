package mimickit.model.neuron;

import java.util.List;

import mimickit.amas.Action;
import mimickit.amas.CooperativeAgent;
import mimickit.amas.Feedback;
import mimickit.model.SOEASYEnvironment;
import mimickit.model.SOEASYParameters;

public class ACHandleFDepolarization extends Action {

	private Neuron ownerNeuron, sourceNeuron;

	private Feedback feedback;

	private double annoyanceLevel, maxDistance;

	private double reorganization_annoyance_level;

	private double evolution_annoyance_level;

	public ACHandleFDepolarization(CooperativeAgent ownerAgent) {
		super(ownerAgent);
	}

	@Override
	public void execute() {
		this.ownerNeuron = (Neuron) getOwnerAgent();
		this.feedback = getReceivedFeedback().getFeedback();
		this.sourceNeuron = (Neuron) feedback.getSource();
		this.annoyanceLevel = ownerNeuron.getAnnoyanceLevel(feedback);

		SOEASYParameters soeasy = SOEASYParameters.getInstance();
		this.maxDistance = soeasy
				.getParameter(SOEASYParameters.HELPING_DISTANCE);
		reorganization_annoyance_level = soeasy
				.getParameter(SOEASYParameters.REORGANIZATION_ANNYOYANCE_LEVEL);
		evolution_annoyance_level = soeasy
				.getParameter(SOEASYParameters.EVOLUTION_ANNYOYANCE_LEVEL);
		
//		if (feedback.isGood()) {
//			Feedback feedback = new FDepolarization(ownerNeuron, Feedback.GOOD);
//			ownerNeuron.sendFeedback(ownerNeuron.getPreNeurons(), feedback);
//		}

		if (ownerNeuron.isPredecessor(sourceNeuron)) {
			helpPostSynapticNeuron();
		} else {
			helpNotPostSynapticNeuron();
		}
	}

	private void helpPostSynapticNeuron() {
		if (annoyanceLevel <= reorganization_annoyance_level) { // tuning
			Synapse postSynapse = ownerNeuron.getPostSynapse(sourceNeuron);
			boolean adjusted = false;
			adjusted = adjustSynapticStrength(postSynapse);
			if (!adjusted) {
				ownerNeuron.annoy(feedback);
				askCloseFriendsForHelp();
			} else {
				ownerNeuron.resetAnnoyance(feedback);
			}
		} else if ((annoyanceLevel > reorganization_annoyance_level)
				&& (annoyanceLevel <= evolution_annoyance_level)) { // re-organization
			if ((ownerNeuron.isExcitatory() && feedback.isDecrease())
					|| (ownerNeuron.isInhibitory() && feedback.isIncrease())) {
				removeSynapse();
				ownerNeuron.resetAnnoyance(feedback);
			} else {
				ownerNeuron.annoy(feedback);
				askCloseFriendsForHelp();
			}
		} else if (annoyanceLevel > evolution_annoyance_level) { // evolution
			if ((ownerNeuron.isExcitatory() && feedback.isIncrease())
					|| (ownerNeuron.isInhibitory() && feedback.isDecrease())) {
				createNeuron();
				ownerNeuron.resetAnnoyance(feedback);
			}
		}
	}

	private boolean adjustSynapticStrength(Synapse postSynapse) {
		boolean adjusted = postSynapse.adjustDepolarization(feedback
				.getAVTType());
//		if (this.ownerNeuron.getCriticality() > 90) {
//			if (feedback.getAVTType().equals(
//					fr.irit.smac.util.avt.Feedback.GREATER)) {
//				postSynapse
//						.adjustDepolarization(fr.irit.smac.util.avt.Feedback.LOWER);
//				adjusted = false;
//			} else if (feedback.getAVTType().equals(fr.irit.smac.util.avt.Feedback.LOWER)) {
//				postSynapse
//						.adjustDepolarization(fr.irit.smac.util.avt.Feedback.GREATER);
//				adjusted = false;
//			} else {
//				postSynapse
//						.adjustDepolarization(fr.irit.smac.util.avt.Feedback.LOWER);
//				adjusted = true;
//			}
//		}
		return adjusted;
	}

	private void helpNotPostSynapticNeuron() {
		if (annoyanceLevel <= reorganization_annoyance_level) { // tuning
			if ((feedback.isIncrease() && ownerNeuron.isExcitatory())
					|| (feedback.isDecrease() && ownerNeuron.isInhibitory())) {
				ownerNeuron.annoy(feedback);
				askCloseFriendsForHelp(); // ????
			} else {
				ownerNeuron.annoy(feedback);
				askCloseFriendsForHelp();
			}
		} else if ((annoyanceLevel > reorganization_annoyance_level)
				&& (annoyanceLevel <= evolution_annoyance_level)) { // re-organization
			if ((feedback.isIncrease() && ownerNeuron.isExcitatory())
					|| (feedback.isDecrease() && ownerNeuron.isInhibitory())) {
				// make a new synapse with the feedback source
				ownerNeuron.makeSynapseWith(sourceNeuron);
				ownerNeuron.resetAnnoyance(feedback);
			} else {
				ownerNeuron.annoy(feedback);
				askCloseFriendsForHelp();
			}
		} else if (annoyanceLevel > evolution_annoyance_level) { // evolution
			if ((ownerNeuron.isExcitatory() && feedback.isDecrease())
					|| (ownerNeuron.isInhibitory() && feedback.isIncrease())) {
				createInverseNeuron();
				ownerNeuron.resetAnnoyance(feedback);
			}
		}
	}

	private void askCloseFriendsForHelp() {
		double distance = sourceNeuron.getLastSpikeTime()
				- ownerNeuron.getLastSpikeTime();
		if (distance < maxDistance) {
			List<Neuron> closeFriendNeurons = ownerNeuron
					.getFriends(0.49, 0.51);
			ownerNeuron.sendFeedback(closeFriendNeurons, feedback);
		}
	}

	private void createNewSynchronousRestingNeuron(boolean isExcitatory) {
		if (ownerNeuron instanceof InnervatedNeuron) {
			System.out.println("AHA AMINA KOYİİM!!!");
		}

		Neuron ownerNeuron = (Neuron) getOwnerAgent();
		Feedback feedback = getReceivedFeedback().getFeedback();

		List<Neuron> presynapticNeurons = ownerNeuron.getPreNeurons();

		Neuron postsynapticNeuron = (Neuron) feedback.getSource();
		RestingNeuron newNeuron = SOEASYEnvironment.getInstance()
				.createRestingNeuron(ownerNeuron, isExcitatory,
						presynapticNeurons, postsynapticNeuron,
						feedback.toString());
		ownerNeuron.getFriends().add(newNeuron);
	}

	private void createNeuron() {
		createNewSynchronousRestingNeuron(ownerNeuron.isExcitatory());
	}

	private void createInverseNeuron() {
		createNewSynchronousRestingNeuron(!ownerNeuron.isExcitatory());
	}

	private void removeSynapse() {
		Synapse postSynapse = ownerNeuron.getPostSynapse(sourceNeuron);
		SOEASYEnvironment.getInstance().removeSynapse(postSynapse);
		if (ownerNeuron.getPreNeurons().isEmpty()) {
			SOEASYEnvironment.getInstance().removeNeuron(ownerNeuron);
		}
	}
}
