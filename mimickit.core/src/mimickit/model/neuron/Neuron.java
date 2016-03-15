package mimickit.model.neuron;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import mimickit.amas.CooperativeAgent;
import mimickit.amas.Feedback;
import mimickit.model.FDepolarization;
import mimickit.model.SOEASYEnvironment;
import mimickit.model.SOEASYParameters;
import mimickit.model.Spike;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.IAction;
import repast.simphony.engine.schedule.ISchedulableAction;
import repast.simphony.engine.schedule.ScheduleParameters;

/**
 * This class represents either one single neuron or a group of neurons having
 * the same characteristics (e.g. firing synchronously).
 * 
 * @author Önder Gürcan <onder.gurcan@gmail.com>
 */
abstract public class Neuron extends CooperativeAgent {

	/**
	 * Indicates the membrane potential of this neuron. It changes between -70
	 * mV (resting potential) and 30 mV (peak potential). The potential of a
	 * neuron is normally -70. When a neuron is excited its membrane potential
	 * goes up to 30, then turns back to resting potential again.
	 */
	public double potential;

	/**
	 * Indicates whether this neuron is excitatory or not ("true" if excitatory,
	 * "false" if inhibitory).
	 */
	private boolean isExcitatory;

	/**
	 * Indicates the number of neuron this neuron object represents.
	 */
	protected int numberOfCells;

	/**
	 * Unique identifier of this neuron object.
	 */
	private String identifier;

	/**
	 * Represents the initial delay between this neuron (presynaptic) and its
	 * correspondent postsynaptic neurons. This information is used when a new
	 * synapse is created.
	 */
	private double axonalDelay;

	/**
	 * Keeps the last time this neuron generated a spike.
	 */
	protected double lastSpikeTime;

	/**
	 * Keeps the previous time this neuron generated a spike.
	 */
	protected double previousSpikeTime;

	/**
	 * The "after hyper-polarization" (AHP) level for this neuron in mV.
	 */
	private double ahpLevel;

	/**
	 * Keeps the synapses incoming to this neuron.
	 */
	private List<Synapse> preSynapses;

	/**
	 * Keeps the synapses outgoing from this neuron.
	 */
	private List<Synapse> postSynapses;

	/**
	 * Keeps all the neuron agents this neuron agent contacted during its
	 * lifetime, even the synapses in-between removed after some time.
	 */
	private List<Neuron> friends;

	/**
	 * Keeps the objects that are observing the spikes of this neuron agent.
	 * Whenever this neuron agent generates a spike, it immediately notifies the
	 * observers in this list.
	 */
	private List<SpikeObserver> observers;

	/**
	 * Default constructor of the Neuron agent.
	 * 
	 * @param creatorAgent
	 * @param identifier
	 * @param isExcitatory
	 * @param numberOfNeurons
	 * @param axonalDelay
	 * @param ahpLevel
	 * @param cause
	 */
	protected Neuron(CooperativeAgent creatorAgent, String identifier,
			boolean isExcitatory, int numberOfNeurons, double axonalDelay,
			double ahpLevel, String cause) {
		super(creatorAgent, cause);
		// set the identifier of the neuron
		setIdentifier(identifier);
		// set if this neuron excitatory or not.
		setExcitatory(isExcitatory);
		// initialize the number of neurons
		this.numberOfCells = numberOfNeurons;
		// set the default axonal delay
		setAxonalDelay(axonalDelay);
		// set the AHP level for this neuron
		this.ahpLevel = ahpLevel;
		// set the initial potential of this neuron
		setPotential(Spike.FIRING_THRESHOLD - getAhpLevel());
		//
		this.lastSpikeTime = SOEASYParameters.getInstance().getCurrentTick() - 1;
	}

	public void addSpikeObserver(SpikeObserver observer) {
		getSpikeObservers().add(observer);
	}

	/**
	 * General Neuron behaviors are defined here.
	 */
	@Override
	protected void defineActions() {
		defineFeedbackActionPair(FDepolarization.class,
				ACHandleFDepolarization.class);
	}

