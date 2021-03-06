package mimickit.plot;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import umontreal.iro.lecuyer.charts.XYLineChart;

public class UnitarySynapticPotential {

	public static void main(String[] args) {
		XYSeries xySeries = new XYSeries("");
		xySeries.add(0.085, 5);
		xySeries.add(0.15, 12);
		xySeries.add(0.25, 5);
		xySeries.add(0.35, 4);
		xySeries.add(0.45, 3);
		xySeries.add(0.55, 2);

		XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
		xySeriesCollection.addSeries(xySeries);

		XYLineChart xyChart = new XYLineChart("Satisfaction Function for Synaptic Potential", "Synaptic Potential (mV)", "Occurence Density",
				xySeriesCollection);

		double[] bounds = { 0.06, 0.60, 0, 13 };
		xyChart.setManualRange(bounds);
		
		xyChart.view(800, 500);
	}

}
