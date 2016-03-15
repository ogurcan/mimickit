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
public class PSFExperimental {

	public static void main(String[] args) {
		StaticPSF psf = new StaticPSF(
				DataFile.EXP_SOLEUS_TRIGGER, DataFile.EXP_SOLEUS_MNDISCHARGE, false);

		psf.getDischargeRatesXYChart(-100, 300).view(800, 500);			
		//		
		new CuSum(psf, 1, 4.0).getXYChart(new Range(-100, 300)).view(800, 500);		
	}
}