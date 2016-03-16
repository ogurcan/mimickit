package mimickit.gui;

import javax.swing.JFrame;

import mimickit.SOEASYController;

import org.jfree.data.category.CategoryDataset;

public class ViewersLastFeedbacksForMN extends CategoryScatterChartPanel {

	private static final long serialVersionUID = -4906888100859045101L;

	public ViewersLastFeedbacksForMN(JFrame parent) {
		super(parent, "Veiwer's Last Feedbacks for Motoneuron",
				"peristimulus time (ms)", "feedback type", false);
	}
	
	public void update() {
		CategoryDataset dataset = SOEASYController.getInstance()
				.getViewersLastFeedbacks();

		updateDataset(dataset);
	}

}
