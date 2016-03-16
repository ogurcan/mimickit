package mimickit.gui;

import java.awt.Color;

import javax.swing.JFrame;

import mimickit.SOEASYController;
import mimickit.model.MNDischargeRates;
import mimickit.model.SOEASYEnvironment;

import org.jfree.data.statistics.HistogramDataset;

public class PSTHPanel extends HistogramChartPanel {

	private static final long serialVersionUID = 1L;

	public static final int REFERENCE_MODE = 0;

	public static final int SIMULATED_MODE = 1;

	public static final int COMBINED_MODE = 2;

	private int mode = 0; // default mode

	private int bins = 600;

	public PSTHPanel(JFrame parent, int mode) {
		super(parent, "PSTH", "peristimulus time (ms)", "count", false);
		this.mode = mode;

		switch (mode) {
		case REFERENCE_MODE:
			setSeriesColor(0, Color.RED);
			break;

		case SIMULATED_MODE:
			setSeriesColor(0, Color.BLUE);
			break;

		case COMBINED_MODE:
			setSeriesColor(0, Color.RED);
			setSeriesColor(1, Color.BLUE);
			break;

		default:
			break;
		}
	}

	public void update() {
		if (mode == REFERENCE_MODE) {
			HistogramDataset dataset = new HistogramDataset();
			MNDischargeRates dataSet2 = SOEASYEnvironment.getInstance()
					.getDataSet();
			double[] valueArray = dataSet2.getReferencePSTH().getValueArray();
			dataset.addSeries("Reference", valueArray, bins);

			updateDataset(dataset);
		} else if (mode == SIMULATED_MODE) {
			if (SOEASYController.getInstance().isSimulationRunning()) {
				HistogramDataset dataset = new HistogramDataset();
				MNDischargeRates dataSet2 = SOEASYEnvironment.getInstance()
						.getDataSet();
				double[] valueArray = dataSet2.getSimulatedPSTH()
						.getValueArray();
				if (valueArray != null) {
					dataset.addSeries("Simulated", valueArray, bins);

					updateDataset(dataset);
				}
			}
		} else if (mode == COMBINED_MODE) {
			HistogramDataset dataset = new HistogramDataset();
			MNDischargeRates dataSet2 = SOEASYEnvironment.getInstance()
					.getDataSet();
			double[] valueArray = dataSet2.getReferencePSTH().getValueArray();
			if (valueArray != null) {
				dataset.addSeries("Reference", valueArray, bins);
			}

			if (SOEASYController.getInstance().isSimulationRunning()) {
				valueArray = dataSet2.getSimulatedPSTH().getValueArray();
				if (valueArray != null) {
					dataset.addSeries("Simulated", valueArray, bins);
				}
			}

			updateDataset(dataset);
		}

	}

}
