/* ===========================================================
 * SO-EASY : a free tool for simulating neural networks
 * ===========================================================
 *
 * (C) Copyright 2010-2012, by Önder Gürcan.
 *
 * Project Info:  http://code.google.com/p/nepe/
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * -------------
 * PeriStimulusFrequencygram.java
 * -------------
 * (C) Copyright 2010-2012, Önder Gürcan.
 *
 * Original Author:  Önder Gürcan;
 * Contributor(s):   -;
 *                   
 *                   
 *
 * Changes
 * -------
 *
 */

package mimickit.util;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import mimickit.model.SOEASYParameters;

import org.jfree.data.Range;
import org.jfree.data.xy.XYDataItem;

public class StaticPSF extends PeristimulusFrequencygram {

	private DigitalizedXYSeries consecutiveEmptyBins;
	private int consecutiveEmptyBinCount = 4;
	private double prestimulusStandardDeviation = 0.0;

	/**
	 * Constructor for a static PSF with data files.
	 * 
	 * @param triggersFileName
	 * @param mnDischargesFileName
	 * @param filter
	 */
	public StaticPSF(String triggersFileName, String mnDischargesFileName,
			boolean filter) {
		this(new DoubleArrayList(triggersFileName), new DoubleArrayList(
				mnDischargesFileName), filter);
	}

	/**
	 * Constructor for a static PSF with double values.
	 * 
	 * @param triggers
	 * @param mnDischarges
	 * @param filter
	 */
	public StaticPSF(DoubleArrayList triggers, DoubleArrayList mnDischarges,
			boolean filter) {
		// set frequency data
		this.triggers = triggers;
		this.mnDischarges = mnDischarges;

		// generate the peristumulus frequencygram by putting the discharge
		// rates into "dischargeRates" variable.
		generatePSF();

		countEmptyBins();

		identifyConsecutiveEmptyBins();

		// // filter the psf if needed
		// if (filter) {
		// filterPSF();
		// }

		calculatePrestimulusMean();

		calculatePrestimulusStandardDeviation();
	}

	private void calculatePrestimulusStandardDeviation() {
		Averager squareAverager = new Averager();
		for (double xValue = offsetRange.getLowerBound() + binSize; xValue <= 0.0; xValue += binSize) {
			List<XYDataItem> itemsAt = dischargeRates.getItemsAt(xValue);
			if (!itemsAt.isEmpty()) {
				double yValue = itemsAt.get(0).getYValue();
				double diff = yValue
						- this.meanPrestimulusDischargeRate.getAverage();
				squareAverager.update(diff * diff);
			}
		}
		this.prestimulusStandardDeviation = Math.sqrt(squareAverager
				.getAverage());
	}

	private void generatePSF() {
		highestFrequency = Double.MIN_VALUE;
		lowestFrequency = Double.MAX_VALUE;
		//
		this.dischargeRates = new DigitalizedXYSeries(offsetRange, binSize);

		// fill the psf with instantaneous frequencies
		for (int i = 0; i < this.triggers.size(); i++) {
			double stimulusTime = this.triggers.get(i);
			DoubleArrayList sweepVector = this.mnDischarges.getSweepVector4PSF(
					stimulusTime + offsetRange.getLowerBound(), stimulusTime
							+ offsetRange.getUpperBound(), stimulusTime);

			for (int index = 1; index < sweepVector.size(); index++) {
				double previousEvent = sweepVector.get(index - 1);
				double currentEvent = sweepVector.get(index);
				double frequency = 1000.0 / (currentEvent - previousEvent);
				// if the frequency value is not unusually high (>50) or low
				// (<1) the frequency is added to the PSF
				if ((frequency > 1.0) && (frequency < 30.0)) {
					// 104.635999 -> 104.5
					currentEvent = MathUtils.digitalize(currentEvent);
					try {
						if ((currentEvent >= offsetRange.getLowerBound())
								&& (currentEvent <= offsetRange.getUpperBound())) {
							this.dischargeRates.add(currentEvent, frequency);
							//
							if (frequency > highestFrequency) {
								highestFrequency = frequency;
							} else if (frequency < lowestFrequency) {
								lowestFrequency = frequency;
							}
						}
					} catch (IndexOutOfBoundsException e) {
						// event is out of range.
					}
				} // if
			} // for
		} // for
	}

