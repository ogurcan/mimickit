package test.soeasy.util;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import mimickit.util.DataFile;
import mimickit.util.PeriStimulusTimeHistogram;
import mimickit.util.Reflex;

import org.junit.Test;

public class SpikeHistogramTest {
	
	@Test
	public void generateSpikeHistogram() {
		PeriStimulusTimeHistogram spikeHistogram = new PeriStimulusTimeHistogram(DataFile.TEST_STIMULI, DataFile.TEST_MOTOR_RESPONSE);
		// instantiate a histogram with default settings
		//Histogram spikeHistogram = dataAnalyzer.generateSpikeHistogram(TRIGGER_FILE, MOTOR_UNIT_RESPONSE_FILE);
		assertNotNull(spikeHistogram);
		
		// default numOfBins is 600, lower offset is -400 and upper offset is 200.
		assertEquals(600, spikeHistogram.getBins().size());
		assertEquals(200, spikeHistogram.getOffsetRange().getUpperBound(), 0.01);
		assertEquals(-400, spikeHistogram.getOffsetRange().getLowerBound(), 0.01);
		//		
		assertEquals(22, spikeHistogram.getMax());
		assertEquals(0, spikeHistogram.getMin());		
	}
	
	@Test
	public void getHReflex() {
		PeriStimulusTimeHistogram spikeHistogram = new PeriStimulusTimeHistogram(DataFile.TEST_STIMULI, DataFile.TEST_MOTOR_RESPONSE);
		Reflex hReflex = spikeHistogram.getHReflex();
		assertNotNull(hReflex);
		//
		double latency = hReflex.getLatency();
		assertEquals(37.0, latency);
		
		double duration = hReflex.getDuration();
		assertEquals(1.0, duration);
	}
}
