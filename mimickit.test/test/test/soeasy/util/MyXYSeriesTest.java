package test.soeasy.util;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import mimickit.util.DigitalizedXYSeries;

import org.jfree.data.Range;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.junit.Test;

public class MyXYSeriesTest {

	@Test
	public void create() {
		// create an xyseries from 0.0 to 10.0 step by 1.0
		DigitalizedXYSeries myXYSeries = new DigitalizedXYSeries(new Range(0.0, 10.0), 1.0);

		assertEquals(0.0, myXYSeries.getXValueRange().getLowerBound(), 0.0);
		assertEquals(10.0, myXYSeries.getXValueRange().getUpperBound(), 0.0);
		assertEquals(1.0, myXYSeries.getStepSize(), 0.0);

		assertEquals(0, myXYSeries.size());
		List<XYDataItem> allItems = myXYSeries.getItems();
		assertNotNull(allItems);
		assertEquals(0, allItems.size());

		List<XYDataItem> values = myXYSeries.getItemsAt(0.0);
		assertNotNull(values);
		assertEquals(0, values.size());

		XYSeries xySeries = myXYSeries.toXYSeries("Creation Test");
		assertNotNull(xySeries);
		assertEquals(0, xySeries.getItemCount());
	}

	@Test
	public void getItemsAt() {
		DigitalizedXYSeries myXYSeries = new DigitalizedXYSeries(new Range(0.0, 10.0), 1.0);
		myXYSeries.add(1.0, 3.4);
		myXYSeries.add(2.0, 1.5);
		myXYSeries.add(1.0, 3.2);
		myXYSeries.add(1.0, 4.9);

		assertEquals(4, myXYSeries.size());

		List<XYDataItem> items = myXYSeries.getItemsAt(1.0);
		assertNotNull(items);
		assertEquals(3, items.size());

		Iterator<XYDataItem> iterator = items.iterator();
		while (iterator.hasNext()) {
			XYDataItem dataItem = iterator.next();
			assertEquals(1.0, dataItem.getXValue(), 0.0);
		}

		items = myXYSeries.getItemsAt(2.0);
		assertNotNull(items);
		assertEquals(1, items.size());

		iterator = items.iterator();
		while (iterator.hasNext()) {
			XYDataItem dataItem = iterator.next();
			assertEquals(2.0, dataItem.getXValue(), 0.0);
		}

		items = myXYSeries.getItemsAt(3.5);
		assertNotNull(items);
		assertEquals(0, items.size());
	}

	@Test
	public void getSortedItemsAt() {
		DigitalizedXYSeries myXYSeries = new DigitalizedXYSeries(new Range(0.0, 10.0), 1.0);
		myXYSeries.add(1.0, 3.4);
		myXYSeries.add(2.0, 1.5);
		myXYSeries.add(1.0, 3.2);
		myXYSeries.add(1.0, 4.9);

		assertEquals(4, myXYSeries.size());

		List<XYDataItem> items = myXYSeries.getSortedItemsAt(1.0);
		assertNotNull(items);
		assertEquals(3, items.size());

		assertEquals(3.2, items.get(0).getYValue(), 0.0);
		assertEquals(3.4, items.get(1).getYValue(), 0.0);
		assertEquals(4.9, items.get(2).getYValue(), 0.0);
	}

	@Test
	public void add() {
		DigitalizedXYSeries myXYSeries = new DigitalizedXYSeries(new Range(0.0, 10.0), 1.0);

		myXYSeries.add(1.0, 5.0);
		assertEquals(1, myXYSeries.size());

		List<XYDataItem> items = myXYSeries.getItemsAt(1.0);
		assertEquals(1, items.size());
		XYDataItem xyDataItem = items.get(0);
		assertEquals(1.0, xyDataItem.getXValue(), 0.0);
		assertEquals(5.0, xyDataItem.getYValue(), 0.0);

		myXYSeries.add(1.0, 7.0);
		assertEquals(2, myXYSeries.size());

		items = myXYSeries.getItemsAt(1.0);
		assertEquals(2, items.size());
		xyDataItem = items.get(1);
		assertEquals(1.0, xyDataItem.getXValue(), 0.0);
		assertEquals(7.0, xyDataItem.getYValue(), 0.0);

		myXYSeries.add(2.0, 7.0);
		assertEquals(3, myXYSeries.size());

		items = myXYSeries.getItemsAt(1.0);
		assertEquals(2, items.size());
		items = myXYSeries.getItemsAt(2.0);
		assertEquals(1, items.size());
		xyDataItem = items.get(0);
		assertEquals(2.0, xyDataItem.getXValue(), 0.0);
		assertEquals(7.0, xyDataItem.getYValue(), 0.0);
	}

	@Test
	public void dontAdd() {
		DigitalizedXYSeries myXYSeries = new DigitalizedXYSeries(new Range(0.0, 10.0), 1.0);

		try {
			myXYSeries.add(-1.0, 5.0);
			fail("This line should not be processed.");
		} catch (IndexOutOfBoundsException e) {
			assertEquals("The xValue -1.0 is out of the ranges.",
					e.getMessage());
		}
	}

	@Test
	public void getItemsBetween() {
		DigitalizedXYSeries myXYSeries = new DigitalizedXYSeries(new Range(0.0, 10.0), 1.0);
		myXYSeries.add(1.0, 3.6);
		myXYSeries.add(1.0, 4.5);
		myXYSeries.add(2.5, 1.7);
		myXYSeries.add(5.0, 1.6);

		assertEquals(4, myXYSeries.size());

		List<XYDataItem> itemsBetween = myXYSeries.getItemsBetween(1.0, 3.0);
		assertEquals(3, itemsBetween.size());

		Iterator<XYDataItem> iterator = itemsBetween.iterator();
		while (iterator.hasNext()) {
			XYDataItem dataItem = iterator.next();
			assertTrue(dataItem.getXValue() >= 1.0);
			assertTrue(dataItem.getXValue() <= 3.0);
		}
	}

	@Test
	public void getAverageYValueBetween() {
		DigitalizedXYSeries myXYSeries = new DigitalizedXYSeries(new Range(0.0, 10.0), 1.0);
		myXYSeries.add(1.0, 3.6);
		myXYSeries.add(1.0, 4.5);
		myXYSeries.add(2.5, 1.7);
		myXYSeries.add(5.0, 1.6);

		double average = myXYSeries.getAverageYValueBetween(1.0, 3.0);
		assertEquals(3.266, average, 0.001);

		average = myXYSeries.getAverageYValueBetween(3.0, 4.0);
		assertTrue(Double.isNaN(average));
	}
}
