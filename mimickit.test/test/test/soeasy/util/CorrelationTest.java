package test.soeasy.util;

import static org.junit.Assert.*;
import mimickit.util.Correlation;
import mimickit.util.CuSum;
import mimickit.util.DataFile;
import mimickit.util.StaticPSF;

import org.junit.Test;

public class CorrelationTest {

	@Test
	public void testCusumCorrelation() {
		StaticPSF psf = new StaticPSF(
				DataFile.EXP_SOLEUS_TRIGGER, DataFile.EXP_SOLEUS_MNDISCHARGE, false);
		CuSum cusum = new CuSum(psf, 1, 4.0);
		
		double pearsonCorrelation = Correlation.getPearsonCorrelation(cusum, cusum, 0.0, 200.0);
		assertEquals(1.0, pearsonCorrelation, 0.001);
		
		pearsonCorrelation = Correlation.getPearsonCorrelation(cusum, cusum, -400.0, 200.0);
		assertEquals(1.0, pearsonCorrelation, 0.001);
	}

}
