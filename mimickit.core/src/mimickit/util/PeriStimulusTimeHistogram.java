package mimickit.util;

import java.util.List;

import org.jfree.data.Range;
import org.jfree.data.statistics.HistogramBin;
import org.jfree.data.statistics.HistogramDataset;

import umontreal.iro.lecuyer.charts.HistogramChart;
import umontreal.iro.lecuyer.charts.HistogramSeriesCollection;

/**
 * 
 * @author Önder Gürcan
 * 
 */
public class PeriStimulusTimeHistogram {

	private HistogramChart histogram = null;
	private DoubleArrayList data;
	private Reflex hReflex;

	private Range offsetRange = new Range(-400.0, 300.0);
	private double binSize = 0.5;
	private double prestimulusMean;
	private HistogramDataset dataset;
	private double[] valueArray;

	public PeriStimulusTimeHistogram(String triggerFileName,
			String mnDischargeFileName) {
		this(new DoubleArrayList(triggerFileName), new DoubleArrayList(
				mnDischargeFileName));
	}

	public PeriStimulusTimeHistogram(DoubleArrayList triggers,
			DoubleArrayList mnDischarges) {
		this.histogram = generatePSTH(triggers, mnDischarges);

		if (this.histogram != null) {
			calculatePSTHPrestimulusMean();
		}
	}

	private void calculatePSTHPrestimulusMean() {
		double stimulusTime = this.getZeroIndex();
		double sum = 0.0;
		for (int index = 0; index < stimulusTime; index++) {
			// get the current bin value
			int binValue = this.getBinValue(index);

			// calculate the sum of all bins
			sum += binValue;
		}
		//
		this.prestimulusMean = sum / stimulusTime;
	}

	public HistogramChart generatePSTH(DoubleArrayList triggers,
			DoubleArrayList mnDischarges) {
		// fill the histogram with spikes
		for (int index = 0; index < triggers.size(); index++) {
			double trigger = triggers.get(index);
			DoubleArrayList sweepVector = mnDischarges.getSweepVector4PSTH(
					trigger + offsetRange.getLowerBound(), trigger
							+ offsetRange.getUpperBound(), trigger);
			// getData().addAllOf(sweepVector);
			for (int i = 0; i < sweepVector.size(); i++) {
				double event = sweepVector.get(i);
				getData().add(MathUtils.digitalize(event));
			}
		}
		//
		DoubleArrayList data = getData();
		if (!data.isEmpty()) {
			histogram = new HistogramChart(
					"Peristimulus Time Histogram (PSTH)", "Time (ms)",
					"Counts", data);
			// Customizes the data plot
			HistogramSeriesCollection collec = histogram.getSeriesCollection();
			int bins = (int) (offsetRange.getLength() / binSize);
			collec.setBins(0, bins);
			double[] bounds = { offsetRange.getLowerBound(),
					offsetRange.getUpperBound(), 0.0, 30.0 };
			histogram.setManualRange(bounds);
			//
			findHReflex(offsetRange.getLowerBound());

			//
			valueArray = new double[data.size()];
			for (int i = 0; i < data.size(); i++) {
				double value = data.get(i);
				valueArray[i] = value;
			}
			getHistogramDataset().addSeries("", valueArray, 600);
		}
		//
		return histogram;
	}

	public double getPrestimulusMean() {
		return prestimulusMean;
	}

	private void findHReflex(double lowerOffset) {
		this.hReflex = null;
		//
		int max = getMax();
		List<HistogramBin> bins = getBins();
		for (int index = 0; index < bins.size(); index++) {
			if (getBinValue(index) == max) {
				double latency = (index * binSize) + lowerOffset;
				// H-Reflex must occur around 35 - 70 msn after stimulation.
				if (latency > 30.0 && latency < 70.0) {
					this.hReflex = new Reflex();
					this.hReflex.setLatency(latency);
					break;
				}

			}
		}
	}

	public DoubleArrayList getData() {
		if (this.data == null) {
			this.data = new DoubleArrayList();
		}
		return data;
	}

	// deleagted methods...

	@SuppressWarnings("unchecked")
	public List<HistogramBin> getBins() {
		if (this.histogram != null) {
			return this.histogram.getSeriesCollection().getBins(0);
		} else {
			return null;
		}
	}

	public int getBinValue(int index) {
		List<HistogramBin> bins = this.getBins();
		return bins.get(index).getCount();
	}

	public int getZeroIndex() {
		return (int) Math.abs(offsetRange.getLowerBound() / binSize);
	}

	public Range getOffsetRange() {
		return this.offsetRange;
	}

	public int getMax() {
		int max = Integer.MIN_VALUE;
		for (int i = 0; i < getBins().size(); i++) {
			if (getBinValue(i) > max) {
				max = getBinValue(i);
			}
		}
		return max;
	}

	public int getMaxIndex() {
		int maxIndex = 0;
		int max = Integer.MIN_VALUE;
		for (int i = 0; i < getBins().size(); i++) {
			if (getBinValue(i) > max) {
				max = getBinValue(i);
				maxIndex = i;
			}
		}
		return maxIndex;
	}

	@SuppressWarnings("unchecked")
	public int getMin() {
		List<HistogramBin> bins = this.histogram.getSeriesCollection().getBins(
				0);
		int min = Integer.MAX_VALUE;
		for (int i = 0; i < bins.size(); i++) {
			if (bins.get(i).getCount() < min) {
				min = bins.get(i).getCount();
			}
		}
		return min;
	}

	public HistogramChart getHistogram() {
		return getHistogram(offsetRange.getLowerBound(),
				offsetRange.getUpperBound());
	}

	public HistogramChart getHistogram(double from, double to) {
		HistogramChart histogram = new HistogramChart(
				"Peristimulus Time Histogram (PSTH)", "Time (ms)", "Counts",
				getData());
		// Customizes the data plot
		HistogramSeriesCollection collec = histogram.getSeriesCollection();
		int bins = (int) (offsetRange.getLength() / binSize);
		collec.setBins(0, bins);
		double[] bounds = { from, to, 0.0, 120.0 };
		histogram.setManualRange(bounds);
		//
		findHReflex(offsetRange.getLowerBound());
		//
		return histogram;
	}

	public Reflex getHReflex() {
		return this.hReflex;
	}

	public double getBinSize() {
		return this.binSize;
	}

	public HistogramDataset getHistogramDataset() {
		if (dataset == null) {
			dataset = new HistogramDataset();
		}
		return dataset;
	}

	public double[] getValueArray() {
		return valueArray;
	}
}