	/**
	 * Counts empty bins in this PSF.
	 */
	private void countEmptyBins() {
		this.consecutiveEmptyBins = new DigitalizedXYSeries(new Range(1.0,
				400.0), 1.0);

		int counter = 0;
		for (double latency = this.offsetRange.getLowerBound(); latency <= this.offsetRange
				.getUpperBound(); latency += 0.5) {
			List<XYDataItem> bin = dischargeRates.getItemsAt(MathUtils
					.digitalize(latency));
			if (bin.isEmpty()) {
				counter++;
			} else {
				if (counter != 0) {
					List<XYDataItem> itemsAt = this.consecutiveEmptyBins
							.getItemsAt(counter);
					if (itemsAt.isEmpty()) {
						this.consecutiveEmptyBins.add(counter, 1);
					} else {
						XYDataItem xyDataItem = itemsAt.get(0);
						xyDataItem.setY(xyDataItem.getYValue() + 1);
					}
					counter = 0;
				}
			}
		}

		counter = 0;
		for (int index = 1; index <= 100; index++) {
			List<XYDataItem> itemsAt = this.consecutiveEmptyBins
					.getItemsAt(index);
			if (itemsAt.isEmpty()) {
				break;
			}
			counter++;
		}

		if (counter > 0) {
			this.consecutiveEmptyBinCount = counter;
		}
	}

	public DigitalizedXYSeries getConsecutiveEmptyBins() {
		return this.consecutiveEmptyBins;
	}

	/**
	 * Identifies the consecutive empty bins. These bins will be regarded as
	 * empty always.
	 */
	private void identifyConsecutiveEmptyBins() {
		this.consecutiveEmptyBinHashtable = new Hashtable<Double, Boolean>();
		double latency = this.offsetRange.getLowerBound();// + binSize;
		double pathwayEnd = SOEASYParameters.getInstance().getParameter(
				SOEASYParameters.PATHWAY_END)
				+ binSize;
		while (latency <= pathwayEnd) {
			boolean isEmpty = true;
			for (int index = 0; index < consecutiveEmptyBinCount; index++) {
				List<XYDataItem> bin = dischargeRates.getItemsAt(MathUtils
						.digitalize(latency + (index * binSize)));

				isEmpty = isEmpty && bin.isEmpty();
			}

			if (isEmpty) {
				for (int index = 0; index < consecutiveEmptyBinCount; index++) {
					this.consecutiveEmptyBinHashtable.put(
							MathUtils.digitalize(latency + (index * binSize)),
							true);
				}
				latency = MathUtils.digitalize(latency + this.binSize
						* consecutiveEmptyBinCount);
			} else {
				this.consecutiveEmptyBinHashtable.put(latency, false);
				latency = MathUtils.digitalize(latency + this.binSize);
			}
		}
	}

	/**
	 * Filters this PSF according to the approach described in [Türker1994]. The
	 * bins in the PSF diagram with no frequency value are given the values in
	 * the preceding bin.
	 */
	private void filterPSF() {
		double latency = MathUtils.digitalize(this.offsetRange.getLowerBound()
				+ binSize);
		double pathwayBeginning = SOEASYParameters.getInstance().getParameter(
				SOEASYParameters.PATHWAY_BEGINNING);
		double pathwayEnd = SOEASYParameters.getInstance().getParameter(
				SOEASYParameters.PATHWAY_END);
		while (latency <= pathwayEnd) {
			List<XYDataItem> currentBin = this.dischargeRates
					.getItemsAt(latency);
			if (currentBin == null) {
				System.out.println("Latency: " + latency);
			}
			Boolean consecutivelyEmpty = consecutiveEmptyBinHashtable
					.get(latency);
			if (consecutivelyEmpty == null) {
				System.out.println("hebekle: " + latency);
			}
			// if the current empty bin is not consecutively empty ...
			if (currentBin.isEmpty() && (!consecutivelyEmpty)) {
				// ... copy the values in the preceding bin to the current bin
				List<XYDataItem> precedingBin = this.dischargeRates
						.getItemsAt(latency - binSize);
				for (int index = 0; index < precedingBin.size(); index++) {
					XYDataItem dischargeRate = precedingBin.get(index);
					dischargeRates.add(latency, dischargeRate.getYValue());
				}
			}
			//
			latency = MathUtils.digitalize(latency + this.binSize);
		}
	}

	public boolean isConsecutivelyEmpty(double latency) {
		boolean result = true;

		Boolean binIsEmpty = this.consecutiveEmptyBinHashtable.get(latency);
		if (binIsEmpty != null) {
			result = binIsEmpty;
		}

		return result;
	}

	private void calculatePrestimulusMean() {
		this.meanPrestimulusDischargeRate = new Averager();
		for (double xValue = offsetRange.getLowerBound(); xValue <= 0.0; xValue += binSize) {
			Iterator<XYDataItem> iterator = this.dischargeRates.getItemsAt(
					xValue).iterator();
			while (iterator.hasNext()) {
				XYDataItem xyDataItem = iterator.next();
				this.meanPrestimulusDischargeRate
						.update(xyDataItem.getYValue());
			}
		}
	}

