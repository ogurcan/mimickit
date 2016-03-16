package mimickit.util;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import mimickit.amas.CooperativeAgent;
import mimickit.model.MNDischargeRates;
import mimickit.model.SOEASYEnvironment;
import mimickit.model.SOEASYParameters;

import org.jfree.data.Range;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import umontreal.iro.lecuyer.charts.ScatterChart;
import umontreal.iro.lecuyer.charts.XYLineChart;
import umontreal.iro.lecuyer.charts.XYListSeriesCollection;

public class SimulationReport {

	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMAT_FILE = "yyyy-MM-dd_HH.mm.ss";
	public static final String TIME_FORMAT = "HH:mm:ss";
	private SimpleDateFormat dateFormatNow;
	private SimpleDateFormat dateFormatFile;

	private String title;
	private Date timeOfStart;
	private long timeOfStartInMilliseconds;
	private long timeOfEndInMilliseconds;
	private Date timeOfEnd;

	private CuSum referencePSFCUSUM;
	private CuSum simulatedPSFCUSUM;
	private PeristimulusFrequencygram simulatedPSF;
	//
	private PeriStimulusTimeHistogram referencePSTH;
	private CuSum referencePSTHCUSUM;
	private PeriStimulusTimeHistogram simulatedPSTH;
	private CuSum simulatedPSTHCUSUM;

	private XYSeries xySeriesNumberOfInhibitoryNeurons;
	private XYSeries xySeriesNumberOfExcitatoryNeurons;
	private XYSeries xySeriesNumberOfInhibitorySynapses;
	private XYSeries xySeriesNumberOfExcitatorySynapses;

	private Hashtable<CooperativeAgent, XYSeries> criticalityValues;
	private MNDischargeRates dataSet;

	public SimulationReport(String title) {
		dateFormatNow = new SimpleDateFormat(DATE_FORMAT_NOW);
		dateFormatFile = new SimpleDateFormat(DATE_FORMAT_FILE);
		timeOfStartInMilliseconds = System.currentTimeMillis();
		timeOfStart = new Date(timeOfStartInMilliseconds);
		xySeriesNumberOfInhibitoryNeurons = new XYSeries("Inhibitory Neurons");
		xySeriesNumberOfExcitatoryNeurons = new XYSeries("Excitatory Neurons");
		xySeriesNumberOfInhibitorySynapses = new XYSeries("Inhibitory Synapses");
		xySeriesNumberOfExcitatorySynapses = new XYSeries("Excitatory Synapses");
		criticalityValues = new Hashtable<CooperativeAgent, XYSeries>();
		//
		this.dataSet = SOEASYEnvironment.getInstance().getDataSet();		
		this.referencePSFCUSUM = dataSet.getReferencePSFCUSUM();
		//
		this.title = title;
	}

