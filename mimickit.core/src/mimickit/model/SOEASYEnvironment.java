package mimickit.model;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import mimickit.amas.CooperativeAgent;
import mimickit.model.neuron.InnervatedNeuron;
import mimickit.model.neuron.Neuron;
import mimickit.model.neuron.RestingNeuron;
import mimickit.model.neuron.RunningNeuron;
import mimickit.model.neuron.Synapse;
import mimickit.model.viewer.Viewer;
import mimickit.util.Averager;
import mimickit.util.DigitalizedXYSeries;
import mimickit.util.FastXYSeries;
import mimickit.util.ISIGenerator;
import mimickit.util.PeriStimulusTimeHistogram;

import org.apache.commons.collections15.Transformer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;

import repast.simphony.context.DefaultContext;
import repast.simphony.util.collections.IndexedIterable;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.io.GraphMLWriter;

public class SOEASYEnvironment extends DefaultContext<Object> {

	private static final String MUSCLE = "Muscle";

	private static SOEASYEnvironment instance = null;

	/**
	 * The agent-based neural network.
	 */
	private Graph<Neuron, Synapse> graph;

	private Viewer viewer;

	/**
	 * List of existing agents in this environment.
	 */
	private Hashtable<String, Object> existingAgents;

	/**
	 * List of removed agents in this environment.
	 */
	private Hashtable<String, Object> removedAgents;

	/**
	 * List of removed synapses in this environment.
	 */
	private Hashtable<String, Synapse> removedSynapses;

	/**
	 * Keeps track of the evolution of excitatory neuron agents.
	 */
	private List<XYDataItem> excitatoryNeuronEvolution;

	private List<XYDataItem> inhibitoryNeuronEvolution;

	private List<XYDataItem> excitatorySynapseEvolution;

	private List<XYDataItem> inhibitorySynapseEvolution;

	private int excitatoryNeuronCount = 0;

	private int inhibitoryNeuronCount = 0;

	private int excitatorySynapseCount = 0;

	private int inhibitorySynapseCount = 0;

	private List<XYDataItem> overallCriticalityEvolution;
	private List<XYDataItem> motoneuronCriticalityEvolution;
	private List<XYDataItem> sensoryNeuronCriticalityEvolution;

	private static int interNeuronNo = 0;

	private MNDischargeRates dataSet;

	private SOEASYEnvironment() {
		super("Agent Factory");
		this.existingAgents = new Hashtable<String, Object>();
		this.removedAgents = new Hashtable<String, Object>();
		this.removedSynapses = new Hashtable<String, Synapse>();
		this.graph = new DirectedSparseGraph<Neuron, Synapse>();
		//
		this.excitatoryNeuronEvolution = new ArrayList<XYDataItem>();
		this.inhibitoryNeuronEvolution = new ArrayList<XYDataItem>();
		this.excitatorySynapseEvolution = new ArrayList<XYDataItem>();
		this.inhibitorySynapseEvolution = new ArrayList<XYDataItem>();
		this.overallCriticalityEvolution = new ArrayList<XYDataItem>();
		this.motoneuronCriticalityEvolution = new ArrayList<XYDataItem>();
		this.sensoryNeuronCriticalityEvolution = new ArrayList<XYDataItem>();
	}

	public static SOEASYEnvironment getInstance() {
		if (instance == null) {
			instance = new SOEASYEnvironment();
		}
		return instance;
	}

	public MNDischargeRates createDataSet(String referenceTriggerDataFile,
			String referenceMNDischargeDataFile) {
		this.dataSet = new MNDischargeRates(referenceTriggerDataFile,
				referenceMNDischargeDataFile);
		return this.dataSet;
	}

	public MNDischargeRates getDataSet() {
		return this.dataSet;
	}

	public void reset() {
		this.existingAgents = new Hashtable<String, Object>();
		this.removedAgents = new Hashtable<String, Object>();
		this.removedSynapses = new Hashtable<String, Synapse>();
		this.graph = new DirectedSparseGraph<Neuron, Synapse>();

		createExtracellularFluid();
	}

