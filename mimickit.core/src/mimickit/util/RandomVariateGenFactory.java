package mimickit.util;

import umontreal.iro.lecuyer.gof.GofFormat;
import umontreal.iro.lecuyer.probdist.ContinuousDistribution;
import umontreal.iro.lecuyer.probdist.GammaDist;
import umontreal.iro.lecuyer.probdist.LognormalDist;
import umontreal.iro.lecuyer.probdist.WeibullDist;
import umontreal.iro.lecuyer.randvar.GammaAcceptanceRejectionGen;
import umontreal.iro.lecuyer.randvar.LognormalGen;
import umontreal.iro.lecuyer.randvar.RandomVariateGen;
import umontreal.iro.lecuyer.randvar.WeibullGen;
import umontreal.iro.lecuyer.rng.MRG32k3a;

public class RandomVariateGenFactory {
	
	public static RandomVariateGen getGenerator(DoubleArrayList dataVector) {		
		return getGenerator(dataVector.elements());
	}

	public static RandomVariateGen getGenerator(double[] dataArray) {
		RandomVariateGen randomVariateGen = null;
		//
		DoubleArrayList dataArrayList = new DoubleArrayList(dataArray);
		
		// try for Gamma
		double[] mleGamma = GammaDist.getMLE(dataArray, dataArray.length);
		ContinuousDistribution dist = new GammaDist(mleGamma[0], mleGamma[1]);		
		String res = GofFormat.formatKS(dataArrayList, dist);
		double pValueGamma = pValue(res);
		
		// try for LogNormal
		double[] mleLogNormal = LognormalDist.getMLE(dataArray, dataArray.length);
		dist = new LognormalDist(mleLogNormal[0], mleLogNormal[1]);		
		res = GofFormat.formatKS(dataArrayList, dist);
		double pValueLognormal = pValue(res);
		
		// try for Weibull
		double[] mleWeibull = WeibullDist.getMLE(dataArray, dataArray.length);
		dist = new LognormalDist(mleWeibull[0], mleWeibull[1]);		
		res = GofFormat.formatKS(dataArrayList, dist);
		double pValueWeibull = pValue(res);
		
		if ((pValueGamma > pValueLognormal) && (pValueGamma > pValueWeibull)) {
			GammaDist gammaDist = new GammaDist(mleGamma[0], mleGamma[1]);
			randomVariateGen = new GammaAcceptanceRejectionGen(new MRG32k3a(), gammaDist);
		} else if ((pValueLognormal > pValueGamma) && (pValueLognormal > pValueWeibull)) {
			LognormalDist lognormalDist = new LognormalDist(mleLogNormal[0], mleLogNormal[1]);
			randomVariateGen = new LognormalGen(new MRG32k3a(), lognormalDist);
		} else if ((pValueWeibull > pValueGamma) && (pValueWeibull > pValueLognormal)) {
			WeibullDist weibullDist = new WeibullDist(mleWeibull[0], mleWeibull[1], mleWeibull[2]);
			randomVariateGen = new WeibullGen(new MRG32k3a(), weibullDist);
		} else { // default generator is Gamma generator
			GammaDist gammaDist = new GammaDist(mleGamma[0], mleGamma[1]);
			randomVariateGen = new GammaAcceptanceRejectionGen(new MRG32k3a(), gammaDist);
		}
		
		return randomVariateGen;
	}
	
	/**
	 * Should be used after calling GofFormat.formatKS() method.
	 * @param res
	 * @return
	 */
	public static double pValue(String res) {
		int dIndex = res.indexOf("Kolmogorov-Smirnov statistic = D      :");
		res = res.substring(dIndex);
		int sIndex = res.indexOf("Significance level of test            :");
		res = res.substring(sIndex);		
		int beginIndex = res.indexOf(":");
		int endIndex = res.lastIndexOf("*****");
		if (endIndex == -1) { // no *****
			String pValueStr = res.substring(beginIndex + 1);
			return Double.parseDouble(pValueStr);
		} else {
			return -1;
		}
	}

}
