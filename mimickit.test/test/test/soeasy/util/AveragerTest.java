package test.soeasy.util;

import static org.junit.Assert.*;
import mimickit.util.Averager;

import org.junit.Test;

public class AveragerTest {

	@Test
	public void create() {
		Averager averager = new Averager();
		assertEquals(0.0, averager.getAverage(), 0.0);
	}
	
	@Test
	public void updateWithDouble() {
		Averager averager = new Averager();
		assertEquals(0.0, averager.getAverage(), 0.0);
		
		averager.update(3.0);
		assertEquals(3.0, averager.getAverage(), 0.0);
		
		averager.update(5.0);
		assertEquals(4.0, averager.getAverage(), 0.0);
		
		averager.update(7.0);
		assertEquals(5.0, averager.getAverage(), 0.0);
		
		averager.update(15.0);
		assertEquals(7.5, averager.getAverage(), 0.0);
		
		averager.update(-10.0);
		assertEquals(4.0, averager.getAverage(), 0.0);
	}
	
	@Test
	public void updateWithAverager() {
		
	}

}