	protected void excite() {
		this.potential = Spike.PEAK_POTENTIAL;

		double currentTime = SOEASYParameters.getInstance().getCurrentTick();

		this.previousSpikeTime = lastSpikeTime;
		this.lastSpikeTime = currentTime;

		// propagate the spike to all its post-synapses
		propagateSpike();

		// notify observers if there are any
		notifySpikeObservers();
		
		SOEASYParameters.schedule(
				ScheduleParameters.createOneTime(currentTime
						+ Spike.SPIKE_DURATION + 0.5, 0.0),
				new ACSendSpikeFeedbacks(Neuron.this));
	}

	protected void notifySpikeObservers() {
		double currentTick = SOEASYParameters.getInstance().getCurrentTick();
		Iterator<SpikeObserver> iterator = getSpikeObservers().iterator();
		while (iterator.hasNext()) {
			SpikeObserver observer = iterator.next();
			observer.update(this, currentTick);
		}
	}

	public List<SpikeObserver> getSpikeObservers() {
		if (this.observers == null) {
			this.observers = new ArrayList<SpikeObserver>();
		}
		return observers;
	}

	/**
	 * Propagates the spike to all its post-synapses.
	 * 
	 */
	protected void propagateSpike() {
		Iterator<Synapse> postSynapseIterator = getPostSynapses().iterator();
		while (postSynapseIterator.hasNext()) {
			// for each post-synapse...
			Synapse postSynapse = postSynapseIterator.next();
			// ...conduct the spike
			postSynapse.conductSpike(isExcitatory);
		}
	}

	/**
	 * predecessor ---> successor
	 */
	public boolean isPredecessor(Neuron secondCell) {
		boolean isPredecessor = false;
		// check for real links
		Iterator<Synapse> iterator = this.getPostSynapses().iterator();
		while (iterator.hasNext()) {
			Neuron postsynapticNeuron = iterator.next().getPostsynapticNeuron();
			isPredecessor = isPredecessor
					|| (secondCell.equals(postsynapticNeuron));
		}

		return isPredecessor;
	}

	protected void addToMembranePotential(double potentialToBeAdded) {
		if (!isExcited()) {
			this.setPotential(this.getPotential() + potentialToBeAdded);
		}
	}

	protected void removeFromMembranePotential(double potentialToBeRemoved) {
		if (!isExcited()) {
			this.setPotential(this.getPotential() - potentialToBeRemoved);
		}
	}

	/**
	 * Creates and returns a synapse.
	 * 
	 * @param postSynapticCell
	 * @return
	 */
	public Synapse makeSynapseWith(Neuron postSynapticCell) {
		Synapse synapse = SOEASYEnvironment.getInstance().createSynapse(this,
				this, postSynapticCell, this.axonalDelay);

		return synapse;
	}

	/** Getter and Setter methods **/

	/**
	 * This method is "public" for watching purposes.
	 */
	public double getPotential() {
		return this.potential;
	}

	protected void setPotential(double potential) {
		this.potential = potential;

		if (this.potential >= Spike.FIRING_THRESHOLD) {
			excite();
		}
	}

	public boolean isExcited() {
		return this.getPotential() >= Spike.FIRING_THRESHOLD;
	}

	public boolean isExcitatory() {
		return isExcitatory;
	}

	public boolean isInhibitory() {
		return !isExcitatory;
	}

	protected void setExcitatory(boolean isExcitatory) {
		this.isExcitatory = isExcitatory;
	}

	public int getNumberOfCells() {
		return this.numberOfCells;
	}

	private void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	private void setAxonalDelay(double axonalDelay) {
		this.axonalDelay = axonalDelay;
	}

	public double getAxonalDelay() {
		return axonalDelay;
	}