	public void toFile(String baseFolderName) throws Exception {
		SOEASYParameters simEnvironment = SOEASYParameters.getInstance();

		// create a folder first
		File allReportsFolder = new File(baseFolderName
				+ dateFormatFile.format(this.timeOfStart));
		allReportsFolder.mkdir();
		File reportFolder = new File(allReportsFolder.getAbsolutePath() + "/"
				+ simEnvironment.getCurrentTickAsString());
		reportFolder.mkdir();
		String reportFolderPath = reportFolder.getAbsolutePath();
		// then put the simulated data to this folder
		simulatedPSF.writeSignalsToFile(reportFolderPath);
		simulatedPSF.toFile(reportFolderPath + "/cusum.txt");

		// then export the graphml file here
		SOEASYEnvironment.getInstance().exportGraphML(reportFolderPath);

		File file = new File(reportFolderPath + "/_report.tex");
		FileWriter writer = new FileWriter(file);
		BufferedWriter report = new BufferedWriter(writer);

		report.append("\\documentclass[12pt]{article}\n");
		report.append("\\usepackage{tikz}\n");
		report.append("\\usetikzlibrary{plotmarks}\n");
		report.append("\\title{" + this.title + "}\n");
		report.append("\\author{Onder Gurcan}\n");
		report.append("\\begin{document}\n");
		report.append("\\maketitle\n");
		report.append("\\section{Experimental Frame}\n");
		report.append("\\begin{itemize}\n");
		String startingTime = dateFormatNow.format(this.timeOfStart);
		timeOfEndInMilliseconds = System.currentTimeMillis();
		timeOfEnd = Calendar.getInstance().getTime();
		String endingTime = dateFormatNow.format(timeOfEnd);
		report.append("\\item Start / End: " + startingTime + " / "
				+ endingTime + "\n");
		long durDiff = this.timeOfEndInMilliseconds - timeOfStartInMilliseconds;
		report.append("\\item Duration:\t\t" + formatTime(durDiff) + " / "
				+ simEnvironment.getCurrentTickAsString() + " ticks \n");
		report.append("\\end{itemize}\n");

		report.append("\\subsection{Simulation Parameters}\n");
		report.append("\\begin{itemize}\n");
		report.append("\\item Annoyance levels (reorganization/evolution):\t"
				+ "20 - 40" + "\n");
		report.append("\\item Resting AHP Level (mV):\t"
				+ simEnvironment.getRestingAhpLevel() + "\n");
		report.append("\\item Pathway beginning/end (ms):\t"
				+ simEnvironment
						.getParameter(SOEASYParameters.PATHWAY_BEGINNING)
				+ " - "
				+ simEnvironment.getParameter(SOEASYParameters.PATHWAY_END)
				+ "\n");
		report.append("\\item Smoothing count:\t"
				+ dataSet.getSmooth() + "\n");
		report.append("\\item Average frequency fault tolerance (Hz):\t"
				+ simEnvironment
						.getParameter(SOEASYParameters.DERIVATIVE_FAULT_TOLERANCE)
				+ "\n");
		report.append("\\end{itemize}\n");

		report.append("\\subsection{Agents}\n");
		report.append("\\begin{itemize}\n");
		report.append("\\item Number of Existing Neuron Agents:\t"
				+ SOEASYEnvironment.getInstance().getNumberOfNeuronAgents()
				+ "\n");
		report.append("\\item Number of Removed Neuron Agents:\t"
				+ SOEASYEnvironment.getInstance()
						.getNumberOfRemovedNeuronAgents() + "\n");
		report.append("\\item Number of Existing Synapses:\t"
				+ SOEASYEnvironment.getInstance().getNumberOfSynapses() + "\n");
		report.append("\\item Number of Removed Synapses:\t"
				+ SOEASYEnvironment.getInstance().getNumberOfRemovedSynapses()
				+ "\n");
		report.append("\\end{itemize}\n");
		report.append("\\section{Results}\n");
		report.append("\\begin{itemize}\n");
		report.append("\\item Reference PSF Peristimulus mean: "
				+ referencePSFCUSUM.getPrestimulusMean() + "\n");
		report.append("\\item Reference PSF ErrorBox: "
				+ referencePSFCUSUM.getErrorBoxLowerBound() + " - "
				+ referencePSFCUSUM.getErrorBoxUpperBound() + "\n");
		report.append("\\end{itemize}\n");
		report.append("\\begin{itemize}\n");
		report.append("\\item Simulated PSF Peristimulus mean: "
				+ simulatedPSFCUSUM.getPrestimulusMean() + "\n");
		report.append("\\item Simulated PSF ErrorBox LowerB: "
				+ simulatedPSFCUSUM.getErrorBoxLowerBound() + " - "
				+ simulatedPSFCUSUM.getErrorBoxUpperBound() + "\n");
		report.append("\\end{itemize}\n");
		report.append("Simulated Results vs. Reference Data\n");
		report.append("\\begin{itemize}\n");
		double diff = simulatedPSFCUSUM.getPrestimulusMean()
				- referencePSFCUSUM.getPrestimulusMean();
		report.append("\\item Peristimulus mean diff: " + diff + "\n");

		double from = simEnvironment
				.getParameter(SOEASYParameters.PATHWAY_BEGINNING);
		double to = simEnvironment.getParameter(SOEASYParameters.PATHWAY_END);

		// calculate PSF-CUSUM correlation and similarity
		double crossCorrelation = Correlation.getPearsonCorrelation(
				referencePSFCUSUM, simulatedPSFCUSUM, from, to);
		report.append("\\item Pearson correlation (r) of PSF-CUSUMs [" + from
				+ ", " + to + "]: " + crossCorrelation + "\n");
		report.append("\\item Similarity (r2) of PSF-CUSUMs: "
				+ (crossCorrelation * crossCorrelation) + "\n");
		System.out.print(", psf-cusum r2: "
				+ (crossCorrelation * crossCorrelation) + ", ");

		// calculate PSTH-CUSUM correlation and similarity
		crossCorrelation = Correlation.getPearsonCorrelation(
				referencePSTHCUSUM, simulatedPSTHCUSUM, from, to);
		report.append("\\item Pearson correlation (r) of PSTH-CUSUMs [" + from
				+ ", " + to + "]: " + crossCorrelation + "\n");
		report.append("\\item Similarity (r2) of PSTH-CUSUMs: "
				+ (crossCorrelation * crossCorrelation) + "\n");
		System.out.println(", psth-cusum-r2: "
				+ (crossCorrelation * crossCorrelation) + ")");

		report.append("\\end{itemize}\n");
		report.append(getAverageDischargeRatesXYChart());
		report.append("\n");
		report.append(getFigureRefPsf());
		report.append("\n");
		report.append(getFigureSimPsf());
		report.append("\n");
		report.append(getFigureRefPsfCusum());
		report.append("\n");
		report.append(getFigureSimPsfCusum());
		report.append("\n");
		report.append(getFigureRefPsth());
		report.append("\n");
		report.append(getFigureSimPsth());
		report.append("\n");
		report.append(getFigureRefPsthCusum());
		report.append("\n");
		report.append(getFigureSimPsthCusum());
		report.append("\n");
		report.append(getFigureNumberOfEntities());
		report.append("\n");
		// appendAllCriticalitiesFigure(report);
		report.append("\\end{document}\n");

		// report.write(report.toString());
		report.close();
		writer.close();

		// printAgentReports(reportFolderPath);
	}

