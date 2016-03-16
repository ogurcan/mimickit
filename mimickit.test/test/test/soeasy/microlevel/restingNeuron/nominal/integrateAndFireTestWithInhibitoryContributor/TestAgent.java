package test.soeasy.microlevel.restingNeuron.nominal.integrateAndFireTestWithInhibitoryContributor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Vector;

import mimickit.model.SOEASYEnvironment;
import mimickit.model.neuron.Neuron;
import mimickit.model.neuron.RestingNeuron;
import mimickit.model.neuron.SpikeObserver;
import mimickit.model.neuron.Synapse;
import rast.AbstractTestAgent;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * 
 * @author Önder Gürcan
 * 
 */
public class TestAgent extends AbstractTestAgent implements SpikeObserver {

	private RestingNeuron restingNeuron;

	private Synapse synapse1, synapse2, synapse3, synapse4, synapse5, synapse6,
			synapse7, synapse8, synapse9, synapse10, synapse11;

	private Vector<Double> firingVector;

	/** INITIALIZATION METHOD **/

	public TestAgent() {
		restingNeuron = (RestingNeuron) SOEASYEnvironment.getInstance().getAgent(
				"RestingNeuron");
		restingNeuron.addSpikeObserver(this);

		synapse1 = restingNeuron.getPreSynapses().get(0);
		synapse2 = restingNeuron.getPreSynapses().get(1);
		synapse3 = restingNeuron.getPreSynapses().get(2);
		synapse4 = restingNeuron.getPreSynapses().get(3);
		synapse5 = restingNeuron.getPreSynapses().get(4);
		synapse6 = restingNeuron.getPreSynapses().get(5);
		synapse7 = restingNeuron.getPreSynapses().get(6);
		synapse8 = restingNeuron.getPreSynapses().get(7);
		synapse9 = restingNeuron.getPreSynapses().get(8);
		synapse10 = restingNeuron.getPreSynapses().get(9);
		synapse11 = restingNeuron.getPreSynapses().get(10);
		//
		firingVector = new Vector<Double>();
	}

	@ScheduledMethod(start = 10, interval = 0, priority = ScheduleParameters.LAST_PRIORITY)
	public void innervateSynapses() {
		synapse1.conductSpike(true);
		synapse2.conductSpike(true);
		synapse3.conductSpike(true);
		synapse4.conductSpike(true);
		synapse5.conductSpike(true);
		synapse6.conductSpike(false);
		synapse7.conductSpike(true);
		synapse8.conductSpike(true);
		synapse9.conductSpike(true);
		synapse10.conductSpike(true);
		synapse11.conductSpike(true);
	}

	/** TEST METHODS **/

	/**
	 * When you stimulate all the synapses at time 10.0, the neuron under test
	 * has to fire at time 23.0 (exatly at the same time with synapse7 and
	 * synapse8).
	 */
	@ScheduledMethod(start = ScheduleParameters.END, interval = 0, priority = ScheduleParameters.LAST_PRIORITY)
	public void testIntegrateAndFire() {
		assertEquals(1, firingVector.size());
		double timeOfFiring = firingVector.get(0);
		// the time of firing must be the same with the latest integrated
		// synapse.
		assertEquals(timeOfFiring, synapse7.getLastSpikeTime(), 0.0);
		assertEquals(timeOfFiring, synapse8.getLastSpikeTime(), 0.0);
		//
		Vector<Synapse> contributedSynapses = restingNeuron
				.getContributedSynapses();
		assertNotNull(contributedSynapses);
		assertEquals(6, contributedSynapses.size());
		assertFalse(contributedSynapses.contains(synapse1));
		assertFalse(contributedSynapses.contains(synapse2));
		assertTrue(contributedSynapses.contains(synapse3));
		assertTrue(contributedSynapses.contains(synapse4));
		assertTrue(contributedSynapses.contains(synapse5));
		assertTrue(contributedSynapses.contains(synapse6));
		assertTrue(contributedSynapses.contains(synapse7));
		assertTrue(contributedSynapses.contains(synapse8));
		assertFalse(contributedSynapses.contains(synapse9));
		assertFalse(contributedSynapses.contains(synapse10));
		assertFalse(contributedSynapses.contains(synapse11));
		//
		Vector<Neuron> candidateNeurons = restingNeuron.getCandidateNeurons();
		assertNotNull(candidateNeurons);
		assertEquals(candidateNeurons.size(), 1);
	}

	@Override
	public void update(Neuron spikingNeuron, double spikeTime) {
		firingVector.add(spikeTime);
	}
}
