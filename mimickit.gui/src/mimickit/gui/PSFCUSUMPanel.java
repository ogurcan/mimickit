package mimickit.gui;

import java.awt.Color;

import javax.swing.JFrame;

import mimickit.SOEASYController;
import mimickit.model.MNDischargeRates;
import mimickit.util.CuSum;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class PSFCUSUMPanel extends LineChartPanel {

	private static final long serialVersionUID = 1L;

	public static final int REFERENCE_MODE = 0;

	public static final int SIMULATED_MODE = 1;

	public static final int COMBINED_MODE = 2;

	private int mode = 0; // default mode

	public PSFCUSUMPanel(JFrame parent, int mode) {
		super(parent, "PSF-CUSUM", "peristimulus time (ms)", "discharge rates (hz)",
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
			CuSum referencePSFCUSUM = SOEASYController.getInstance().getDataSet().getReferencePSFCUSUM();	
			XYSeries cusumXYSeries = referencePSFCUSUM.getCusumXYSeries()
					.toXYSeries("PSF-CUSUM");
			XYSeriesCollection dataset = new XYSeriesCollection();
			dataset.addSeries(cusumXYSeries);

			updateDataset(dataset);	
			updateTarget();
		} else if (mode == SIMULATED_MODE) {
			MNDischargeRates dataSet2 = SOEASYController.getInstance().getDataSet();
			CuSum simulatedPSFCUSUM = dataSet2.getSimulatedPSFCUSUM();
			XYSeries dischargeRates = simulatedPSFCUSUM.getCusumXYSeries()
					.toXYSeries("");
			XYSeriesCollection dataset = new XYSeriesCollection();
			dataset.addSeries(dischargeRates);

			updateDataset(dataset);
			updateTarget();
		} else if (mode == COMBINED_MODE) {
			MNDischargeRates dataSet2 = SOEASYController.getInstance().getDataSet();
			CuSum referencePSFCUSUM = SOEASYController.getInstance().getDataSet().getReferencePSFCUSUM();	
			XYSeries cusumXYSeries = referencePSFCUSUM.getCusumXYSeries()
					.toXYSeries("PSF-CUSUM");
			XYSeriesCollection dataset = new XYSeriesCollection();
			dataset.addSeries(cusumXYSeries);

			if (SOEASYController.getInstance().isSimulationRunning()) {
				CuSum simulatedPSFCUSUM = dataSet2.getSimulatedPSFCUSUM();
				cusumXYSeries = simulatedPSFCUSUM.getCusumXYSeries()
						.toXYSeries("");				
				dataset.addSeries(cusumXYSeries);
			}

			updateDataset(dataset);
			updateTarget();
		}

	}

}