	private String formatTime(long durDiff) {
		StringBuffer buffer = new StringBuffer();

		int hours = (int) (durDiff / 1000 / 3600);
		if (hours > 0) {
			buffer.append(hours + " hours ");
		}
		int minutes = (int) (durDiff / 1000 / 60) - (hours * 60);
		if (minutes > 0) {
			buffer.append(minutes + " minutes ");
		}
		int seconds = (int) (durDiff / 1000) - (minutes * 60) - (hours * 3600);
		buffer.append(seconds + " seconds");

		return buffer.toString();
	}

	private String getFigureNumberOfEntities() {
		XYSeriesCollection xySeriesCollection = new XYSeriesCollection();

		xySeriesCollection.addSeries(this.xySeriesNumberOfInhibitoryNeurons);
		xySeriesCollection.addSeries(this.xySeriesNumberOfExcitatoryNeurons);
		xySeriesCollection.addSeries(this.xySeriesNumberOfInhibitorySynapses);
		xySeriesCollection.addSeries(this.xySeriesNumberOfExcitatorySynapses);

		XYLineChart chart = new XYLineChart("Number of Entities", "Time (ms)",
				"Number (Z+)", xySeriesCollection);

		// Customizes the data plot
		XYListSeriesCollection collec = chart.getSeriesCollection();
		collec.setColor(0, Color.BLACK); // cusum
		collec.setColor(1, Color.RED); // cusum
		collec.setColor(2, Color.DARK_GRAY); // cusum
		collec.setColor(3, Color.BLUE); // cusum

		chart.setAutoRange();

		String latexExp = chart.toLatex(10, 14);

		int indexOfFigureBegin = latexExp.indexOf("\\begin{figure}");
		int indexOfFigureEnd = latexExp.indexOf("\\caption");
		String figureString = latexExp.substring(indexOfFigureBegin,
				indexOfFigureEnd);
		String caption = "Number of Entities by time";
		figureString = figureString + "\\caption{" + caption
				+ "}\n\\end{figure}\n";

		return figureString;
	}

