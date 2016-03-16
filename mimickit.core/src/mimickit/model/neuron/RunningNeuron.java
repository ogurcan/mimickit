package mimickit.model.neuron;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import mimickit.amas.CooperativeAgent;
import mimickit.model.FInstantFrequency;
import mimickit.model.SOEASYParameters;
import mimickit.model.Spike;
import mimickit.util.ISIGenerator;
import repast.simphony.engine.schedule.IAction;
import repast.simphony.engine.schedule.ScheduleParameters;

public class RunningNeuron extends Neuron {

	/**
	 * The instant potential increase used while this neuron is "tonic firing".
	 */
	protected double instantPotentialIncrease;

	private ISIGenerator isiDistribution;
	
	/**
	 * Indicates the period for each instant potential increase.
	 */
	private double instant = 0.5;

	private double criticality = 0.0;

	public RunningNeuron(CooperativeAgent creatorAgent, String identifier,
			boolean isExcitatory, int numberOfNeurons, double axonalDelay,
			double ahpLevel, ISIGenerator isiDistribution, String cause) {
		super(creatorAgent, identifier, isExcitatory, numberOfNeurons,
				axonalDelay, ahpLevel, cause);

		this.isiDistribution = isiDistribution;

		calculateInstantPotentialDifference();
	}

	@Override
	protected void defineActions() {
		defineFeedbackActionPair(FInstantFrequency.class,
				ACHandleFInstantFrequency4RunningNeuron.class);

		scheduleBehaveMethod();
	}

	protected void scheduleBehaveMethod() {
		double currentTick = SOEASYParameters.getInstance()
				.getCurrentTick();

		SOEASYParameters.schedule(ScheduleParameters.createRepeating(
				currentTick + instant, instant, ScheduleParameters.FIRST_PRIORITY, 0),
				new IAction() {
					@Override
					public void execute() {
						behave();
					}
				});
	}

	/**
	 * Used for generating continuous spike activity while this neuron is
	 * running.
	 */
	protected void calculateInstantPotentialDifference() {
		double interspikeInterval = isiDistribution.nextInterspikeInterval();
		interspikeInterval = interspikeInterval - Spike.SPIKE_DURATION;
		instantPotentialIncrease = getAhpLevel() / (interspikeInterval * 2);		
	}

	public ISIGenerator getIsiDistribution() {
		return isiDistribution;
	}

	@Override
	public void cooperate() {
		handleFeedbacksNEW();
	}

//	@Override
//	public double getCriticality() {
//		return this.criticality ;
//	}

	public void behave() {
		if (!isExcited()) {
			addToMembranePotential(instantPotentialIncrease);			
		}
	}

	@Override
	protected void excite() {
		double currentTime = SOEASYParameters.getInstance()
				.getCurrentTick();

		this.previousSpikeTime = lastSpikeTime;
		this.lastSpikeTime = currentTime;

		// propagate the spike to all its axons
		propagateSpike();

		notifySpikeObservers();

		// make this neuron go back to resting state after
		// "amplitude" time steps.
		SOEASYParameters.schedule(ScheduleParameters.createOneTime(
				currentTime + Spike.SPIKE_DURATION, 0, 0.0), new IAction() {
			public void execute() {
				setPotential(Spike.FIRING_THRESHOLD - getAhpLevel());
				calculateInstantPotentialDifference();
			}
		});
	}

	/**
	 * The synapses that have been active between the latest spike and the
	 * previous spike.
	 */
	public List<Synapse> getLastActiveSynapses() {
		Vector<Synapse> lastActiveSynapses = new Vector<Synapse>();
		Iterator<Synapse> iterator = this.getPreSynapses().iterator();
		while (iterator.hasNext()) {
			Synapse synapse = iterator.next();
			if ((synapse.getLastSpikeTime() <= this.getLastSpikeTime())
					&& (synapse.getLastSpikeTime() > this
							.getPreviousSpikeTime())) {
				lastActiveSynapses.add(synapse);
			}
		}

		return lastActiveSynapses;
	}

	/**
	 * The neurons (that this neuron knows) that have been active between the
	 * latest spike and the previous spike of this neuron.
	 */
	public List<Neuron> getLastActiveFriendNeurons() {
		List<Neuron> lastActiveNeurons = new ArrayList<Neuron>();
		Iterator<Neuron> iterator = getFriends().iterator();
		while (iterator.hasNext()) {
			Neuron neuron = iterator.next();
			if ((neuron.getLastSpikeTime() < this.getLastSpikeTime())
					&& (neuron.getLastSpikeTime() > this.getPreviousSpikeTime())) {
				lastActiveNeurons.add(neuron);
			}
		}

		return lastActiveNeurons;
	}
	
	/**
	 * The neurons (that this neuron knows) that have been active between the
	 * latest spike and the previous spike of this neuron.
	 */
	public List<Neuron> getLastActivePresynapticNeurons() {
		List<Neuron> lastActiveNeurons = new ArrayList<Neuron>();
		Iterator<Neuron> iterator = getPreNeurons().iterator();
		while (iterator.hasNext()) {
			Neuron neuron = iterator.next();
			if ((neuron.getLastSpikeTime() <= this.getLastSpikeTime())
					&& (neuron.getLastSpikeTime() > this.getPreviousSpikeTime())) {
				lastActiveNeurons.add(neuron);
			}
		}

		return lastActiveNeurons;
	}

//	public void recalculateCriticality(Feedback feedback) {
//		double x = 0.9;
//		double y = Boolean.compare(true, feedback.isGood());		
//		this.criticality = (x * (this.criticality / 100) + (1 - x) * y) * 100;
//	}	

}
