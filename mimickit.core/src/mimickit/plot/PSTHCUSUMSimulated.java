package mimickit.plot;

import mimickit.util.CuSum;

/**
 * Generates and plots spike histogram of experimental data.
 * 
 * @author Önder Gürcan
 * 
 */
public class PSTHCUSUMSimulated {

	public static void main(String[] args) {
		CuSum cuSum = new CuSum();
		cuSum.readFile("./data/simulated/cusum.txt");
		cuSum.getXYChart().view(800, 500);
	}
}
