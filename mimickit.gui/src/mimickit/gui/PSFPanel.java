package mimickit.gui;

import java.awt.Color;

import javax.swing.JFrame;

import mimickit.SOEASYController;
import mimickit.util.PeristimulusFrequencygram;
import mimickit.util.StaticPSF;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class PSFPanel extends XYScatterChartPanel {

	private static final long serialVersionUID = 1L;

	public static final int REFERENCE_MODE = 0;

	public static final int SIMULATED_MODE = 1;

	public static final int COMBINED_MODE = 2;

	private int mode = 0; // default mode

	public PSFPanel(JFrame parent, int mode) {
		super(parent, "PSF", "peristimulus time (ms)", "discharge rates (hz)",
				false);
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
			StaticPSF referencePSF = SOEASYController.getInstance()
					.getDataSet().getReferencePSF();
			XYSeries dischargeRates = referencePSF.getDischargeRates()
					.toXYSeries("");
			XYSeriesCollection dataset = new XYSeriesCollection();
			dataset.addSeries(dischargeRates);

			updateDataset(dataset);
			updateTarget();
		} else if (mode == SIMULATED_MODE) {
			PeristimulusFrequencygram simulatedPSF = SOEASYController
					.getInstance().getDataSet().getSimulatedPSF();
			XYSeries dischargeRates = simulatedPSF.getDischargeRates()
					.toXYSeries("");
			XYSeriesCollection dataset = new XYSeriesCollection();
			dataset.addSeries(dischargeRates);

			updateDataset(dataset);
		} else if (mode == COMBINED_MODE) {
			StaticPSF referencePSF = SOEASYController.getInstance()
					.getDataSet().getReferencePSF();

			XYSeriesCollection dataset = new XYSeriesCollection();
			XYSeries dischargeRates = referencePSF.getDischargeRates()
					.toXYSeries("ReferencePSF");
			dataset.addSeries(dischargeRates);

			if (SOEASYController.getInstance().isSimulationRunning()) {
				PeristimulusFrequencygram simulatedPSF = SOEASYController
						.getInstance().getDataSet().getSimulatedPSF();
				dischargeRates = simulatedPSF.getDischargeRates().toXYSeries(
						"SimulatedPSF");
				dataset.addSeries(dischargeRates);
			}

			updateDataset(dataset);
			updateTarget();
		}

	}

}
