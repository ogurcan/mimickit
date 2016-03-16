package mimickit.plot;

import mimickit.util.CuSum;
import mimickit.util.DataFile;
import mimickit.util.StaticPSF;

import org.jfree.data.Range;

/**
 * Generates and plots spike histogram of experimental data.
 * 
 * @author Önder Gürcan
 * 
 */
public class PSFSimulated {

	public static void main(String[] args) {
		StaticPSF psf_raw = new StaticPSF(
				DataFile.SIMULATED_STIMULI, DataFile.SIMULATED_MOTOR_RESPONSE, false);		

		psf_raw.getDischargeRatesXYChart(-50, 200).view(800, 500);
		
		psf_raw.getAverageDischargeRatesXYChart(-50, 200).view(800, 500);

		// PeriStimulusFrequencygram filteredPSF = psf.generateFilteredPSF();
		//
		// filteredPSF.getDischargeRatesXYChart(-100, 200).view(800, 500);
		//
		// filteredPSF.getAverageDischargeRatesXYChart(-100, 200).view(800,
		// 500);
		//
		CuSum cuSum = new CuSum(psf_raw, 1, 4.0);
		cuSum.getXYChart(new Range(-50, 200)).view(800, 500);
		//
		// System.out.println(cuSum);
	}
}
