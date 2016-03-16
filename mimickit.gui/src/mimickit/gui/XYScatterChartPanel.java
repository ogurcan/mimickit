package mimickit.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.border.LineBorder;

import mimickit.model.SOEASYParameters;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

/**
 * ChartPanel desgined for SO-EASY.
 * 
 * @author Gurcan
 * 
 */
public class XYScatterChartPanel extends org.jfree.chart.ChartPanel {

	private static final long serialVersionUID = 1L;

	private Font titleFont = new Font("Tahoma", Font.BOLD, 15);

	private Shape shape = new Ellipse2D.Float(0.0f, 0.0f, 2.0f, 2.0f);

	private boolean isUpdateTarget = true;

	public XYScatterChartPanel(JFrame parent, String title, String xAxisLabel,
			String yAxisLabel, boolean legend) {
		super(ChartFactory.createScatterPlot(title, xAxisLabel, yAxisLabel,
				null, PlotOrientation.VERTICAL, legend, false, false));

		setBorder(new LineBorder(Color.LIGHT_GRAY));

		getChart().getTitle().setFont(titleFont);
		getChart().setBackgroundPaint(parent.getBackground());

		XYPlot xyPlot = (XYPlot) getChart().getPlot();
		xyPlot.setBackgroundPaint(parent.getBackground());
		xyPlot.setOutlinePaint(null);

		xyPlot.getRenderer().setSeriesShape(0, shape);
		xyPlot.getRenderer().setSeriesShape(1, shape);

		updateTarget();
	}

	public void updateTarget() {
		if (isUpdateTarget) {
			double pathwayBeginning = SOEASYParameters.getInstance()
					.getParameter(SOEASYParameters.PATHWAY_BEGINNING);
			double pathwayEnd = SOEASYParameters.getInstance().getParameter(
					SOEASYParameters.PATHWAY_END);
			final IntervalMarker target = new IntervalMarker(pathwayBeginning,
					pathwayEnd);
			target.setLabel("Training Range");
			target.setLabelFont(new Font("SansSerif", Font.ITALIC, 11));
			target.setLabelAnchor(RectangleAnchor.TOP);
			target.setLabelTextAnchor(TextAnchor.TOP_CENTER);
			target.setPaint(new Color(222, 222, 255, 128));

			XYPlot xyPlot = (XYPlot) getChart().getPlot();
			xyPlot.clearDomainMarkers();
			xyPlot.addDomainMarker(target, Layer.BACKGROUND);
		} else {
			XYPlot xyPlot = (XYPlot) getChart().getPlot();
			xyPlot.clearDomainMarkers();
		}
	}

	public void updateDataset(XYDataset dataset) {
		XYPlot xyPlot = (XYPlot) getChart().getPlot();

		xyPlot.setDataset(dataset);

		updateTarget();
	}

	public void setSeriesColor(int series, Paint paint) {
		XYPlot plot = (XYPlot) getChart().getPlot();
		plot.getRenderer().setSeriesPaint(series, paint);
	}

	public void saveChartAsPNG(File file, int width, int height)
			throws IOException {
		JFreeChart chart = getChart();
		chart.setBackgroundPaint(Color.WHITE);
		chart.getPlot().setBackgroundPaint(Color.WHITE);
		isUpdateTarget = false;
		updateTarget();
		ChartUtilities.saveChartAsPNG(file, chart, 613, 395);
		isUpdateTarget = true;
	}
	
	public Range getRangeAxisRange() {
		XYPlot plot = (XYPlot) getChart().getPlot();
		return plot.getRangeAxis().getRange();
	}
	
	public void setRangeAxisRange(Range range) {
		XYPlot plot = (XYPlot) getChart().getPlot();
		plot.getRangeAxis().setRange(range);
	}
	
	public void setAutoRangeDomainAxis(boolean auto) {
		XYPlot plot = (XYPlot) getChart().getPlot();
		plot.getDomainAxis().setAutoRange(auto);
	}
	
	public void configureDomainAxisRange(double lower, double upper) {
		XYPlot plot = (XYPlot) getChart().getPlot();
		plot.getDomainAxis().setRange(lower, upper);
		plot.configureRangeAxes();
	}

}
