package mimickit;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import mimickit.SOEASYController;
import mimickit.amas.Feedback;
import mimickit.model.MNDischargeRates;
import mimickit.model.SOEASYEnvironment;
import mimickit.model.SOEASYParameters;
import mimickit.model.neuron.Neuron;
import mimickit.model.neuron.Synapse;
import mimickit.model.viewer.Viewer;
import mimickit.scenario.AbstractScenarioExecuter;
import mimickit.util.Correlation;
import mimickit.util.CuSum;
import mimickit.util.DigitalizedXYSeries;
import mimickit.util.PeriStimulusTimeHistogram;

import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.statistics.DefaultMultiValueCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.uci.ics.jung.graph.Graph;

/**
 * Controller class for user interface classes.
 * 
 * @author Onder Gurcan
 * 
 */
public class SOEASYController {

	private static SOEASYController instance;

	private static AbstractScenarioExecuter scenarioExecutor;

	private long startingTime;

	private SOEASYController() {
		startingTime = System.currentTimeMillis();
	}

	public static SOEASYController getInstance() {
		if (instance == null) {
			instance = new SOEASYController();
		}
		return instance;
	}

	public void runSimulation(final AbstractScenarioExecuter executor) {
		new Thread() {
			@Override
			public void run() {
				try {
					scenarioExecutor = executor;
					scenarioExecutor.mainMethod();
					startingTime = System.currentTimeMillis();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();

	}

	public SOEASYParameters getParameters() {
		return SOEASYParameters.getInstance();
	}

	public double getCurrentTick() {
		return SOEASYParameters.getInstance().getCurrentTick();
	}

	public double getCurrentTime() {
		return System.currentTimeMillis() - startingTime;
	}

	public PeriStimulusTimeHistogram getSimulatedPSTH() {
		return SOEASYEnvironment.getInstance().getDataSet().getSimulatedPSTH();
	}

	public Graph<Neuron, Synapse> getGraph() {
		return SOEASYEnvironment.getInstance().getGraph();
	}

	public XYSeries getExcitatoryNeuronEvolution() {
		return SOEASYEnvironment.getInstance().getExcitatoryNeuronEvolution();
	}

	public XYSeries getAverageCriticality() {
		return SOEASYEnvironment.getInstance().getAverageCriticalityEvolution();
	}
	
	public XYSeries getMotoneuronCriticality() {
		return SOEASYEnvironment.getInstance().getMotoneuronCriticalityEvolution();
	}
	
	public XYSeries getSensoryNeuronCriticality() {
		return SOEASYEnvironment.getInstance().getSensoryNeuronCriticalityEvolution();
	}

	public XYSeries getInhibitoryNeuronEvolution() {
		return SOEASYEnvironment.getInstance().getInhibitoryNeuronEvolution();
	}

	public XYSeries getExcitatorySynapseEvolution() {
		return SOEASYEnvironment.getInstance().getExcitatorySynapseEvolution();
	}

	public XYSeries getInhibitorySynapseEvolution() {
		return SOEASYEnvironment.getInstance().getInhibitorySynapseEvolution();
	}

	public void stopSimulation() {
		scenarioExecutor.stop();
	}

	public boolean isSimulationRunning() {
		return (scenarioExecutor != null) && (scenarioExecutor.isRunning());
	}

	public CategoryDataset getViewersLastFeedbacks() {
		DefaultMultiValueCategoryDataset dataset = new DefaultMultiValueCategoryDataset();
		List<Double> fIncrease = new ArrayList<Double>();
		List<Double> fGood = new ArrayList<Double>();
		List<Double> fDecrease = new ArrayList<Double>();
		//
		Viewer viewer = SOEASYEnvironment.getInstance().getViewer();
		HashMap<Double, Feedback> feedbacks = viewer
				.getLastOutgoingFeedbacksByLatency();
		double goodCount = 0.0, increaseCount = 0.0, decreaseCount = 0.0;
		for (double latency = 0.0; latency <= 200.0; latency += 0.5) {
			Feedback feedback = feedbacks.get(latency);
			if (feedback != null) {
				if (feedback.isIncrease()) {
					fIncrease.add(latency);
					increaseCount++;
				} else if (feedback.isGood()) {
					fGood.add(latency);
					goodCount++;
				} else {
					fDecrease.add(latency);
					decreaseCount++;
				}
			}
		}
		//
		double totalFeedbackCount = goodCount + increaseCount + decreaseCount;
		double increasePercentage = (increaseCount / totalFeedbackCount) * 100;
		double goodPercentage = (goodCount / totalFeedbackCount) * 100;
		double decreasePercentage = (decreaseCount / totalFeedbackCount) * 100;
		//
		DecimalFormat decimalFormat = new DecimalFormat("##.##");
		dataset.add(fIncrease, "",
				"Increase\n" + decimalFormat.format(increasePercentage) + "%");
		dataset.add(fGood, "", "Good\n" + decimalFormat.format(goodPercentage)
				+ "%");
		dataset.add(fDecrease, "",
				"Decrease\n" + decimalFormat.format(decreasePercentage) + "%");
		//
		return dataset;
	}

	public String getViewersGoodFeedbackRate() {
		List<Double> fIncrease = new ArrayList<Double>();
		List<Double> fGood = new ArrayList<Double>();
		List<Double> fDecrease = new ArrayList<Double>();
		//
		Viewer viewer = SOEASYEnvironment.getInstance().getViewer();
		HashMap<Double, Feedback> feedbacks = viewer
				.getLastOutgoingFeedbacksByLatency();
		double goodCount = 0.0, increaseCount = 0.0, decreaseCount = 0.0;
		for (double latency = 0.0; latency <= 200.0; latency += 0.5) {
			Feedback feedback = feedbacks.get(latency);
			if (feedback != null) {
				if (feedback.isIncrease()) {
					fIncrease.add(latency);
					increaseCount++;
				} else if (feedback.isGood()) {
					fGood.add(latency);
					goodCount++;
				} else {
					fDecrease.add(latency);
					decreaseCount++;
				}
			}
		}
		//
		double totalFeedbackCount = goodCount + increaseCount + decreaseCount;
		// double increasePercentage = (increaseCount / totalFeedbackCount) *
		// 100;
		double goodPercentage = (goodCount / totalFeedbackCount) * 100;
		// double decreasePercentage = (decreaseCount / totalFeedbackCount) *
		// 100;
		//
		DecimalFormat decimalFormat = new DecimalFormat("##.##");
		return decimalFormat.format(goodPercentage) + " %";
	}

	public void exportAsGraphML(String exportDir) {
		SOEASYEnvironment.getInstance().exportGraphML(exportDir);
	}

	public int getNumberOfNeurons() {
		return SOEASYEnvironment.getInstance().getNumberOfNeuronAgents();
	}

	public int getNumberOfSynapses() {
		return SOEASYEnvironment.getInstance().getNumberOfSynapses();
	}

	public HistogramDataset getSynapsesOnMotoneuron() {
		HistogramDataset dataset = new HistogramDataset();
		//
		Neuron motorNeuron = (Neuron) SOEASYEnvironment.getInstance().getAgent(
				"MotorNeuron");
		Neuron sensoryNeuron = (Neuron) SOEASYEnvironment.getInstance()
				.getAgent("AfferentNeuron");
		double stimulationTime = sensoryNeuron.getLastSpikeTime();
		List<Neuron> preNeurons = motorNeuron.getPreNeurons();
		Iterator<Neuron> iterator = preNeurons.iterator();
		List<Double> excitatorySynapses = new ArrayList<Double>();
		List<Double> inhibitorySynapses = new ArrayList<Double>();
		while (iterator.hasNext()) {
			Neuron preNeuron = iterator.next();
			double latency = preNeuron.getLastSpikeTime() - stimulationTime;
			if ((latency > 0.0) && (latency <= 200.0)) {
				if (preNeuron.isExcitatory()) {
					excitatorySynapses.add(latency
							+ motorNeuron.getPreSynapses().get(0)
									.getAxonalDelay());
				} else {
					inhibitorySynapses.add(latency
							+ motorNeuron.getPreSynapses().get(0)
									.getAxonalDelay());
				}
			}
		}
		//
		if (excitatorySynapses.size() > 0) {
			double[] excitatoryValue = new double[excitatorySynapses.size()];
			for (int i = 0; i < excitatorySynapses.size(); i++) {
				excitatoryValue[i] = excitatorySynapses.get(i);
			}
			dataset.addSeries("Excitatory", excitatoryValue, 400);
		}
		//
		if (inhibitorySynapses.size() > 0) {
			double[] inhibitoryValue = new double[inhibitorySynapses.size()];
			for (int i = 0; i < inhibitorySynapses.size(); i++) {
				inhibitoryValue[i] = inhibitorySynapses.get(i);
			}
			dataset.addSeries("Inhibitory", inhibitoryValue, 400);
		}
		//
		return dataset;
	}

	public XYDataset getNetEffectOnMotoneuron() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		DigitalizedXYSeries xySeries = new DigitalizedXYSeries(new Range(0.0,
				200.0), 0.5);
		for (double xValue = 0.0; xValue <= 200.0; xValue += 0.5) {
			xySeries.add(xValue, 0.0);
		}
		//
		Neuron motorNeuron = (Neuron) SOEASYEnvironment.getInstance().getAgent(
				"MotorNeuron");
		Neuron sensoryNeuron = (Neuron) SOEASYEnvironment.getInstance()
				.getAgent("AfferentNeuron");
		double stimulationTime = sensoryNeuron.getLastSpikeTime();
		List<Neuron> preNeurons = motorNeuron.getPreNeurons();
		Iterator<Neuron> iterator = preNeurons.iterator();
		while (iterator.hasNext()) {
			Neuron preNeuron = iterator.next();
			double latency = preNeuron.getLastSpikeTime() - stimulationTime;
			if ((latency > 0.0) && (latency <= 200.0)) {
				double actualLatency = latency
						+ motorNeuron.getPreSynapses().get(0).getAxonalDelay();
				for (double t = 0; t < 4.0; t += 0.5) {
					actualLatency += t;
					if (actualLatency <= 200.0) {
						List<XYDataItem> effectList = xySeries
								.getItemsAt(actualLatency);
						double effect = preNeuron.getPostSynapse(motorNeuron)
								.getTotalSynapticStrength();
						double previousEffect = effectList.get(0).getYValue();
						effect += previousEffect;
						effectList.get(0).setY(effect);
					}
				}

			}
		}
		//
		dataset.addSeries(xySeries.toXYSeries("Net Effect"));
		return dataset;
	}

	public void saveToFile(String fileName) throws IOException {
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
				fileName));
		bufferedWriter.write("Viewer's good feedback rate" + " = "
				+ getViewersGoodFeedbackRate() + "\n");
		double psfcusumCorrelation = getPSFCUSUMCorrelation();
		bufferedWriter.write("PSF-CUSUM correlation" + " = "
				+ psfcusumCorrelation + "\n");
		bufferedWriter.write("PSF-CUSUM similarity" + " = "
				+ psfcusumCorrelation * psfcusumCorrelation * 100 + " %\n");
		bufferedWriter.close();
	}