	// private void appendAllCriticalitiesFigure(BufferedWriter report) {
	// Collection<XYSeries> series = criticalityValues.values();
	// Iterator<XYSeries> iterator = series.iterator();
	// XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
	// while (iterator.hasNext()) {
	// XYSeries xySeriesCriticality = iterator.next();
	// if (xySeriesCriticality.getItemCount() > 2) {
	// xySeriesCollection.addSeries(xySeriesCriticality);
	// }
	// }
	//
	// XYLineChart chart = new XYLineChart("The Criticality of InterNeuron1",
	// "Time (ms)", "Criticality", xySeriesCollection);
	//
	// // Customizes the data plot
	// XYListSeriesCollection collec = chart.getSeriesCollection();
	// collec.setColor(0, Color.RED); // cusum
	//
	// Range domainBounds = xySeriesCollection.getDomainBounds(false);
	//
	// double[] range = { domainBounds.getLowerBound(),
	// domainBounds.getUpperBound(), 0.0, 100.0 };
	// chart.setManualRange(range);
	// // chart.setAutoRange();
	// // chart.view(800, 600);
	// try {
	// String latexExp = chart.toLatex(10, 14);
	//
	// int indexOfFigureBegin = latexExp.indexOf("\\begin{figure}");
	// int indexOfFigureEnd = latexExp.indexOf("\\caption");
	// String figureCriticality = latexExp.substring(indexOfFigureBegin,
	// indexOfFigureEnd);
	// String caption = "Criticalities of all agents";
	// figureCriticality = figureCriticality + "\\caption{" + caption
	// + "}\n\\end{figure}\n";
	//
	// report.append(figureCriticality);
	// report.append("\n\n");
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	//
	// private void printAgentReports(String reportFolderPath) throws Exception
	// {
	// Collection<XYSeries> series = criticalityValues.values();
	// Iterator<XYSeries> iterator = series.iterator();
	// while (iterator.hasNext()) {
	// XYSeries xySeriesCriticality = iterator.next();
	// if (xySeriesCriticality.getItemCount() > 2) {
	// XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
	// xySeriesCollection.addSeries(xySeriesCriticality);
	//
	// XYLineChart chart = new XYLineChart("The Criticality of "
	// + xySeriesCriticality.getDescription(), "Time (ms)",
	// "Criticality", xySeriesCollection);
	//
	// // Customizes the data plot
	// XYListSeriesCollection collec = chart.getSeriesCollection();
	// collec.setColor(0, Color.RED);
	//
	// Range domainBounds = xySeriesCollection.getDomainBounds(false);
	//
	// double[] range = { domainBounds.getLowerBound(),
	// domainBounds.getUpperBound(), 0.0, 100.0 };
	// chart.setManualRange(range);
	// // chart.setAutoRange();
	// // chart.view(800, 600);
	// String latexExp = chart.toLatex(10, 6);
	//
	// File file = new File(reportFolderPath + "/"
	// + xySeriesCriticality.getDescription() + ".tex");
	// FileWriter writer = new FileWriter(file);
	// BufferedWriter report = new BufferedWriter(writer);
	// report.append(latexExp);
	// report.write(report.toString());
	// report.close();
	// writer.close();
	// } else {
	// // System.out.println(xySeriesCriticality.getDescription() +
	// // " = "
	// // + count);
	// }
	// }
	// }

	private String getFigureRefPsf() {
		StaticPSF referencePSF = SOEASYEnvironment.getInstance().getDataSet()
				.getReferencePSF();
		String latexExp = referencePSF.getDischargeRatesXYChart(-50, 200)
				.toLatex(12, 6);
		int indexOfFigureBegin = latexExp.indexOf("\\begin{figure}");
		int indexOfFigureEnd = latexExp.indexOf("\\caption");
		String figureRefPsfCusum = latexExp.substring(indexOfFigureBegin,
				indexOfFigureEnd);
		figureRefPsfCusum = figureRefPsfCusum
				+ "\\caption{Reference PSF}\n\\end{figure}\n";
		return figureRefPsfCusum;
	}

