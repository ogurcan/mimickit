package mimickit.model.neuron;

import mimickit.amas.CooperativeAgent;
import mimickit.model.SOEASYParameters;
import mimickit.model.Spike;
import mimickit.util.CriticalityFunction;
import fr.irit.smac.util.avt.AVT;
import fr.irit.smac.util.avt.AVTBuilder;
import fr.irit.smac.util.avt.Feedback;
import repast.simphony.engine.schedule.IAction;
import repast.simphony.engine.schedule.ScheduleParameters;

/**
 * This class represents and axon and the synapse in its ending. An axon or
 * nerve fiber is a long, slender projection of a nerve cell, or neuron, that
 * conducts electrical impulses away from the neuron's cell body or soma. A
 * synapse is a junction that permits a neuron to pass an electrical or chemical
 * signal to another cell.
 * 
 * @author Önder Gürcan
 * 
 */
public class Synapse {

	protected Neuron presynapticCell;

	protected Neuron postsynapticCell;

	/**
	 * Delay between this neuron (presynaptic) and its correspondent
	 * postsynaptic neuron. Delay can change from synapse to synapse depending
	 * on the length and type of the axon. Since there is only one synapse here,
	 * we used only one delay parameter.
	 */
	private double axonalDelay;

	/**
	 * Represents the strength of the synapse in terms of voltage. Synaptic
	 * strength can vary between 0.2 and 0.5 mV.
	 */
	private AVT avtSynapticStrength;

	/** Not Tunable Fields **/

	/**
	 * The time of occurence of the latest spike.
	 */
	private double lastSpikeTime;

	/**
	 * The time of creation of this synapse.
	 */
	private double creationTime;

	/** Initialization Methods **/

	public Synapse(CooperativeAgent creatorAgent, Neuron presynapticCell,
			Neuron postsynapticCell, double axonalDelay) {
		// create a directed link from presynapticCell to postsynapticCell
		this.presynapticCell = presynapticCell;
		this.presynapticCell.addPostSynapse(this);

		this.postsynapticCell = postsynapticCell;
		this.postsynapticCell.addPreSynapse(this);

		// initial axonal is the given axonal delay
		setAxonalDelay(axonalDelay);
		// initially the last spike time is -1
		setLastSpikeTime(-1.0d);

		// initially synaptic strength is set to either 0.25 mV or -0.25 mV
		// according to the type of the neuron.
		this.avtSynapticStrength = new AVTBuilder().lowerBound(0.07)
				.upperBound(0.60).startValue(Spike.INITIAL_SYNAPTIC_POTENTIAL)
				.deltaMin(0.01).build();

		this.creationTime = SOEASYParameters.getInstance().getCurrentTick();
	}

	/** Simulation related methods **/

	public void conductSpike(final boolean isExcitatory) {
		final double spikeStartTime = SOEASYParameters.getInstance()
				.getCurrentTick();
		final double synapticStrength = getTotalSynapticStrength();
		// transfer the spike to the synapse after "axonalDelay" time steps.
		final double spikeTransferTime = spikeStartTime + this.getAxonalDelay();
		SOEASYParameters.schedule(
				ScheduleParameters.createOneTime(spikeTransferTime, 0, 0),
				new IAction() {
					public void execute() {
						// a spike occurs in this synapse
						Synapse.this.setLastSpikeTime(spikeTransferTime);

						// create post-synaptic potential and transfer it to the
						// post-synaptic neuron for 4.0 ms

						// t = 0.0 - immediately
						postsynapticCell
								.receivePostSynapticPotential(synapticStrength);

						for (double duration = 0.5; duration <= 3.5; duration += 0.5) {
							double pspTransferTime = spikeTransferTime
									+ duration;
							final double reducedStrength = synapticStrength
									* ((Spike.SPIKE_DURATION - duration) / Spike.SPIKE_DURATION);
							SOEASYParameters.schedule(ScheduleParameters
									.createOneTime(pspTransferTime,
											ScheduleParameters.LAST_PRIORITY),
									new IAction() {
										public void execute() {
											if (postsynapticCell != null) {
												postsynapticCell
														.receivePostSynapticPotential(reducedStrength);
											}
										}
									});
						}

					}
				});
	}

	private void setLastSpikeTime(double lastSpikeTime) {
		this.lastSpikeTime = lastSpikeTime;
	}

	public double getLastSpikeTime() {
		return lastSpikeTime;
	}

	/** Getter and setter methods **/

	public double getAxonalDelay() {
		return axonalDelay;
	}

	private void setAxonalDelay(double axonalDelay) {
		this.axonalDelay = axonalDelay;
	}

