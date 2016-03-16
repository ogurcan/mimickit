package mimickit.gui;

import java.awt.Color;

import javax.swing.JFrame;

import mimickit.SOEASYController;
import mimickit.model.MNDischargeRates;
import mimickit.util.CuSum;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class PSFCUSUMDerivativePanel extends XYScatterChartPanel {

	private static final long serialVersionUID = 1L;

	public static final int REFERENCE_MODE = 0;

	public static final int SIMULATED_MODE = 1;

	public static final int COMBINED_MODE = 2;

	private int mode = 0; // default mode

	public PSFCUSUMDerivativePanel(JFrame parent, int mode) {
		super(parent, "PSF-CUSUM Derivation", "peristimulus time (ms)",
				"average discharge rates (hz)", false);
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

			XYSeries derivativeXYSeries = referencePSFCUSUM.getDerivativeXYSeries()
					.toXYSeries("PSF-CUSUM Derivation");
			XYSeriesCollection dataset = new XYSeriesCollection();
			dataset.addSeries(derivativeXYSeries);

			updateDataset(dataset);
			updateTarget();	
		} else if (mode == SIMULATED_MODE) {
			MNDischargeRates dataSet2 = SOEASYController.getInstance().getDataSet();
			CuSum simulatedPSFCUSUM = dataSet2.getSimulatedPSFCUSUM();
			XYSeries dischargeRates = simulatedPSFCUSUM.getDerivativeXYSeries()
					.toXYSeries("");
			XYSeriesCollection dataset = new XYSeriesCollection();
			dataset.addSeries(dischargeRates);

			updateDataset(dataset);				
		} else if (mode == COMBINED_MODE) {
			MNDischargeRates dataSet2 = SOEASYController.getInstance().getDataSet();
			CuSum referencePSFCUSUM = dataSet2.getReferencePSFCUSUM();			

			XYSeries derivativeXYSeries = referencePSFCUSUM.getDerivativeXYSeries()
					.toXYSeries("PSF-CUSUM Derivation");
			XYSeriesCollection dataset = new XYSeriesCollection();
			dataset.addSeries(derivativeXYSeries);

			if (SOEASYController.getInstance().isSimulationRunning()) {
				CuSum simulatedPSFCUSUM = dataSet2.getSimulatedPSFCUSUM();
				XYSeries dischargeRates = simulatedPSFCUSUM.getDerivativeXYSeries()
						.toXYSeries("");
				dataset.addSeries(dischargeRates);
			}

			updateDataset(dataset);
		}

	}

}
