package mimickit.gui;

import javax.swing.JFrame;

import mimickit.SOEASYController;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class FeedbacksEvolutionPanel extends LineChartPanel {

	private static final long serialVersionUID = -687012286034581722L;

	public FeedbacksEvolutionPanel(JFrame parent) {
		super(parent, "Viewer's Feedback Evolution", "simulation time (ms)", "count", true);
	}
	
	public void update() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		//
		XYSeries excitatorySynapsesByTime = SOEASYController.getInstance()
				.getExcitatorySynapseEvolution();
		dataset.addSeries(excitatorySynapsesByTime);
		//
		XYSeries inhibitorySynapsesByTime = SOEASYController.getInstance()
				.getInhibitorySynapseEvolution();
		dataset.addSeries(inhibitorySynapsesByTime);

		updateDataset(dataset);
	}

}
