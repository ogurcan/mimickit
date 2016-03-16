package mimickit.gui;

import javax.swing.JFrame;

import mimickit.SOEASYController;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class CriticalityPanel extends LineChartPanel {

	private static final long serialVersionUID = -687012286034581722L;

	public CriticalityPanel(JFrame parent) {
		super(parent, "Criticality", "simulation time (ms)",
				"criticality", true);
	}

	public void update() {
		try {
			XYSeriesCollection dataset = new XYSeriesCollection();
			//
			SOEASYController controller = SOEASYController.getInstance();
			XYSeries overallCriticality = controller.getAverageCriticality();
			dataset.addSeries(overallCriticality);
			XYSeries motoneuronCriticality = controller.getMotoneuronCriticality();
			dataset.addSeries(motoneuronCriticality);
			XYSeries sensoryNeuronCriticality = controller.getSensoryNeuronCriticality();
			dataset.addSeries(sensoryNeuronCriticality);
			//
			updateDataset(dataset);
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}

}