	/**
	 * Returns the synaptic strength of one synapse.
	 * 
	 * @return
	 */
	private double getSynapticStrength() {
		double synapticStrength = 0.0;
		Neuron ownerNeuron = getPresynapticNeuron();
		if (ownerNeuron != null) {
			if (ownerNeuron.isExcitatory()) {
				synapticStrength = avtSynapticStrength.getValue();
			} else {
				synapticStrength = avtSynapticStrength.getValue() * (-1);
			}
		}
		return synapticStrength;
	}

	/**
	 * Returns the synaptic strength of the neuron group.
	 * 
	 * @return
	 */
	public double getTotalSynapticStrength() {
		Neuron presynapticNeuron = getPresynapticNeuron();
		int numberOfNeurons = presynapticNeuron.getNumberOfCells();
		return getSynapticStrength() * numberOfNeurons;
	}

	/**
	 * Returns the presynaptic (owner) neuron.
	 */
	public Neuron getPresynapticNeuron() {
		return this.presynapticCell;
	}

	/**
	 * Returns the postsynaptic neuron.
	 */
	public Neuron getPostsynapticNeuron() {
		return this.postsynapticCell;
	}

	/**
	 * Tries to increase the synaptic strength. Return true if the increasement
	 * is successfull, false otherwise.
	 * 
	 * @return
	 */
	private boolean increaseSynapticStrength() {
		double previousValue = getSynapticStrength();
		Neuron neuron = getPresynapticNeuron();
		if (neuron.isExcitatory()) {
			this.avtSynapticStrength.adjustValue(Feedback.GREATER);
		} else {
			this.avtSynapticStrength.adjustValue(Feedback.LOWER);
		}
		double nextValue = getSynapticStrength();
		return previousValue != nextValue;
	}

	/**
	 * Tries to decrease the synaptic strength. Return true if the decreasement
	 * is successfull, false otherwise.
	 * 
	 * @return
	 */
	private boolean decreaseSynapticStrength() {
		double previousValue = getSynapticStrength();
		Neuron neuron = getPresynapticNeuron();
		if (neuron.isExcitatory()) {
			this.avtSynapticStrength.adjustValue(Feedback.LOWER);
		} else {
			this.avtSynapticStrength.adjustValue(Feedback.GREATER);
		}
		double nextValue = getSynapticStrength();
		return previousValue != nextValue;
	}

	public boolean adjustDepolarization(Feedback adjustmentType) {
		boolean adjustmentSucceeds = false;
		if (adjustmentType.equals(Feedback.GOOD)) {
			this.avtSynapticStrength.adjustValue(Feedback.GOOD);
			adjustmentSucceeds = true;
		} else if (adjustmentType.equals(Feedback.GREATER)) {
			adjustmentSucceeds = increaseSynapticStrength();
		} else if (adjustmentType.equals(Feedback.LOWER)) {
			adjustmentSucceeds = decreaseSynapticStrength();
		}
		return adjustmentSucceeds;
	}

	@Override
	public String toString() {
		return presynapticCell + " -> " + postsynapticCell;
	}

	public double getCriticality() {
		double criticality = 0.0;
		double postSynapticPotential = avtSynapticStrength.getValue();
		if (postSynapticPotential <= 0.30) {
			CriticalityFunction cf = new CriticalityFunction(0.07f, 0.30f,
					0.0f, 40.0f, 0.15f);
			criticality = cf.compute((float) postSynapticPotential);
		} else {
			CriticalityFunction cf = new CriticalityFunction(0.00f, 0.60f,
					0.0f, 60.0f, 0.30f);
			criticality = cf.compute((float) postSynapticPotential);
		}
		return criticality;
	}

	public void kill() {
		this.presynapticCell.removePostSynapse(this);
		this.presynapticCell = null;
		//
		this.postsynapticCell.removePreSynapse(this);
		this.postsynapticCell = null;
	}

	protected double getDeltaCriticality() {
		double deltaMin = this.avtSynapticStrength.getAdvancedAVT().getDeltaManager().getAdvancedDM().getDeltaMin();
		double deltaMax = this.avtSynapticStrength.getAdvancedAVT().getDeltaManager().getAdvancedDM().getDeltaMax();
		double currentDelta = this.avtSynapticStrength.getAdvancedAVT().getDeltaManager().getDelta();
		double deltaCriticality = ((currentDelta - deltaMin) / (deltaMax - deltaMin)) * 100.0;
		return deltaCriticality;
	}

	public double getCreationTime() {
		return creationTime;
	}
}