	public void exportGraphML(String dataDir) {
		try {
			double currentTick = SOEASYParameters.getInstance()
					.getCurrentTick();

			GraphMLWriter<Neuron, Synapse> graphMLWriter = new GraphMLWriter<Neuron, Synapse>();
			FileWriter writer = new FileWriter(dataDir + "/graph_"
					+ currentTick + ".graphml");

			graphMLWriter.addEdgeData("axonal_delay", "null", "0.5",
					new Transformer<Synapse, String>() {
						@Override
						public String transform(Synapse synapse) {
							return String.valueOf(synapse.getAxonalDelay());
						}
					});

			graphMLWriter.addEdgeData("synaptic_strength", "null", "0.15",
					new Transformer<Synapse, String>() {
						@Override
						public String transform(Synapse synapse) {
							return String.valueOf(synapse
									.getTotalSynapticStrength());
						}
					});

			graphMLWriter.addVertexData("last_spike_time", "null", "0.0",
					new Transformer<Neuron, String>() {
						@Override
						public String transform(Neuron neuron) {
							return new DecimalFormat("000000000000.0")
									.format(neuron.getLastSpikeTime());
						}
					});

			graphMLWriter.addVertexData("delay", "null", "0.0",
					new Transformer<Neuron, String>() {
						@Override
						public String transform(Neuron neuron) {
							Neuron afferentNeuron = (Neuron) getAgent("AfferentNeuron");
							double referenceSpikeTime = afferentNeuron
									.getLastSpikeTime();
							return new DecimalFormat("000000000000.0")
									.format(neuron.getLastSpikeTime()
											- referenceSpikeTime);
						}
					});

			graphMLWriter.addVertexData("cause", "null", "0.0",
					new Transformer<Neuron, String>() {
						@Override
						public String transform(Neuron neuron) {
							String cause = neuron.getCause();
							if (cause == null) {
								return "";
							} else {
								return cause;
							}
						}
					});

			graphMLWriter.addVertexData("excitatory", "null", "true",
					new Transformer<Neuron, String>() {
						@Override
						public String transform(Neuron neuron) {
							return String.valueOf(neuron.isExcitatory());
						}
					});

			graphMLWriter.addVertexData("creator_agent", "null", "true",
					new Transformer<Neuron, String>() {
						@Override
						public String transform(Neuron neuron) {
							return String.valueOf(neuron.getCreatorAgent());
						}
					});

			graphMLWriter.addVertexData("creation_time", "null", "0.0",
					new Transformer<Neuron, String>() {
						@Override
						public String transform(Neuron neuron) {
							return new DecimalFormat("000000000000.0")
									.format(neuron.getCreationTime());
						}
					});

			graphMLWriter.addVertexData("r", "null", "255",
					new Transformer<Neuron, String>() {
						@Override
						public String transform(Neuron neuron) {
							String red = "255";
							if (neuron instanceof RunningNeuron) {
								red = "0";
							}
							if (neuron instanceof InnervatedNeuron) {
								red = "0";
							}
							if (neuron.getIdentifier().equals(MUSCLE)) {
								red = "255";
							} else {
								if (neuron.isExcitatory()) {
									red = "255";
								} else {
									red = "0";
								}
							}
							return red;
						}
					});

			graphMLWriter.addVertexData("g", "null", "255",
					new Transformer<Neuron, String>() {
						@Override
						public String transform(Neuron neuron) {
							String green = "255";
							if (neuron instanceof RunningNeuron) {
								green = "0";
							}
							if (neuron instanceof InnervatedNeuron) {
								green = "255";
							}
							if (neuron.getIdentifier().equals(MUSCLE)) {
								green = "0";
							} else {
								if (neuron.isExcitatory()) {
									green = "255";
								} else {
									green = "0";
								}
							}
							return green;
						}
					});

			graphMLWriter.addVertexData("b", "null", "0",
					new Transformer<Neuron, String>() {
						@Override
						public String transform(Neuron neuron) {
							String blue = "255";
							if (neuron instanceof RunningNeuron) {
								blue = "255";
							}
							if (neuron instanceof InnervatedNeuron) {
								blue = "0";
							}
							if (neuron.getIdentifier().equals(MUSCLE)) {
								blue = "0";
							} else {
								if (neuron.isExcitatory()) {
									blue = "0";
								} else {
									blue = "0";
								}
							}
							return blue;
						}
					});

			graphMLWriter.save(graph, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addCreatedNeuronToTheEnvironment(Neuron newNeuron) {
		this.add(newNeuron); // add to repast context
		this.existingAgents.put(newNeuron.toString(), newNeuron);
		this.graph.addVertex(newNeuron);
		//
		if (newNeuron.isExcitatory()) {
			this.excitatoryNeuronCount++;
		} else {
			this.inhibitoryNeuronCount++;
		}
		//
		updateEvolutionLists();
	}

	private void updateEvolutionLists() {
		double tick = SOEASYParameters.getInstance().getCurrentTick();
		this.excitatoryNeuronEvolution.add(new XYDataItem(tick,
				getNumberOfExcitatoryNeuronAgents()));
		this.inhibitoryNeuronEvolution.add(new XYDataItem(tick,
				getNumberOfInhibitoryNeuronAgents()));
		this.excitatorySynapseEvolution.add(new XYDataItem(tick,
				getNumberOfExcitatorySynapses()));
		this.inhibitorySynapseEvolution.add(new XYDataItem(tick,
				getNumberOfInhibitorySynapses()));
	}

	public RestingNeuron createRestingNeuron(CooperativeAgent creatorAgent,
			String identifier, boolean isExcitatory, int numberOfNeurons,
			double axonalDelay, double ahpLevel, String cause) {
		RestingNeuron neuron = new RestingNeuron(creatorAgent, identifier,
				isExcitatory, numberOfNeurons, axonalDelay, ahpLevel, cause);
		//
		addCreatedNeuronToTheEnvironment(neuron);
		//
		SOEASYParameters.getInstance().setStable(false);
		return neuron;
	}

	public RunningNeuron createRunningNeuron(CooperativeAgent creatorAgent,
			String identifier, double axonalDelay,
			ISIGenerator isiDistribution, String cause) {
		RunningNeuron neuron = new RunningNeuron(creatorAgent, identifier,
				true, 1, axonalDelay, Spike.RUNNING_AHP_LEVEL, isiDistribution,
				cause);
		//
		addCreatedNeuronToTheEnvironment(neuron);
		//
		SOEASYParameters.getInstance().setStable(false);
		return neuron;
	}

	public InnervatedNeuron createInnervatedNeuron(
			CooperativeAgent creatorAgent, String identifier,
			boolean isExcitatory, int numberOfNeurons, double axonalDelay,
			String cause) {
		InnervatedNeuron neuron = new InnervatedNeuron(creatorAgent,
				identifier, isExcitatory, numberOfNeurons, axonalDelay,
				SOEASYParameters.getInstance().getRestingAhpLevel(), cause);
		//
		addCreatedNeuronToTheEnvironment(neuron);
		//
		SOEASYParameters.getInstance().setStable(false);
		return neuron;
	}

	/**
	 * Used for evolution of Neurons
	 * 
	 * @param creatorAgent
	 * @param isExcitatory
	 * @param preSynapticNeurons
	 * @param postSynapticNeuron
	 * @return
	 */
	public RestingNeuron createRestingNeuron(CooperativeAgent creatorAgent,
			boolean isExcitatory, List<Neuron> preSynapticNeurons,
			Neuron postSynapticNeuron, String cause) {
		double axonalDelay = 0.5d;
		// first, create the new resting neuron
		RestingNeuron newNeuron = createRestingNeuron(creatorAgent,
				"InterNeuron" + interNeuronNo++, isExcitatory, 1, axonalDelay,
				SOEASYParameters.getInstance().getRestingAhpLevel(), cause);

		// then create its synapses
		// pre-synapses first...
		Iterator<Neuron> iterator = preSynapticNeurons.iterator();
		while (iterator.hasNext()) {
			Neuron preSynapticNeuron = iterator.next();
			createSynapse(creatorAgent, preSynapticNeuron, newNeuron,
					preSynapticNeuron.getAxonalDelay());
		}
		// post-synapses after...
		if (postSynapticNeuron != null) {
			createSynapse(creatorAgent, newNeuron, postSynapticNeuron,
					newNeuron.getAxonalDelay());
		}

		SOEASYParameters.getInstance().setStable(false);

		return newNeuron;
	}

	public InnervatedNeuron createInnervatedNeuron(
			CooperativeAgent creatorAgent, String identifier,
			int numberOfNeurons, String cause) {
		InnervatedNeuron neuron = new InnervatedNeuron(creatorAgent,
				identifier, true, numberOfNeurons, 0.5, SOEASYParameters
						.getInstance().getRestingAhpLevel(), cause);
		//
		addCreatedNeuronToTheEnvironment(neuron);
		//
		SOEASYParameters.getInstance().setStable(false);

		return neuron;
	}

	public Synapse createSynapse(CooperativeAgent creatorAgent, Neuron source,
			Neuron target, double axonalDelay) {
		Synapse synapse = new Synapse(creatorAgent, source, target, axonalDelay);
		this.add(synapse);
		graph.addEdge(synapse, source, target);

		if (source.isExcitatory()) {
			this.excitatorySynapseCount++;
		} else {
			this.inhibitorySynapseCount++;
		}

		updateEvolutionLists();

		return synapse;
	}

	public RestingNeuron createMuscle() {
		RestingNeuron muscle = new RestingNeuron(null, MUSCLE, true, 1, 1.0,
				0.00001, null);
		this.add(muscle);
		existingAgents.put(muscle.toString(), muscle);
		graph.addVertex(muscle);

		SOEASYParameters.getInstance().setStable(false);

		return muscle;
	}

	/**
	 * TODO Be careful!! This is not an agent!!!
	 */
	private ExtracellularFluid createExtracellularFluid() {
		ExtracellularFluid extracellularFluid = new ExtracellularFluid();
		this.add(extracellularFluid);
		existingAgents.put("ExtracellularFluid", extracellularFluid);

		SOEASYParameters.getInstance().setStable(false);

		return extracellularFluid;
	}

	public Viewer createViewer(RunningNeuron motorNeuron, Neuron muscle,
			PeriStimulusTimeHistogram referencePSTH) {
		this.viewer = new Viewer(motorNeuron, muscle, referencePSTH);
		this.add(viewer);
		existingAgents.put(viewer.toString(), viewer);

		SOEASYParameters.getInstance().setStable(false);

		return this.viewer;
	}

	public Viewer createViewer(RunningNeuron motorNeuron, Neuron muscle) {
		this.viewer = new Viewer(motorNeuron, muscle);
		this.add(viewer);
		existingAgents.put(viewer.toString(), viewer);

		SOEASYParameters.getInstance().setStable(false);

		return this.viewer;
	}

	/**
	 * Returns a specific agent using "identifier".
	 * 
	 * @param identifier
	 *            identifier of the requested agent.
	 * @return the agent with identifier or null.
	 */
	public Object getAgent(String identifier) {
		return existingAgents.get(identifier);
	}

	public List<CooperativeAgent> getAgents() {
		List<CooperativeAgent> agents = new Vector<CooperativeAgent>();
		IndexedIterable<Object> objects = this.getObjects(Neuron.class);
		Iterator<Object> iterator = objects.iterator();
		while (iterator.hasNext()) {
			CooperativeAgent agent = (CooperativeAgent) iterator.next();
			agents.add(agent);
		}
		return agents;
	}

	public void createTestAgent(String packageName) {
		String className = packageName + ".TestAgent";
		try {
			Class<?> cls = Class.forName(className);
			Constructor<?> constructor = cls.getConstructor();
			Object testAgent = constructor.newInstance();
			this.add(testAgent);
			existingAgents.put("TestAgent", testAgent);
		} catch (Throwable e) {
			System.err.println("Error while creating the Tester agent");
			e.printStackTrace();
		}
	}

	public void createReporterAgent(String packageName) {
		String className = packageName + ".ReporterAgent";
		try {
			Class<?> cls = Class.forName(className);
			Constructor<?> constructor = cls.getConstructor();
			Object reporterAgent = constructor.newInstance();
			this.add(reporterAgent);
			existingAgents.put("ReporterAgent", reporterAgent);
		} catch (Throwable e) {
			System.err.println("Error while creating the Reporter agent");
			e.printStackTrace();
		}
	}

	public int getNumberOfNeuronAgents() {
		return this.getObjects(Neuron.class).size();
	}

	public int getNumberOfExcitatoryNeuronAgents() {
		return this.excitatoryNeuronCount;
	}

	public int getNumberOfInhibitoryNeuronAgents() {
		return this.inhibitoryNeuronCount;
	}

	public int getNumberOfRemovedNeuronAgents() {
		return this.removedAgents.size();
	}

	public int getNumberOfExcitatorySynapses() {
		return this.excitatorySynapseCount;
	}

	public int getNumberOfInhibitorySynapses() {
		return this.inhibitorySynapseCount;
	}

	public int getNumberOfSynapses() {
		return this.getObjects(Synapse.class).size();
	}

	public int getNumberOfRemovedSynapses() {
		return this.removedSynapses.size();
	}

	public void removeSynapse(Synapse synapse) {
		if (synapse.getPresynapticNeuron().isExcitatory()) {
			this.excitatorySynapseCount--;
		} else {
			this.inhibitorySynapseCount--;
		}

		updateEvolutionLists();

		this.removedSynapses.put(synapse.toString(), synapse);
		synapse.kill();
		this.existingAgents.remove(synapse);
		this.remove(synapse);
		this.graph.removeEdge(synapse);
	}

	public void removeNeuron(Neuron neuron) {
		System.out.println(neuron + " is being removed.");
		List<Synapse> clone = (Vector<Synapse>) neuron.getPostSynapses(); // .clone();
		Iterator<Synapse> postSynapseIterator = clone.iterator();
		while (postSynapseIterator.hasNext()) {
			Synapse synapse = postSynapseIterator.next();
			removeSynapse(synapse);
		}

		clone = (Vector<Synapse>) neuron.getPreSynapses(); // .clone();
		Iterator<Synapse> preSynapseIterator = clone.iterator();
		while (preSynapseIterator.hasNext()) {
			Synapse synapse = preSynapseIterator.next();
			removeSynapse(synapse);
		}

		neuron.kill();
		this.existingAgents.remove(neuron.toString());
		this.removedAgents.put(neuron.toString(), neuron);
		this.remove(neuron);
		this.graph.removeVertex(neuron);

		if (neuron.isExcitatory()) {
			this.excitatoryNeuronCount--;
		} else {
			this.inhibitoryNeuronCount--;
		}

		updateEvolutionLists();
	}

	/**
	 * Returns a list of agents or an empty list.
	 * 
	 * @param clazz
	 * @return
	 */
	public List<CooperativeAgent> getAgents(
			Class<? extends CooperativeAgent> clazz) {
		List<CooperativeAgent> agents = new Vector<CooperativeAgent>();
		IndexedIterable<Object> objects = this.getObjects(clazz);
		Iterator<Object> iterator = objects.iterator();
		while (iterator.hasNext()) {
			CooperativeAgent agent = (CooperativeAgent) iterator.next();
			agents.add(agent);
		}
		return agents;
	}

	public List<InnervatedNeuron> getSensoryNeurons() {
		List<InnervatedNeuron> result = new Vector<InnervatedNeuron>();
		//
		List<CooperativeAgent> agents = getAgents(InnervatedNeuron.class);
		Iterator<CooperativeAgent> iterator = agents.iterator();
		while (iterator.hasNext()) {
			result.add((InnervatedNeuron) iterator.next());
		}
		//
		return result;
	}

	public ExtracellularFluid getExtracellularFluid() {
		return (ExtracellularFluid) existingAgents.get("ExtracellularFluid");
	}

	public Graph<Neuron, Synapse> getGraph() {
		return graph;
	}

	public XYSeries getExcitatoryNeuronEvolution() {
		FastXYSeries xySeries = new FastXYSeries("Excitatory");
		xySeries.setData(this.excitatoryNeuronEvolution);
		return xySeries;
	}

	public XYSeries getInhibitoryNeuronEvolution() {
		FastXYSeries xySeries = new FastXYSeries("Inhibitory");
		xySeries.setData(this.inhibitoryNeuronEvolution);
		return xySeries;
	}

	public XYSeries getAverageCriticalityEvolution() {
		double tick = SOEASYParameters.getInstance().getCurrentTick();
		this.overallCriticalityEvolution.add(new XYDataItem(tick,
				getOverallCriticality()));

		FastXYSeries xySeries = new FastXYSeries("Average");
		xySeries.setData(this.overallCriticalityEvolution);
		return xySeries;
	}

	public XYSeries getMotoneuronCriticalityEvolution() {
		double tick = SOEASYParameters.getInstance().getCurrentTick();
		CooperativeAgent motoneuronAgent = (CooperativeAgent) getAgent("MotorNeuron");
		this.motoneuronCriticalityEvolution.add(new XYDataItem(tick,
				motoneuronAgent.getKriticality()));

		FastXYSeries xySeries = new FastXYSeries("Motoneuron");
		xySeries.setData(this.motoneuronCriticalityEvolution);
		return xySeries;
	}

	public XYSeries getSensoryNeuronCriticalityEvolution() {
		double tick = SOEASYParameters.getInstance().getCurrentTick();
		CooperativeAgent sensoryneuronAgent = (CooperativeAgent) getAgent("AfferentNeuron");
		this.sensoryNeuronCriticalityEvolution.add(new XYDataItem(tick,
				sensoryneuronAgent.getKriticality()));

		FastXYSeries xySeries = new FastXYSeries("SensoryNeuron");
		xySeries.setData(this.sensoryNeuronCriticalityEvolution);
		return xySeries;
	}

	private double getOverallCriticality() {
		Averager overallCriticality = new Averager();
		//
		synchronized (existingAgents) {

			Iterator<Object> iterator = this.existingAgents.values().iterator();
			while (iterator.hasNext()) {
				Object next = iterator.next();
				if (next instanceof CooperativeAgent) {
					CooperativeAgent agent = (CooperativeAgent) next;
					double criticality = agent.getKriticality();
					overallCriticality.update(criticality);
				}
			}

		}
		//
		return overallCriticality.getAverage();
	}

	public XYSeries getExcitatorySynapseEvolution() {
		FastXYSeries xySeries = new FastXYSeries("Excitatory");
		xySeries.setData(this.excitatorySynapseEvolution);
		return xySeries;
	}

	public XYSeries getInhibitorySynapseEvolution() {
		FastXYSeries xySeries = new FastXYSeries("Inhibitory");
		xySeries.setData(this.inhibitorySynapseEvolution);
		return xySeries;
	}

	public Viewer getViewer() {
		return this.viewer;
	}

	public DigitalizedXYSeries getCriticalityDistribution() {
		DigitalizedXYSeries criticalityDistribution = new DigitalizedXYSeries(
				new Range(0.0, 100.0), 5.0);

		synchronized (existingAgents) {
			Iterator<Object> iterator = this.existingAgents.values().iterator();
			while (iterator.hasNext()) {
				Object object = iterator.next();
				if (object instanceof CooperativeAgent) {
					CooperativeAgent agent = (CooperativeAgent) object;
					double criticality = agent.getKriticality();
					List<XYDataItem> itemsAt = criticalityDistribution
							.getItemsAt(criticality);
					if (itemsAt.isEmpty()) {
						criticalityDistribution.add(criticality, 1.0);
					} else {
						double count = itemsAt.get(0).getYValue();
						itemsAt.get(0).setY(++count);
					}
				}
			}
		}

		return criticalityDistribution;
	}

}
