package mimickit.util;

import java.util.List;

import org.jfree.data.Range;
import org.jfree.data.xy.XYDataItem;

public class Correlation {
	public static double getPearsonCorrelation(double[] scores1,
			double[] scores2) {
		double result = 0;
		double sum_sq_x = 0;
		double sum_sq_y = 0;
		double sum_coproduct = 0;
		double mean_x = scores1[0];
		double mean_y = scores2[0];
		for (int i = 2; i < scores1.length + 1; i += 1) {
			double sweep = Double.valueOf(i - 1) / i;
			double delta_x = scores1[i - 1] - mean_x;
			double delta_y = scores2[i - 1] - mean_y;
			sum_sq_x += delta_x * delta_x * sweep;
			sum_sq_y += delta_y * delta_y * sweep;
			sum_coproduct += delta_x * delta_y * sweep;
			mean_x += delta_x / i;
			mean_y += delta_y / i;
		}
		double pop_sd_x = (double) Math.sqrt(sum_sq_x / scores1.length);
		double pop_sd_y = (double) Math.sqrt(sum_sq_y / scores1.length);
		double cov_x_y = sum_coproduct / scores1.length;
		result = cov_x_y / (pop_sd_x * pop_sd_y);
		return result;
	}

	public static double getPearsonCorrelation(CuSum cusum1, CuSum cusum2,
			double from, double to) {
		DoubleArrayList cusumValues1 = new DoubleArrayList();
		DoubleArrayList cusumValues2 = new DoubleArrayList();
		for (double time = from; time <= to; time += 0.5) {
			double rCusum = cusum1.getCuSumAt(time);
			double sCusum = cusum2.getCuSumAt(time);
			boolean b1 = Double.isNaN(rCusum);
			boolean b2 = Double.isNaN(sCusum);
			if (!b1 && !b2) {
				cusumValues1.add(rCusum);
				cusumValues2.add(sCusum);
			}
		}
		double pearsonCorrelation = Correlation.getPearsonCorrelation(
				cusumValues1.elements(), cusumValues2.elements());
		return pearsonCorrelation;
	}

	public static double getPearsonCorrelation(CuSum cusum1, CuSum cusum2,
			Range range) {
		return getPearsonCorrelation(cusum1, cusum2, range.getLowerBound(),
				range.getUpperBound());
	}

	public static double getPearsonCorrelation(DigitalizedXYSeries series1,
			DigitalizedXYSeries series2, double from, double to) {
		DoubleArrayList cusumValues1 = new DoubleArrayList();
		DoubleArrayList cusumValues2 = new DoubleArrayList();
		for (double time = from; time <= to; time += 0.5) {
			List<XYDataItem> items1 = series1.getItemsAt(time);
			List<XYDataItem> items2 = series2.getItemsAt(time);
			if ((!items1.isEmpty()) && (!items2.isEmpty())) {
				cusumValues1.add(items1.get(0).getYValue());
				cusumValues2.add(items2.get(0).getYValue());
			}
		}
		double pearsonCorrelation = Correlation.getPearsonCorrelation(
				cusumValues1.elements(), cusumValues2.elements());
		return pearsonCorrelation;
	}
}