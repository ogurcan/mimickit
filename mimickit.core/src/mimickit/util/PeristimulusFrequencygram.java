package mimickit.util;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.jfree.data.Range;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import umontreal.iro.lecuyer.charts.ScatterChart;
import umontreal.iro.lecuyer.charts.XYChart;
import umontreal.iro.lecuyer.charts.XYLineChart;
import umontreal.iro.lecuyer.charts.XYListSeriesCollection;

/**
 * The peristimulus frequency-gram (PSF) plots the instantaneous discharge
 * frequency (rate) values against the time of the stimulus.
 * 
 * @author Önder Gürcan
 * 
 */
abstract public class PeristimulusFrequencygram {

	/**
	 * Range of the offset.
	 */
	protected Range offsetRange = new Range(-400.0, 300.0);

	/**
	 * The size of each bin.
	 */
	protected double binSize = 0.5;

	/**
	 * Keeps the discharge rate values. A discharge rate is composed of
	 * "latency" and "frequency".
	 */
	protected DigitalizedXYSeries dischargeRates;

	/**
	 * Keeps the average discharge rate values. An average discharge rate is
	 * computed by using the neighbor bins.
	 */
	protected DigitalizedXYSeries avarageDischargeRates;

	/**
	 * The mean discharge rate before the time of stimulation.
	 */
	protected Averager meanPrestimulusDischargeRate;

	/**
	 * The highest frequency value in this PSF. This value is for manual ranging
	 * of the XYChart.
	 */
	protected double highestFrequency;

	/**
	 * The lowest frequency value in this PSF. This value is for manual ranging
	 * of the XYChart.
	 */
	protected double lowestFrequency;

	/**
	 * Sensory neuron triggers.
	 */
	protected DoubleArrayList triggers;

	/**
	 * Motoneuron discharge values.
	 */
	protected DoubleArrayList mnDischarges;

	/**
	 * Represents consecutive empty bins as <bin, empty or not>. A bin is empty
	 * if its corresponding value is "true", otherwise that bin is said to be
	 * not empty.
	 */
	protected Hashtable<Double, Boolean> consecutiveEmptyBinHashtable;

	protected double lastDischargeRate;

	public DoubleArrayList getTriggers() {
		return triggers;
	}

	public DoubleArrayList getMnDischarges() {
		return mnDischarges;
	}

	public DigitalizedXYSeries getDischargeRates() {
		return this.dischargeRates;
	}

	public DigitalizedXYSeries getAvarageDischargeRates() {
		return this.avarageDischargeRates;
	}

	public Hashtable<Double, Boolean> getConsecutiveEmptyBinHashtable() {
		return this.consecutiveEmptyBinHashtable;
	}

	public double getMeanPrestimulusDischargeRate() {
		return this.meanPrestimulusDischargeRate.getAverage();
	}

	public Range getOffseRange() {
		return this.offsetRange;
	}

	public double getUpperOffsetOf(double trigger) {
		return trigger + this.offsetRange.getUpperBound();
	}

	public double getLowerOffsetOf(double trigger) {
		return trigger + this.offsetRange.getLowerBound();
	}

	public double getBinSize() {
		return this.binSize;
	}

	public XYChart getDischargeRatesXYChart(double from, double to) {
		XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
		//
		xySeriesCollection.addSeries(getDischargeRates().toXYSeries(
				"Discharge Rates"));
		//
		xySeriesCollection.addSeries(getYSeries(meanPrestimulusDischargeRate
				.getAverage()));
		//
		ScatterChart xyChart = new ScatterChart("PSF - Raw Data", "Time (ms)",
				"PSF (Hz)", xySeriesCollection);
		// Customizes the data plot
		XYListSeriesCollection collec = xyChart.getSeriesCollection();
		collec.setColor(0, Color.RED); // discharge rates
		collec.setColor(1, Color.GREEN); // mean prestimulus discharge rates
		//
		double[] bounds = { from, to, -1.0, 20.0 };
		xyChart.setManualRange(bounds);
		return xyChart;
	}

	public XYChart getDischargeRatesXYChartWithAverageDischargeRates(
			double from, double to, XYSeries xySeries) {
		XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
		//
		xySeriesCollection.addSeries(getDischargeRates().toXYSeries(
				"Discharge Rates"));
		xySeriesCollection.addSeries(xySeries);
		//
		xySeriesCollection.addSeries(getYSeries(meanPrestimulusDischargeRate
				.getAverage()));
		//
		ScatterChart xyChart = new ScatterChart("PSF - Raw Data", "Time (ms)",
				"PSF (Hz)", xySeriesCollection);
		// Customizes the data plot
		XYListSeriesCollection collec = xyChart.getSeriesCollection();
		collec.setColor(0, Color.RED); // discharge rates
		collec.setColor(1, Color.GREEN); // mean prestimulus discharge rates
		//
		double[] bounds = { from, to, (lowestFrequency - 3.0),
				(highestFrequency + 3.0) };
		xyChart.setManualRange(bounds);
		return xyChart;
	}

	public XYChart getAverageDischargeRatesXYChart(double from, double to) {
		XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
		//
		xySeriesCollection.addSeries(getAvarageDischargeRates().toXYSeries(
				"Average Discharge Rates"));
		//
		xySeriesCollection.addSeries(getYSeries(meanPrestimulusDischargeRate
				.getAverage()));
		//
		ScatterChart xyChart = new ScatterChart(
				"PSF - Average Discharge Rates", "Time (ms)", "PSF (Hz)",
				xySeriesCollection);
		// Customizes the data plot
		XYListSeriesCollection collec = xyChart.getSeriesCollection();
		collec.setColor(0, Color.RED); // discharge rates
		collec.setColor(1, Color.BLUE); // mean prestimulus discharge rates
		collec.setColor(2, Color.GREEN); // mean prestimulus discharge rates
		//
		double[] bounds = { from, to, 5.0, 15.0 };
		xyChart.setManualRange(bounds);
		return xyChart;
	}

