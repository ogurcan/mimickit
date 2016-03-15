package mimickit.plot;

import mimickit.util.CuSum;
import mimickit.util.DataFile;
import mimickit.util.PeriStimulusTimeHistogram;

/**
 * Generates and plots spike histogram of experimental data.
 * 
 * @author Önder Gürcan
 * 
 */
public class PSTHExperimental {

	public static void main(String[] args) {		
		PeriStimulusTimeHistogram psth = new PeriStimulusTimeHistogram(
				DataFile.EXP_SOLEUS_TRIGGER, DataFile.EXP_SOLEUS_MNDISCHARGE);
		psth.getHistogram().view(800, 500);
		
		CuSum cuSum = new CuSum(psth, 1, 4.0);				
		cuSum.getXYChart().view(800, 500);
	}
}
