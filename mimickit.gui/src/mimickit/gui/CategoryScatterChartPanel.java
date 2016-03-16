package mimickit.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.border.LineBorder;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.ScatterRenderer;
import org.jfree.data.category.CategoryDataset;

/**
 * ChartPanel desgined for SO-EASY.
 * 
 * @author Gurcan
 * 
 */
public class CategoryScatterChartPanel extends org.jfree.chart.ChartPanel {

	private static final long serialVersionUID = 1L;

	private Font titleFont = new Font("Tahoma", Font.BOLD, 15);

	private Shape shape = new Ellipse2D.Float(-2.0f, -20.0f, 4.0f, 40.0f);

	public CategoryScatterChartPanel(JFrame parent, String title,
			String categoryAxisLabel, String valueAxisLabel, boolean legend) {
		super(new JFreeChart(title, new CategoryPlot(null, new CategoryAxis(
				valueAxisLabel), new NumberAxis(categoryAxisLabel),
				new ScatterRenderer())));		

		setBorder(new LineBorder(Color.LIGHT_GRAY));

		getChart().getTitle().setFont(titleFont);
		getChart().setBackgroundPaint(parent.getBackground());

		getChart().getPlot().setBackgroundPaint(parent.getBackground());
		getChart().getPlot().setOutlinePaint(null);		
		CategoryPlot categoryPlot = (CategoryPlot) getChart().getPlot();
		categoryPlot.setOrientation(PlotOrientation.HORIZONTAL);
		categoryPlot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
		categoryPlot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT);					
		categoryPlot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_90);	
		categoryPlot.getDomainAxis().setMaximumCategoryLabelLines(2);
		categoryPlot.getDomainAxis().setMaximumCategoryLabelLines(2); 

		categoryPlot.getRenderer().setSeriesShape(0, shape);
		categoryPlot.getRenderer().setBaseSeriesVisibleInLegend(false);
	}

	public void updateDataset(CategoryDataset dataset) {
		CategoryPlot categoryPlot = (CategoryPlot) getChart().getPlot();

		categoryPlot.setDataset(dataset);
	}
	
	public void saveChartAsPNG(File file, int width, int height) throws IOException {
		JFreeChart chart = getChart();	
		chart.setBackgroundPaint(Color.WHITE);
		chart.getPlot().setBackgroundPaint(Color.WHITE);
		ChartUtilities.saveChartAsPNG(file, chart, 613, 395);
	}

}
