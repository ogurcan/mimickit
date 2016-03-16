package test.soeasy.util;

import static org.junit.Assert.assertEquals;
import mimickit.util.FixedISIGenerator;
import mimickit.util.ISIGenerator;

import org.junit.Test;

public class FixedISIGeneratorTest {

	@Test
	public void testNextInterspikeInterval() {
		double fixedISI = 100.0;
		ISIGenerator isiGenerator = new FixedISIGenerator(fixedISI);
		for (int i = 0; i < 1000; i++) {
			double nextISI = isiGenerator.nextInterspikeInterval();
			assertEquals(fixedISI, nextISI, 0.0);
		}
	}

}