	/** other methods **/

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Neuron other = (Neuron) obj;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		return true;
	}

	public double getLastSpikeTime() {
		return this.lastSpikeTime;
	}

	public double getPreviousSpikeTime() {
		return this.previousSpikeTime;
	}

	public double getAhpLevel() {
		return ahpLevel;
	}

	@Override
	public String toString() {
		return this.identifier;
	}

	public List<Synapse> getPreSynapses() {
		if (this.preSynapses == null) {
			this.preSynapses = new Vector<Synapse>();
		}
		return this.preSynapses;
	}

	/**
	 * Pre-friends of this neurons, either with a synaptic connection or not.
	 * 
	 * @return
	 */
	public List<Neuron> getFriends() {
		if (this.friends == null) {
			this.friends = new ArrayList<Neuron>();
		}
		return this.friends;
	}

	public List<Neuron> getFriends(double from, double to) {
		return getSynchronicNeurons(getFriends(), from, to);
	}

	public List<Synapse> getPostSynapses() {
		if (this.postSynapses == null) {
			this.postSynapses = new Vector<Synapse>();
		}
		return this.postSynapses;
	}

	public void addPreSynapse(Synapse synapse) {
		getPreSynapses().add(synapse);
		getFriends().add(synapse.getPresynapticNeuron());
	}

	public void removePreSynapse(Synapse synapse) {
		getPreSynapses().remove(synapse);
	}

	public void addPostSynapse(Synapse synapse) {
		getPostSynapses().add(synapse);
		// getFriends().add(synapse.getPostsynapticNeuron());
	}

	public void removePostSynapse(Synapse synapse) {
		getPostSynapses().remove(synapse);
	}

	@Override
	protected List<CooperativeAgent> getNeighbourAgents() {
		// for neurons, the critically important neighbors are inSynapses
		List<CooperativeAgent> neighbourAgents = new Vector<CooperativeAgent>();
		neighbourAgents.addAll(getPreNeurons());
		neighbourAgents.addAll(getPostNeurons());
		return neighbourAgents;
	}

	@Override
	protected List<? extends CooperativeAgent> getInputAgents() {
		return getPreNeurons();
	}

	public boolean sendToCandidateNeurons(Feedback feedback) {
		return sendFeedback(getCandidateNeurons(), feedback);
	}

	public boolean sendToContributedNeurons(Feedback feedback) {
		return sendFeedback(getContributedNeurons(), feedback);
	}

	public boolean sendToMostContributedNeurons(Feedback feedback) {
		return sendFeedback(getMostContributedNeurons(), feedback);
	}

	public boolean sendToMostSynchronicNeurons(Feedback feedback) {
		return sendFeedback(getMostSynchronicNeurons(), feedback);
	}

	public boolean sendToAllPreNeurons(Feedback feedback) {
		return sendFeedback(getPreNeurons(), feedback);
	}

	public boolean sendToAllPostNeurons(Feedback feedback) {
		return sendFeedback(getPostNeurons(), feedback);
	}

	public Vector<Neuron> getCandidateNeurons() {
		Vector<Neuron> candidateNeurons = new Vector<Neuron>();
		Iterator<Synapse> iterator = this.getPreSynapses().iterator();
		while (iterator.hasNext()) {
			// if the last spike occurred on an synapse is between the last and
			// the previous spikes of the owner neuron,
			// it can be said that that this synapse contributed the owner
			// neuron for the last spike.
			Synapse nextSynapse = iterator.next();
			double difference = this.getLastSpikeTime()
					- nextSynapse.getLastSpikeTime();
			if ((difference > Spike.SPIKE_DURATION) && (difference <= 8.0)) {
				candidateNeurons.add(nextSynapse.getPresynapticNeuron());
			}
		}

		return candidateNeurons;
	}

	public Vector<Neuron> getContributedNeurons() {
		Vector<Neuron> contributedNeurons = new Vector<Neuron>();

		Iterator<Synapse> iterator = this.getPreSynapses().iterator();
		while (iterator.hasNext()) {
			// if the last spike occurred on an synapse is between the last and
			// the previous spikes of the owner neuron,
			// it can be said that that this synapse contributed the owner
			// neuron for the last spike.
			Synapse nextSynapse = iterator.next();
			double distance = this.getLastSpikeTime()
					- nextSynapse.getLastSpikeTime();
			if ((distance >= 0.0) && (distance <= Spike.SPIKE_DURATION)) {
				contributedNeurons.add(nextSynapse.getPresynapticNeuron());
			}
		}

		return contributedNeurons;
	}

	/**
	 * Return the neurons that activate nearly at the same time with this
	 * neuron.
	 * 
	 * @return
	 */
	public List<Neuron> getSynchronicNeurons(List<Neuron> neuronList,
			double from, double to) {
		List<Neuron> synchronicNeurons = new ArrayList<Neuron>();

		Iterator<Neuron> iterator = neuronList.iterator();
		while (iterator.hasNext()) {
			Neuron nextNeuron = iterator.next();
			double distance = this.getLastSpikeTime()
					- nextNeuron.getLastSpikeTime();
			if ((distance >= from) && (distance <= to)) {
				synchronicNeurons.add(nextNeuron);
			}
		}

		return synchronicNeurons;
	}

	/**
	 * Returns the neurons that are synchronous with the previous spike of this
	 * neuron.
	 * 
	 * @param neuronList
	 * @param from
	 * @param to
	 * @return
	 */
	public List<Neuron> getPreviousSynchronicNeurons(List<Neuron> neuronList,
			double from, double to) {
		List<Neuron> synchronicNeurons = new ArrayList<Neuron>();

		Iterator<Neuron> iterator = neuronList.iterator();
		while (iterator.hasNext()) {
			Neuron nextNeuron = iterator.next();
			double distance = this.getPreviousSpikeTime()
					- nextNeuron.getLastSpikeTime();
			if ((distance >= from) && (distance <= to)) {
				synchronicNeurons.add(nextNeuron);
			}
		}

		return synchronicNeurons;
	}

	/**
	 * Returns excitatory or inhitory neurons only.
	 * 
	 * @param neuronList
	 * @param from
	 * @param to
	 * @param isExcitatory
	 * @return
	 */
	public List<Neuron> getSynchronicNeurons(List<Neuron> neuronList,
			double from, double to, boolean isExcitatory) {
		List<Neuron> synchronicNeurons = new ArrayList<Neuron>();

		Iterator<Neuron> iterator = neuronList.iterator();
		while (iterator.hasNext()) {
			Neuron nextNeuron = iterator.next();
			if (nextNeuron.isExcitatory() == isExcitatory) {
				double distance = this.getLastSpikeTime()
						- nextNeuron.getLastSpikeTime();
				if ((distance >= from) && (distance <= to)) {
					synchronicNeurons.add(nextNeuron);
				}
			}
		}

		return synchronicNeurons;
	}

	/**
	 * 
	 */
	public Vector<Neuron> getMostSynchronicNeurons() {
		Vector<Neuron> mostSynchronicNeurons = new Vector<Neuron>();
		List<Neuron> synchronicNeurons = getSynchronicNeurons(
				this.getFriends(), 0.49, 0.51);
		if (!synchronicNeurons.isEmpty()) {
			sortByLastSpikeTime(synchronicNeurons);
			double referenceSpikeTime = synchronicNeurons.get(0)
					.getLastSpikeTime();
			Iterator<Neuron> iterator = synchronicNeurons.iterator();
			while (iterator.hasNext()) {
				Neuron neuron = iterator.next();
				if (neuron.getLastSpikeTime() == referenceSpikeTime) {
					mostSynchronicNeurons.add(neuron);
				} else {
					break;
				}
			}
		}
		return mostSynchronicNeurons;
	}

	/**
	 * 
	 */
	public Vector<Neuron> getMostContributedNeurons() {
		Vector<Neuron> mostContributedNeurons = new Vector<Neuron>();
		Vector<Neuron> contributedNeurons = getContributedNeurons();
		if (!contributedNeurons.isEmpty()) {
			sortByLastSpikeTime(contributedNeurons);
			double referenceSpikeTime = contributedNeurons.get(0)
					.getLastSpikeTime();
			Iterator<Neuron> iterator = contributedNeurons.iterator();
			while (iterator.hasNext()) {
				Neuron neuron = iterator.next();
				if (neuron.getLastSpikeTime() == referenceSpikeTime) {
					mostContributedNeurons.add(neuron);
				} else {
					break;
				}
			}
		}
		return mostContributedNeurons;
	}

	private void sortByLastSpikeTime(List<Neuron> neurons) {
		Collections.sort(neurons, new Comparator<Neuron>() {
			@Override
			public int compare(Neuron neuron1, Neuron neuron2) {
				int result = 0;
				double lastSpikeTime1 = neuron1.getLastSpikeTime();
				double lastSpikeTime2 = neuron2.getLastSpikeTime();
				if (lastSpikeTime1 < lastSpikeTime2) {
					result = Integer.MAX_VALUE;
				} else if (lastSpikeTime2 < lastSpikeTime1) {
					result = Integer.MIN_VALUE;
				}
				return result;
			}
		});
	}

	public Vector<Synapse> getContributedSynapses() {
		Vector<Synapse> contributedSynapses = new Vector<Synapse>();

		Iterator<Synapse> iterator = this.getPreSynapses().iterator();
		while (iterator.hasNext()) {
			// if the last spike occurred on an synapse is between the last and
			// the previous spikes of the owner neuron,
			// it can be said that that this synapse contributed the owner
			// neuron for the last spike.
			Synapse nextSynapse = iterator.next();
			double distance = this.getLastSpikeTime()
					- nextSynapse.getLastSpikeTime();
			if ((distance >= 0.0) && (distance <= Spike.SPIKE_DURATION)) {
				contributedSynapses.add(nextSynapse);
			}
		}

		return contributedSynapses;
	}

	public List<Neuron> getPreNeurons() {
		List<Neuron> preNeurons = new Vector<Neuron>();
		Iterator<Synapse> iterator = getPreSynapses().iterator();
		while (iterator.hasNext()) {
			Synapse synapse = iterator.next();
			preNeurons.add(synapse.getPresynapticNeuron());
		}
		return preNeurons;
	}

	public List<Neuron> getPreNeurons(double from, double to) {
		return getSynchronicNeurons(getPreNeurons(), from, to);
	}

	public List<Neuron> getPreNeurons(double from, double to,
			boolean isExcitatory) {
		return getSynchronicNeurons(getPreNeurons(), from, to, isExcitatory);
	}

	public List<Neuron> getFriends(double from, double to, boolean isExcitatory) {
		return getSynchronicNeurons(getFriends(), from, to, isExcitatory);
	}

	public List<Neuron> getPostNeurons() {
		List<Neuron> postNeurons = new Vector<Neuron>();
		Iterator<Synapse> iterator = getPostSynapses().iterator();
		while (iterator.hasNext()) {
			Synapse synapse = iterator.next();
			postNeurons.add(synapse.getPostsynapticNeuron());
		}
		return postNeurons;
	}

	public Synapse getPostSynapse(Neuron source) {
		Iterator<Synapse> iterator = getPostSynapses().iterator();
		while (iterator.hasNext()) {
			Synapse synapse = iterator.next();
			if (synapse.getPostsynapticNeuron().equals(source)) {
				return synapse;
			}
		}
		return null;
	}

	public void receivePostSynapticPotential(final double postSynapticPotential) {
		if (!isExcited()) {
			this.addToMembranePotential(postSynapticPotential);

			double currentTick = SOEASYParameters.getInstance()
					.getCurrentTick();
			SOEASYParameters.schedule(
					ScheduleParameters.createOneTime(currentTick + 0.5, 0.0),
					new IAction() {
						public void execute() {
							removeFromMembranePotential(postSynapticPotential);
						}
					});
		}
	}

	/**
	 * Remove all scheduled actions of this neuron.
	 */
	public void kill() {
		double currentTick = SOEASYParameters.getInstance().getCurrentTick();
		SOEASYParameters.schedule(
				ScheduleParameters.createOneTime(currentTick + 1, 0.0),
				new IAction() {
					public void execute() {
						Iterator<ISchedulableAction> iterator = getScheduledRepeatingActions()
								.iterator();
						while (iterator.hasNext()) {
							ISchedulableAction iSchedulableAction = iterator
									.next();
							RunEnvironment.getInstance().getCurrentSchedule()
									.removeAction(iSchedulableAction);
						}
					}
				});
	}

	@Override
	protected boolean isBackwardPropagated(CooperativeAgent senderAgent) {
		return getPostNeurons().contains(senderAgent);
	}

	/**
	 * Used to introduce a new friend neuron to this neuron.
	 * @param newFriendNeuron
	 */
	public void introduce(Neuron newFriendNeuron) {
		friends.add(newFriendNeuron);		
	}

}
