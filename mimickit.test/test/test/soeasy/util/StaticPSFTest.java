package test.soeasy.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Iterator;
import java.util.List;

import mimickit.util.DataFile;
import mimickit.util.DigitalizedXYSeries;
import mimickit.util.DoubleArrayList;
import mimickit.util.StaticPSF;

import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.junit.Test;

public class StaticPSFTest {

	@Test
	public void staticPSFCreation() {
		StaticPSF psf = new StaticPSF(DataFile.EXP_SOLEUS_TRIGGER,
				DataFile.EXP_SOLEUS_MNDISCHARGE, false);

		XYSeries xySeries = psf.getDischargeRates().toXYSeries("");
		List<?> items = xySeries.getItems();
		Iterator<?> iterator = items.iterator();
		// xValues (time) should be like 10.0, 10.5, 11.0, 11.5 etc.
		while (iterator.hasNext()) {
			XYDataItem xyDataItem = (XYDataItem) iterator.next();
			double xValue = xyDataItem.getXValue();
			assertEquals(0, (xValue * 10) % 5, 0.0);
			assertEquals(0, xValue % 0.5, 0.0);
		}
	}

	@Test
	public void predictionInterval() {
		StaticPSF psf = new StaticPSF(DataFile.EXP_SOLEUS_TRIGGER,
				DataFile.EXP_SOLEUS_MNDISCHARGE, false);

		double latency = 45.0;
		double[] predictionInderval = psf.getDataInterval(latency);
		assertNull(predictionInderval);

		latency = 38.5; // 6.569438969913175 - 7.415097137766872
		predictionInderval = psf.getDataInterval(latency);
		assertNotNull(predictionInderval);
		assertEquals(6.569438969913175, predictionInderval[0], 0.0);
		assertEquals(7.415097137766872, predictionInderval[1], 0.0);
	}

	@Test
	public void dischargeRates() {
		DoubleArrayList triggers = new DoubleArrayList();
		triggers.add(1000.0);

		DoubleArrayList mnDischarges = new DoubleArrayList();
		mnDischarges.add(94.5);
		mnDischarges.add(227.0);
		mnDischarges.add(312.5);
		mnDischarges.add(410.0);
		mnDischarges.add(510.0);
		mnDischarges.add(609.5);
		mnDischarges.add(764.5); // isi= -235.5

		// loweroffset is -200.0

		mnDischarges.add(847.5); // isi= -152.5,
								 // rate = (1000 / (-152.5 - (-235.5))) = 12.0481
		mnDischarges.add(950.5); // isi= -49.5
								 // rate = (1000 / (-49.5 - (-152.5))) = 9.7087
		mnDischarges.add(1039.0); // isi = 39.0
		mnDischarges.add(1133.0); // isi = 133.0

		StaticPSF psf = new StaticPSF(triggers, mnDischarges, true);

		DigitalizedXYSeries dischargeRates = psf.getDischargeRates();

		assertEquals(4, dischargeRates.size());

		List<XYDataItem> items = dischargeRates.getItemsAt(-152.5);
		assertEquals(1, items.size());
		assertEquals(-152.5, items.get(0).getXValue(), 0.0);
		assertEquals(12.0481, items.get(0).getYValue(), 0.001);
		
		items = dischargeRates.getItemsAt(-49.5);
		assertEquals(1, items.size());
		assertEquals(-49.5, items.get(0).getXValue(), 0.0);
		assertEquals(9.7087, items.get(0).getYValue(), 0.001);
		
		items = dischargeRates.getItemsAt(39.0);
		assertEquals(1, items.size());
		assertEquals(39.0, items.get(0).getXValue(), 0.0);
		assertEquals(11.2994, items.get(0).getYValue(), 0.001);
		
		items = dischargeRates.getItemsAt(133.0);
		assertEquals(1, items.size());
		assertEquals(133.0, items.get(0).getXValue(), 0.0);
		assertEquals(10.6382, items.get(0).getYValue(), 0.001);
	}
	
	@Test
	public void averageDischargeRates() {
		DoubleArrayList triggers = new DoubleArrayList();
		triggers.add(1000.0);

		DoubleArrayList mnDischarges = new DoubleArrayList();
		mnDischarges.add(94.5);
		mnDischarges.add(227.0);
		mnDischarges.add(312.5);
		mnDischarges.add(410.0);
		mnDischarges.add(510.0);
		mnDischarges.add(609.5);
		mnDischarges.add(764.5); // isi= -235.5

		// loweroffset is -200.0

		mnDischarges.add(847.5); // isi= -152.5,
								 // rate = (1000 / (-152.5 - (-235.5))) = 12.0481
		mnDischarges.add(950.5); // isi= -49.5
								 // rate = (1000 / (-49.5 - (-152.5))) = 9.7087
		mnDischarges.add(1039.0); // isi = 39.0
		mnDischarges.add(1133.0); // isi = 133.0

		StaticPSF psf = new StaticPSF(triggers, mnDischarges, true);

		DigitalizedXYSeries averageDischargeRates = psf.getAvarageDischargeRates();
		
		assertEquals(4*15, averageDischargeRates.size());

		List<XYDataItem> items = averageDischargeRates.getItemsAt(-152.5);
		assertEquals(1, items.size());
		assertEquals(-152.5, items.get(0).getXValue(), 0.0);
		assertEquals(12.0481, items.get(0).getYValue(), 0.001);
		
		items = averageDischargeRates.getItemsAt(-49.5);
		assertEquals(1, items.size());
		assertEquals(-49.5, items.get(0).getXValue(), 0.0);
		assertEquals(9.7087, items.get(0).getYValue(), 0.001);
		
		items = averageDischargeRates.getItemsAt(39.0);
		assertEquals(1, items.size());
		assertEquals(39.0, items.get(0).getXValue(), 0.0);
		assertEquals(11.2994, items.get(0).getYValue(), 0.001);
		
		items = averageDischargeRates.getItemsAt(133.0);
		assertEquals(1, items.size());
		assertEquals(133.0, items.get(0).getXValue(), 0.0);
		assertEquals(10.6382, items.get(0).getYValue(), 0.001);
	}
}
