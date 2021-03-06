package mimickit.plot;

import mimickit.util.DoubleArrayList;
import mimickit.util.ExactISIGenerator;
import mimickit.util.ISIGenerator;
import umontreal.iro.lecuyer.charts.HistogramChart;
import umontreal.iro.lecuyer.charts.HistogramSeriesCollection;

/**
 * Generates and plots spike histogram of experimental data.
 * @author Önder Gürcan
 *
 */
public class InterspikeIntervalHistogram {
	private static String MOTOR_BEHAVIOR = "./data/experimental/motoneuron_only.txt";

	private static DoubleArrayList getData() {
		ISIGenerator isiDistribution = null;
		try {
			isiDistribution = new ExactISIGenerator(MOTOR_BEHAVIOR);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isiDistribution.getIsiArray();
	}

	public static void main(String[] args) {
		DoubleArrayList data = getData();
		System.out.println("size: " + data.size());
		data.writeToFile("./data/experimental/motoneuron_only_isi.txt");

		HistogramChart chart;
		chart = new HistogramChart("Interspike Interval Histogram", "inter-spike intervals (ms)", "count", data);
		
		// Customizes the data plot
		HistogramSeriesCollection collec = chart.getSeriesCollection();
		collec.setBins(0, 200);		
		double[] bounds = { 0, 200, 0, 40 };
		chart.setManualRange(bounds);

		chart.view(800, 500);
		//chart.toLatexFile("SH_Simulated_Data.tex", 12, 8);
	}
}