	@SuppressWarnings("unused")
	private DigitalizedXYSeries calculateAverageInParallel(
			DigitalizedXYSeries inputs, int smooth) {
		DigitalizedXYSeries avarages = null;

		try {
			avarages = calculateAverageInParallel(inputs);
			for (int i = 0; i < smooth; i++) {
				avarages = calculateAverageInParallel(avarages);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return avarages;
	}

	private DigitalizedXYSeries calculateAverageInParallel(
			final DigitalizedXYSeries inputs) throws InterruptedException,
			ExecutionException {
		final DigitalizedXYSeries output = new DigitalizedXYSeries(offsetRange,
				binSize);

		int threads = Runtime.getRuntime().availableProcessors();
		ExecutorService service = Executors.newFixedThreadPool(threads);
		List<Future<List<XYDataItem>>> futures = new Vector<Future<List<XYDataItem>>>();
		//
		final double interval = 4.0;			
		final double stepSize = 0.5;
		//
		int numberOfSegments = threads;
		double segmentSize = offsetRange.getLength() // (upperOffset -
														// lowerOffset)
				/ (double) numberOfSegments;
		// System.out.println("number of segments: " + numberOfSegments
		// + ", segment size: " + segmentSize);
		for (int segmentNo = 0; segmentNo < numberOfSegments; segmentNo++) {
			final double beginIndex = offsetRange.getLowerBound()
					+ (segmentNo * segmentSize);
			final double endIndex = beginIndex + segmentSize - binSize;
			// System.out.println("Segment " + segmentNo + ", from " +
			// beginIndex + " to " + endIndex);
			Callable<List<XYDataItem>> callable = new Callable<List<XYDataItem>>() {
				@Override
				public List<XYDataItem> call() throws Exception {
					List<XYDataItem> dataItems = new Vector<XYDataItem>();
					double fromIndex = beginIndex;
					double toIndex = endIndex;
					for (double time = fromIndex; time < toIndex; time += stepSize) {
						// process your input here and compute the output
						boolean isConsecutivelyEmpty = true;
						if (consecutiveEmptyBinHashtable.containsKey(time)) {
							isConsecutivelyEmpty = consecutiveEmptyBinHashtable
									.get(time).booleanValue();
						} else {
							isConsecutivelyEmpty = false;
						}

						// if the current bin is not a consecutive empty bin
						if (!isConsecutivelyEmpty) {
							List<XYDataItem> subList = inputs.getItemsBetween(
									time - interval / 2, time + interval / 2);
							//
							double total = 0.0;
							Iterator<XYDataItem> iterator = subList.iterator();
							while (iterator.hasNext()) {
								XYDataItem xyDataItem = iterator.next();
								total += xyDataItem.getYValue();
							}
							if (!subList.isEmpty()) {
								double avarage = total
										/ (double) subList.size();
								dataItems.add(new XYDataItem(time, avarage));
							}
						}
					} // for loop

					return dataItems;
				}
			};
			Future<List<XYDataItem>> future = service.submit(callable);
			futures.add(future);
		} // for loop
		service.shutdown();

		List<XYDataItem> allData = new Vector<XYDataItem>();
		Iterator<Future<List<XYDataItem>>> iterator = futures.iterator();
		while (iterator.hasNext()) {
			Future<List<XYDataItem>> future = iterator.next();
			allData.addAll(future.get());
		}

		return output;
	}

	/**
	 * Return data interval or null.
	 * 
	 * @param time
	 * @return
	 */
	public double[] getDataInterval(double latency) {
		List<XYDataItem> rates = dischargeRates.getSortedItemsAt(latency);
		double[] dataInterval = null;
		if (!rates.isEmpty()) {
			dataInterval = new double[2];
			dataInterval[0] = rates.get(0).getYValue();
			dataInterval[1] = rates.get(rates.size() - 1).getYValue();
		}
		return dataInterval;
	}

	/**
	 * This method must not return null. There must be a feedback...
	 * 
	 * @param latency
	 * @return
	 */
	public Range getRateIntervalUsingAverage(double latency) {
		List<XYDataItem> average = avarageDischargeRates.getItemsAt(latency);
		Range averageInterval = null;

		if (average.isEmpty()) {
			average = avarageDischargeRates.getItemsAt(latency - 2.0);
		}

		if (!average.isEmpty()) {
			double averageFrequency = average.get(0).getYValue();
			double tolerance = SOEASYParameters.getInstance().getParameter(
					SOEASYParameters.DERIVATIVE_FAULT_TOLERANCE);
			double lowerBound = averageFrequency - tolerance;
			double upperBound = averageFrequency + tolerance;
			averageInterval = new Range(lowerBound, upperBound);
		}

		return averageInterval;
	}

	public int getConsecutiveEmptyBinCount() {
		return consecutiveEmptyBinCount;
	}

	public double getPrestimulusStandartDeviation() {
		return this.prestimulusStandardDeviation;
	}
}
