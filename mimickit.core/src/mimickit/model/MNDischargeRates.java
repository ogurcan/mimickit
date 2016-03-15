package mimickit.model;

import mimickit.util.CuSum;
import mimickit.util.DoubleArrayList;
import mimickit.util.PeriStimulusTimeHistogram;
import mimickit.util.StaticPSF;

/**
 * Records discharge rates from Neurons and notifies DataListeners.
 * 
 * @author Onder Gurcan
 * 
 */
public class MNDischargeRates {

	private String referenceTriggerDataFile;
	private String referenceMNDischargeDataFile;

	private StaticPSF referencePSF;
	private PeriStimulusTimeHistogram referencePSTH;

	private CuSum referencePSFCUSUM;
	private CuSum referencePSTHCUSUM;

	private double referenceTriggerDuration = 2000000;
	private int referenceTriggerCount = 1000;

	private int psfSmooth = 1;
	private double psfMovingAverageInterval = 4.0;
	
	private int psthSmooth = 0;
	private double psthMovingAverageInterval = 1.5;

	private DoubleArrayList simulatedTriggers;
	private DoubleArrayList simulatedMNDischarges;

	public MNDischargeRates(String referenceTriggerDataFile,
			String referenceMNDischargeDataFile) {
		this.referenceTriggerDataFile = referenceTriggerDataFile;
		this.referenceMNDischargeDataFile = referenceMNDischargeDataFile;

		this.referencePSF = new StaticPSF(referenceTriggerDataFile,
				referenceMNDischargeDataFile, true);
		this.referencePSTH = new PeriStimulusTimeHistogram(
				referenceTriggerDataFile, referenceMNDischargeDataFile);

		DoubleArrayList triggers = getReferencePSF().getTriggers();
		double firstTrigger = triggers.get(0);
		double lastTrigger = triggers.lastElement();
		this.referenceTriggerDuration = Math.round(lastTrigger - firstTrigger);
		this.referenceTriggerCount = triggers.size();

		this.simulatedTriggers = new DoubleArrayList();
		this.simulatedMNDischarges = new DoubleArrayList();
	}

	public String getReferenceTriggerDataFile() {
		return referenceTriggerDataFile;
	}

	public String getReferenceMNDischargeDataFile() {
		return referenceMNDischargeDataFile;
	}

	public StaticPSF getReferencePSF() {
		return referencePSF;
	}

	public PeriStimulusTimeHistogram getReferencePSTH() {
		return referencePSTH;
	}

	public CuSum getReferencePSFCUSUM() {
		if (this.referencePSFCUSUM == null) {
			this.referencePSFCUSUM = new CuSum(getReferencePSF(), psfSmooth,
					psfMovingAverageInterval);
		}
		return this.referencePSFCUSUM;
	}

	public CuSum getReferencePSTHCUSUM() {
		if (this.referencePSTHCUSUM == null) {
			this.referencePSTHCUSUM = new CuSum(getReferencePSTH(), psthSmooth,
					psthMovingAverageInterval);
		}
		return this.referencePSTHCUSUM;
	}

	public double getReferenceTriggerDuration() {
		return referenceTriggerDuration;
	}

	public int getReferenceTriggerCount() {
		return referenceTriggerCount;
	}

	public void setSmoothingCount(int smooth) {
		this.psfSmooth = smooth;
		this.referencePSFCUSUM = null;
		this.referencePSTHCUSUM = null;
	}

	@Deprecated
	public int getSmooth() {
		return this.psfSmooth;
	}

	public void setMovingAverageInterval(double movingAverageInterval) {
		this.psfMovingAverageInterval = movingAverageInterval;
		this.referencePSFCUSUM = null;
		this.referencePSTHCUSUM = null;
	}

	@Deprecated
	public double getMovingAverageInterval() {
		return psfMovingAverageInterval;
	}

	public DoubleArrayList getSimulatedTriggers(int triggerCount) {
		DoubleArrayList result = new DoubleArrayList();
		//
		int index = 0;
		if (this.simulatedTriggers.size() > triggerCount) {
			index = this.simulatedTriggers.size() - triggerCount;
		}

		for (; index < this.simulatedTriggers.size(); index++) {
			double signal = this.simulatedTriggers.get(index);
			result.add(signal);
		}
		//
		return result;
	}
	
	public DoubleArrayList getMNDischarges(double fromTime) {
		DoubleArrayList result = new DoubleArrayList(); //
		int fromIndex = -1;
		for (int index = 0; index < this.simulatedMNDischarges.size(); index++) {
			double signal = this.simulatedMNDischarges.get(index);
			if (signal > fromTime) {
				fromIndex = index;
				int toIndex = this.simulatedMNDischarges.size() - 1;
				result = this.simulatedMNDischarges.subList(fromIndex, toIndex);
				break;
			}
		}
		//
		return result;
	}		

	public StaticPSF getSimulatedPSF() {
		StaticPSF simulatedPSF = null;
		DoubleArrayList simulatedTriggers = getSimulatedTriggers(referenceTriggerCount);
		if (!simulatedTriggers.isEmpty()) {
			double firstTrigger = simulatedTriggers.get(0);
			DoubleArrayList simulatedMNDischarges = getMNDischarges(firstTrigger - 3000);
			simulatedPSF = new StaticPSF(simulatedTriggers,
					simulatedMNDischarges, true);
		} else {
			simulatedPSF = new StaticPSF(simulatedTriggers,
					this.simulatedMNDischarges, true);
		}
		return simulatedPSF;
	}
	
	public CuSum getSimulatedPSFCUSUM() {		
		return new CuSum(getSimulatedPSF(), psfSmooth, psfMovingAverageInterval);
	}

	public PeriStimulusTimeHistogram getSimulatedPSTH() {
		PeriStimulusTimeHistogram simulatedPSTH = null;
		DoubleArrayList simulatedTriggers = getSimulatedTriggers(referenceTriggerCount);
		if (!simulatedTriggers.isEmpty()) {
			double firstTrigger = simulatedTriggers.get(0);
			DoubleArrayList simulatedMNDischarges = getMNDischarges(firstTrigger - 3000);
			simulatedPSTH = new PeriStimulusTimeHistogram(simulatedTriggers,
					simulatedMNDischarges);
		} else {
			simulatedPSTH = new PeriStimulusTimeHistogram(simulatedTriggers,
					this.simulatedMNDischarges);
		}
		return simulatedPSTH;
	}
	
	public CuSum getSimulatedPSTHCUSUM() {
		// TODO psth boş ise CUSUM da boş veya null olsun.
		return new CuSum(getSimulatedPSTH(), psthSmooth, psthMovingAverageInterval);
	}
	
	public void addSimulatedTrigger(double trigger) {
		this.simulatedTriggers.add(trigger);
	}

	public DoubleArrayList getSimulatedTriggers() {
		return this.simulatedTriggers;
	}

	public void addSimulatedMNDischarge(double spikeTime) {
		this.simulatedMNDischarges.add(spikeTime);
	}
	
	public int comparePSFCUSUMsAt(double latency) {
		return this.referencePSFCUSUM.compareAt(getSimulatedPSFCUSUM(), latency);
	}
	
	public int comparePSTHCUSUMsAt(double latency) {
		return this.referencePSTHCUSUM.compareAt(getSimulatedPSTHCUSUM(), latency);
	}

}
