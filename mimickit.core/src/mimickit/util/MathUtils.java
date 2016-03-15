package mimickit.util;

public class MathUtils {
	
	/**
	 * 104.635999 -> 104.5
	 * 
	 * @param value
	 * @return
	 */
	public static double digitalize(double value) {
		int binSize = 5;
		return ((double) Math.round(value * 10 / binSize) * binSize) / 10;
	}

}