	public double getPSFCUSUMCorrelation() {
		MNDischargeRates dataSet = SOEASYEnvironment.getInstance().getDataSet();
		CuSum referencePSFCUSUM = dataSet.getReferencePSFCUSUM();
		CuSum simulatedPSFCUSUM = dataSet.getSimulatedPSFCUSUM();

		SOEASYParameters parameters = SOEASYParameters.getInstance();
		double beginning = parameters
				.getParameter(SOEASYParameters.PATHWAY_BEGINNING);
		double end = parameters.getParameter(SOEASYParameters.PATHWAY_END);

		return Correlation.getPearsonCorrelation(referencePSFCUSUM,
				simulatedPSFCUSUM, beginning, end);
	}

	public double getPSTHCUSUMCorrelation() {
		MNDischargeRates dataSet = SOEASYEnvironment.getInstance().getDataSet();
		CuSum referencePSTHCUSUM = dataSet.getReferencePSTHCUSUM();
		PeriStimulusTimeHistogram simulatedPSTH = getSimulatedPSTH();
		if (simulatedPSTH.getValueArray() != null) {
			CuSum simulatedPSTHCUSUM = dataSet.getSimulatedPSTHCUSUM();

			SOEASYParameters parameters = SOEASYParameters.getInstance();
			Range trainingRange = parameters.getTrainingRange();

			return Correlation.getPearsonCorrelation(referencePSTHCUSUM,
					simulatedPSTHCUSUM, trainingRange);
		} else {
			return 0.0;
		}
	}

	public MNDischargeRates createDataSet(String triggerFileName,
			String mnDischargesFileName) {
		return SOEASYEnvironment.getInstance().createDataSet(triggerFileName,
				mnDischargesFileName);
	}

	public MNDischargeRates getDataSet() {
		return SOEASYEnvironment.getInstance().getDataSet();
	}
}