	private String getFigureRefPsth() {
		String latexExp = referencePSTH.getHistogram(-50, 200).toLatex(12, 6);
		int indexOfFigureBegin = latexExp.indexOf("\\begin{figure}");
		int indexOfFigureEnd = latexExp.indexOf("\\caption");
		String figureRefPsfCusum = latexExp.substring(indexOfFigureBegin,
				indexOfFigureEnd);
		figureRefPsfCusum = figureRefPsfCusum
				+ "\\caption{Reference PSTH}\n\\end{figure}\n";
		return figureRefPsfCusum;
	}

	private String getFigureSimPsth() {
		String latexExp = simulatedPSTH.getHistogram(-50, 200).toLatex(12, 6);
		int indexOfFigureBegin = latexExp.indexOf("\\begin{figure}");
		int indexOfFigureEnd = latexExp.indexOf("\\caption");
		String figureRefPsfCusum = latexExp.substring(indexOfFigureBegin,
				indexOfFigureEnd);
		figureRefPsfCusum = figureRefPsfCusum
				+ "\\caption{Simulated PSTH}\n\\end{figure}\n";
		return figureRefPsfCusum;
	}

	public String getAverageDischargeRatesXYChart() {
		XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
		//
		StaticPSF referencePSF = SOEASYEnvironment.getInstance().getDataSet()
				.getReferencePSF();
		XYSeries averageDischargeRatesReference = referencePSF
				.getAvarageDischargeRates().toXYSeries("");
		averageDischargeRatesReference.setKey("Reference rates");
		xySeriesCollection.addSeries(averageDischargeRatesReference);

		XYSeries averageDischargeRatesSimulated = simulatedPSF
				.getAvarageDischargeRates().toXYSeries("");
		averageDischargeRatesSimulated.setKey("Simulated rates");
		xySeriesCollection.addSeries(averageDischargeRatesSimulated);
		//
		ScatterChart xyChart = new ScatterChart(
				"PSF - Moving Average Discharge Rates", "Time (ms)",
				"PSF (Hz)", xySeriesCollection);
		// Customizes the data plot
		XYListSeriesCollection collec = xyChart.getSeriesCollection();
		collec.setColor(0, Color.RED);
		collec.setColor(1, Color.BLUE);
		collec.setColor(2, Color.GREEN);
		//
		xyChart.setAutoRange();
		// xyChart.view(800, 600);
		String latexExp = xyChart.toLatex(12, 5);

		int indexOfFigureBegin = latexExp.indexOf("\\begin{figure}");
		int indexOfFigureEnd = latexExp.indexOf("\\caption");
		String figureStr = latexExp.substring(indexOfFigureBegin,
				indexOfFigureEnd);
		figureStr = figureStr
				+ "\\caption{PSF - Average Discharge Rates}\n\\end{figure}\n";
		return figureStr;
	}

	private String getFigureRefPsfCusum() {
		XYLineChart xyChart = referencePSFCUSUM.getXYChart(new Range(-50, 200),
				null);
		String latexExp = xyChart.toLatex(12, 6);
		int indexOfFigureBegin = latexExp.indexOf("\\begin{figure}");
		int indexOfFigureEnd = latexExp.indexOf("\\caption");
		String figureRefPsfCusum = latexExp.substring(indexOfFigureBegin,
				indexOfFigureEnd);
		figureRefPsfCusum = figureRefPsfCusum
				+ "\\caption{Reference PSF-CUSUM}\n\\end{figure}\n";
		return figureRefPsfCusum;
	}

	private String getFigureRefPsthCusum() {
		XYLineChart xyChart = referencePSTHCUSUM
				.getXYChart(new Range(-50, 200));
		String latexExp = xyChart.toLatex(12, 6);
		int indexOfFigureBegin = latexExp.indexOf("\\begin{figure}");
		int indexOfFigureEnd = latexExp.indexOf("\\caption");
		String figureRefPsfCusum = latexExp.substring(indexOfFigureBegin,
				indexOfFigureEnd);
		figureRefPsfCusum = figureRefPsfCusum
				+ "\\caption{Reference PSTH-CUSUM}\n\\end{figure}\n";
		return figureRefPsfCusum;
	}

