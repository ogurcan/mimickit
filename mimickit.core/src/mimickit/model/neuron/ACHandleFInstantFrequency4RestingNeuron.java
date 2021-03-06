package mimickit.model.neuron;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import mimickit.amas.Action;
import mimickit.amas.CooperativeAgent;
import mimickit.amas.Feedback;
import mimickit.model.SOEASYEnvironment;
import mimickit.model.SOEASYParameters;
import mimickit.model.Spike;

public class ACHandleFInstantFrequency4RestingNeuron extends Action {

	public ACHandleFInstantFrequency4RestingNeuron(CooperativeAgent ownerAgent) {
		super(ownerAgent);
	}

	@Override
	public void execute() {
		Neuron ownerNeuron = (Neuron) getOwnerAgent();
		Feedback feedback = getReceivedFeedback().getFeedback();

		if (ownerNeuron.isExcitatory() && (!feedback.isGood())) {
			List<Neuron> preNeurons = choosePreSynapticNeurons();
			Neuron postNeuron = (Neuron) feedback.getSource();
			Neuron newNeuron = SOEASYEnvironment.getInstance()
					.createRestingNeuron(ownerNeuron, true, preNeurons, null,
							feedback.toString());
			postNeuron.introduce(newNeuron);
			newNeuron.introduce(postNeuron);
		} else {
//			Feedback feedback2 = new FDepolarization(ownerNeuron, Feedback.GOOD);
//			ownerNeuron.sendFeedback(ownerNeuron.getPreNeurons(), feedback2);
		}
	}

	private List<Neuron> choosePreSynapticNeurons() {
		Neuron ownerNeuron = (Neuron) getOwnerAgent();
		List<Neuron> preNeurons = new ArrayList<Neuron>();
		double totalPSP = 0.0;
		// always try to include itself to the list
		if (ownerNeuron.isExcitatory()) {
			preNeurons.add(ownerNeuron);
			totalPSP = Spike.INITIAL_SYNAPTIC_POTENTIAL
					* ownerNeuron.getNumberOfCells();
		}
		List<Synapse> contributedSynapses = ownerNeuron
				.getContributedSynapses();
		sortByLastSpikeTimeInDescendingOrder(contributedSynapses);
		Iterator<Synapse> iterator = contributedSynapses.iterator();
		while (iterator.hasNext()) {
			Synapse synapse = iterator.next();
			double psp = synapse.getTotalSynapticStrength();
			if ((totalPSP < SOEASYParameters.getInstance().getRestingAhpLevel())
					&& (psp > 0.0)) {
				preNeurons.add(synapse.getPresynapticNeuron());
				totalPSP += psp;
			}
		}
		//
		return preNeurons;
	}

	private void sortByLastSpikeTimeInDescendingOrder(List<Synapse> preSynapses) {
		Collections.sort(preSynapses, new Comparator<Synapse>() {
			@Override
			public int compare(Synapse synapse1, Synapse synapse2) {
				int result = 0;
				double lastSpikeTime1 = synapse1.getLastSpikeTime();
				double lastSpikeTime2 = synapse2.getLastSpikeTime();
				if (lastSpikeTime1 > lastSpikeTime2) {
					result = Integer.MIN_VALUE;
				} else if (lastSpikeTime2 > lastSpikeTime1) {
					result = Integer.MAX_VALUE;
				}
				return result;
			}
		});
	}
}
