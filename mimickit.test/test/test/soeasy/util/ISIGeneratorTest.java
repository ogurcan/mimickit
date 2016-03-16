package test.soeasy.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;

import mimickit.util.DataFile;
import mimickit.util.ISIGenerator;
import mimickit.util.RandomVariateGenFactory;

import org.junit.Test;

import umontreal.iro.lecuyer.gof.GofFormat;
import umontreal.iro.lecuyer.probdist.ContinuousDistribution;
import umontreal.iro.lecuyer.probdist.GammaDist;
import umontreal.iro.lecuyer.probdist.LognormalDist;
import umontreal.iro.lecuyer.probdist.WeibullDist;
import umontreal.iro.lecuyer.randvar.LognormalGen;
import umontreal.iro.lecuyer.randvar.RandomVariateGen;
import cern.colt.list.DoubleArrayList;

public class ISIGeneratorTest {

	private String IMPROPER_NUMBER_FORMAT_MOTOR_UNIT_ONLY_FILE = "./data/test/motoneuron_only_improper_number_format.txt";

	@Test
	public void initializeImproperly() {
		// try to initialize with an invalid file name
		try {
			new ISIGenerator("an invalid file name");
			fail("Not sensitive to file names.");
		} catch (Exception e) {
			assertTrue(e instanceof FileNotFoundException);
		}

		// try to initialize with an invalid file name
		try {
			new ISIGenerator(IMPROPER_NUMBER_FORMAT_MOTOR_UNIT_ONLY_FILE);
			fail("Not sensitive to improper number formats.");
		} catch (Exception e) {
			assertTrue(e instanceof NumberFormatException);
		}
	}

	@Test
	public void initializeProperly() {
		// initialize with a proper file
		try {
			ISIGenerator isiDistribution = new ISIGenerator(
					DataFile.TEST_MOTOR_ACTIVITY);
			assertNotNull(isiDistribution.getIsiArray());
			assertEquals(1316, isiDistribution.getIsiArray().size());
		} catch (Exception e) {
			e.printStackTrace();
			fail("No exception was expected! " + e.getMessage());
		}
	}

	@Test
	public void getNextInterspikeInterval() throws IOException {
		try {
			ISIGenerator isiDistribution = new ISIGenerator("");
			isiDistribution.nextInterspikeInterval();
			fail("An exception was expected.");
		} catch (Exception e) {
			//
		}
	}

	/**
	 * Not finished unfortunately yet.
	 * 
	 * @throws IOException
	 * @throws InitializationException
	 */
	@Test
	public void kolmogorovSmirnovTest() throws IOException {
		ISIGenerator isiDistribution = new ISIGenerator(
				DataFile.TEST_MOTOR_ACTIVITY);

		double[] dataArray = isiDistribution.getIsiArray().elements();
		DoubleArrayList dataArrayList = isiDistribution.getIsiArrayList();
		double[] mle;
		double alpha, lambda;

		// try for Gamma
		mle = GammaDist.getMLE(dataArray, dataArray.length);
		alpha = mle[0];
		lambda = mle[1];
		ContinuousDistribution dist = new GammaDist(alpha, lambda);
		String res = GofFormat.formatKS(dataArrayList, dist);
		double pValueGamma = RandomVariateGenFactory.pValue(res);

		// try for LogNormal
		mle = LognormalDist.getMLE(dataArray, dataArray.length);
		alpha = mle[0];
		lambda = mle[1];
		dist = new LognormalDist(alpha, lambda);
		res = GofFormat.formatKS(dataArrayList, dist);
		double pValueLognormal = RandomVariateGenFactory.pValue(res);
		//

		// try for Weibull
		mle = WeibullDist.getMLE(dataArray, dataArray.length);
		alpha = mle[0];
		lambda = mle[1];
		double delta = mle[2];
		dist = new WeibullDist(alpha, lambda, delta);
		res = GofFormat.formatKS(dataArrayList, dist);
		double pValueWeibull = RandomVariateGenFactory.pValue(res);

		// Lognormal should have the highest p-value
		assertTrue(pValueLognormal > pValueGamma);
		assertTrue(pValueLognormal > pValueWeibull);

		// So, the should be an instance of LognornalGen
		RandomVariateGen randomVariateGen = RandomVariateGenFactory
				.getGenerator(dataArray);
		assertTrue(randomVariateGen instanceof LognormalGen);
	}

}