	private String getFigureSimPsf() {
		String latexSim = simulatedPSF.getDischargeRatesXYChart(-50, 200)
				.toLatex(12, 6);
		int indexOfFigureBegin = latexSim.indexOf("\\begin{figure}");
		int indexOfFigureEnd = latexSim.indexOf("\\caption");
		String figureSimPsfCusum = latexSim.substring(indexOfFigureBegin,
				indexOfFigureEnd);
		figureSimPsfCusum = figureSimPsfCusum
				+ "\\caption{Simulated PSF}\n\\end{figure}\n";
		return figureSimPsfCusum;
	}

	private String getFigureSimPsthCusum() {
		XYLineChart xyChart = simulatedPSTHCUSUM
				.getXYChart(new Range(-50, 200));
		String latexExp = xyChart.toLatex(12, 6);
		int indexOfFigureBegin = latexExp.indexOf("\\begin{figure}");
		int indexOfFigureEnd = latexExp.indexOf("\\caption");
		String figureRefPsfCusum = latexExp.substring(indexOfFigureBegin,
				indexOfFigureEnd);
		figureRefPsfCusum = figureRefPsfCusum
				+ "\\caption{Reference PSTH-CUSUM}\n\\end{figure}\n";
		return figureRefPsfCusum;
	}

	private String getFigureSimPsfCusum() {
		Range offsetRange = new Range(-50, 200);
		Range cusumRange = null; // referencePSFCUSUM.getCUSUMRange(offsetRange);
		String latexSim = simulatedPSFCUSUM.getXYChart(offsetRange, cusumRange)
				.toLatex(12, 6);
		int indexOfFigureBegin = latexSim.indexOf("\\begin{figure}");
		int indexOfFigureEnd = latexSim.indexOf("\\caption");
		String figureSimPsfCusum = latexSim.substring(indexOfFigureBegin,
				indexOfFigureEnd);
		figureSimPsfCusum = figureSimPsfCusum
				+ "\\caption{Simulated PSF-CUSUM}\n\\end{figure}\n";
		return figureSimPsfCusum;
	}

	public void setSimulatedPSTH(PeriStimulusTimeHistogram simulatedPSTH) {
		this.simulatedPSTH = simulatedPSTH;
		this.simulatedPSTHCUSUM = new CuSum(simulatedPSTH, dataSet.getSmooth(), dataSet.getMovingAverageInterval());
	}

	public void setSimulatedPSF(PeristimulusFrequencygram simulatedPSF) {
		this.simulatedPSF = simulatedPSF;
		this.simulatedPSFCUSUM = new CuSum(simulatedPSF, dataSet.getSmooth(), dataSet.getMovingAverageInterval());
	}

	public void addNumberOfInhibitoryNeurons(double currentTick,
			int numberOfInhibitoryNeurons) {
		XYDataItem dataItem = new XYDataItem(currentTick,
				numberOfInhibitoryNeurons);
		xySeriesNumberOfInhibitoryNeurons.add(dataItem);
	}

	public void addNumberOfExcitatoryNeurons(double currentTick,
			int numberOfNeurons) {
		XYDataItem dataItem = new XYDataItem(currentTick, numberOfNeurons);
		xySeriesNumberOfExcitatoryNeurons.add(dataItem);
	}

	public void addNumberOfExcitatorySynapse(double currentTick,
			int numberOfExcitatorySynapses) {
		XYDataItem dataItem = new XYDataItem(currentTick,
				numberOfExcitatorySynapses);
		xySeriesNumberOfExcitatorySynapses.add(dataItem);
	}

	public void addNumberOfInhibitorySynapse(double currentTick,
			int numberOfInhibitorySynapses) {
		XYDataItem dataItem = new XYDataItem(currentTick,
				numberOfInhibitorySynapses);
		xySeriesNumberOfInhibitorySynapses.add(dataItem);
	}

	public void addValue(CooperativeAgent agent, String parameter,
			double currentTick, double criticality) {
		XYSeries xySeries = criticalityValues.get(agent);
		if (xySeries == null) {
			xySeries = new XYSeries(agent.toString());
			xySeries.setDescription(agent.toString());
			criticalityValues.put(agent, xySeries);
		}
		xySeries.add(currentTick, criticality);
	}

}
