package test.soeasy.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import mimickit.util.DoubleArrayList;

import org.junit.Test;

public class DoubleArrayListTest {

	@Test
	public void subListByValues() {
		double[] values = { 1.0, 1.2, 2.1, 3.0, 4.6, 5.8, 6.9 };
		DoubleArrayList list = new DoubleArrayList(values);
		// exact boundaries
		double[] expecteds = new double[] { 1.0, 1.2, 2.1, 3.0 };
		double[] actuals = list.subList(1.0, 3.0).elements();
		assertDoubleArrayEquals(expecteds, actuals);

		// exact begin value
		expecteds = new double[] { 1.0, 1.2, 2.1 };
		actuals = list.subList(1.0, 2.5).elements();
		assertDoubleArrayEquals(expecteds, actuals);

		// exact end value
		expecteds = new double[] { 2.1, 3.0, 4.6, 5.8 };
		actuals = list.subList(1.3, 5.8).elements();
		assertDoubleArrayEquals(expecteds, actuals);

		// no exact value
		expecteds = new double[] { 3.0, 4.6 };
		actuals = list.subList(2.15, 4.7).elements();
		assertDoubleArrayEquals(expecteds, actuals);

		// no exact value
		expecteds = new double[] { 1.0, 1.2, 2.1 };
		actuals = list.subList(0.5, 2.4).elements();
		assertDoubleArrayEquals(expecteds, actuals);

		// no exact value
		expecteds = new double[] { 4.6, 5.8, 6.9 };
		actuals = list.subList(3.5, 7.0).elements();
		assertDoubleArrayEquals(expecteds, actuals);
	}

	private void assertDoubleArrayEquals(double[] expecteds, double[] actuals) {
		assertEquals(expecteds.length, actuals.length);
		for (int i = 0; i < expecteds.length; i++) {
			assertEquals(expecteds[i], actuals[i], 0.0);
		}
	}

	@Test
	public void binarySearchNearest() {
		DoubleArrayList list = new DoubleArrayList();
		for (int value = 0; value < 5000; value += 10) {
			list.add(value);
		}
		
		int foundIndex = list.binarySearchNearest(132);
		assertTrue((foundIndex == 13)||(foundIndex == 14));
		
		foundIndex = list.binarySearchNearest(145);
		assertTrue((foundIndex == 14)||(foundIndex == 15));
	}

}
