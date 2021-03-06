package mimickit.util;


public class FixedISIGenerator extends ISIGenerator {

	private double fixedIsiValue;

	public FixedISIGenerator(double fixedIsiValue) {
		if ((fixedIsiValue >= getMinimumISI())
				&& (fixedIsiValue <= getMaximumISI())) {
			this.fixedIsiValue = fixedIsiValue;
		} else {
			throw new RuntimeException("Bad Fixed Value");
		}
	}

	@Override
	public double nextInterspikeInterval() {
		return this.fixedIsiValue; // - Spike.AMPLITUDE;
	}

}
