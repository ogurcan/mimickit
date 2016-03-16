package mimickit.gui;

import javax.swing.JFrame;

import mimickit.SOEASYController;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class MotoneuronCriticalityPanel extends LineChartPanel {	

	private static final long serialVersionUID = 6423232657057193216L;

	public MotoneuronCriticalityPanel(JFrame parent) {
		super(parent, "Motoneuron Criticality", "simulation time (ms)",
				"criticality", true);
	}

	public void update() {
		try {
			XYSeriesCollection dataset = new XYSeriesCollection();
			//
			SOEASYController controller = SOEASYController.getInstance();
			XYSeries motoneuronCriticality = controller.getMotoneuronCriticality();
			dataset.addSeries(motoneuronCriticality);
			//
			updateDataset(dataset);
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}

}