	public XYChart getAverageDischargeRatesLineChart(double from, double to) {
		XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
		//
		xySeriesCollection.addSeries(getAvarageDischargeRates().toXYSeries(
				"Average Discharge Rates"));
		//
		xySeriesCollection.addSeries(getYSeries(meanPrestimulusDischargeRate
				.getAverage()));
		//
		XYLineChart xyChart = new XYLineChart("PSF - Average Discharge Rates",
				"Time (ms)", "PSF (Hz)", xySeriesCollection);
		// Customizes the data plot
		XYListSeriesCollection collec = xyChart.getSeriesCollection();
		collec.setColor(0, Color.RED); // discharge rates
		collec.setColor(1, Color.GREEN); // mean prestimulus discharge rates
		//
		double[] bounds = { from, to, (lowestFrequency - 3.0),
				(highestFrequency + 3.0) };
		xyChart.setManualRange(bounds);
		return xyChart;
	}

	/**
	 * Generates a horizontal line using the given yValue.
	 * 
	 * @param yValue
	 * @return
	 */
	private XYSeries getYSeries(double yValue) {
		XYSeries xySeries = new XYSeries("Mean prestimulus discharge rate");
		for (double x = offsetRange.getLowerBound(); x < offsetRange
				.getUpperBound(); x++) {
			xySeries.add(x, yValue);
		}
		return xySeries;
	}

	public XYSeries getPrestimulusMeanXYSeries() {
		return getYSeries(this.meanPrestimulusDischargeRate.getAverage());
	}

	public void writeSignalsToFile(String folderName) {
		triggers.writeToFile(folderName + "/sim_sensory_neuron_triggers.txt");
		mnDischarges.writeToFile(folderName
				+ "/sim_motor_neuron_discharges.txt");
	}

	public void toFile(String fileName) {
		try {
			File eventFile = new File(fileName);
			FileWriter fileWriter = new FileWriter(eventFile);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			Iterator<?> iterator = dischargeRates.getItems().iterator();
			while (iterator.hasNext()) {
				XYDataItem xyDataItem = (XYDataItem) iterator.next();
				bufferedWriter.write(xyDataItem.getXValue() + ", "
						+ xyDataItem.getYValue() + "\n");
			}
			bufferedWriter.close();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the latency value of the last mn discharge if there exists at
	 * least one trigger and the latency value is between the PSF boundaries.
	 * Otherwise, the method returns "not-a-number" (Double.NaN).
	 * 
	 * @return
	 */
	public double getLatencyOfLastMnDischarge() {
		double latency = Double.NaN;
		if (this.triggers.size() > 0) {
			if (this.triggers.size() == 1) {
				double lastTrigger = getLastTrigger();

				double lastDischarge = getLastMnDischarge();

				if (offsetRange.contains(lastDischarge - lastTrigger)) {
					latency = lastDischarge - lastTrigger;
				}
			} else if (this.triggers.size() > 1) {
				double lastTrigger = getLastTrigger();
				double previousTrigger = getPreviousTrigger();

				double lastDischarge = getLastMnDischarge();
				if (offsetRange.contains(lastDischarge - lastTrigger)) {
					latency = lastDischarge - lastTrigger;
				} else if (offsetRange
						.contains(lastDischarge - previousTrigger)) {
					latency = lastDischarge - previousTrigger;
				}
			}
		}
		return latency;
	}

	protected double getLastTrigger() {
		int triggerCount = this.triggers.size();
		if (triggerCount > 0) {
			return this.triggers.lastElement();
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	protected double getLastMnDischarge() {
		int mnDischargeCount = this.mnDischarges.size();
		if (mnDischargeCount > 0) {
			return this.mnDischarges.lastElement();
		} else {
			return Double.NaN;
		}
	}

	protected double getPreviousTrigger() {
		int triggerCount = this.triggers.size();
		if (triggerCount > 1) {
			return this.triggers.get(triggerCount - 2);
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	protected double getPreviousMnDischarge() {
		int mnDischargeCount = this.mnDischarges.size();
		if (mnDischargeCount > 1) {
			return this.mnDischarges.get(mnDischargeCount - 2);
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	/**
	 * Returns the last discharge rate value.
	 * 
	 * @return
	 */
	public double getLastDischargeRate() {
		double isi = getLastMnDischarge() - getPreviousMnDischarge();
		this.lastDischargeRate = 1000.0 / isi;
		return this.lastDischargeRate;
	}

	/**
	 * Returns the average discharge rate for a given latency.
	 * 
	 * @param latency
	 */
	public double getAverageDischargeRateAt(double latency) {
		if (offsetRange.contains(latency)) {
			double result = Double.NaN;
			List<XYDataItem> items = getAvarageDischargeRates().getItems();
			Iterator<XYDataItem> iterator = items.iterator();
			while (iterator.hasNext()) {
				XYDataItem next = iterator.next();
				if (next.getXValue() == latency) {
					result = next.getYValue();
					break;
				}
			}
			return result;
		} else {
			throw new IndexOutOfBoundsException(
					"The latency value should be between "
							+ offsetRange.getLowerBound() + " and "
							+ offsetRange.getUpperBound() + " .");
		}
	}	

}
