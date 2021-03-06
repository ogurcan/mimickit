package mimickit.plot;

import mimickit.util.DataFile;
import mimickit.util.PeriStimulusTimeHistogram;
import umontreal.iro.lecuyer.charts.HistogramChart;

/**
 * Generates and plots spike histogram of experimental data.
 * @author Önder Gürcan
 *
 */
public class PSTHSimulated {

	public static void main(String[] args) {
		HistogramChart chart;
		chart = new PeriStimulusTimeHistogram(DataFile.SIMULATED_STIMULI, DataFile.TEST_MOTOR_ACTIVITY).getHistogram();
		
		chart.view(800, 500);
		//chart.toLatexFile("SH_Simulated_Data.tex", 12, 8);		
	}
}
