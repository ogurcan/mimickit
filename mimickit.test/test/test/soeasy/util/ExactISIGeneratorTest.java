package test.soeasy.util;

import static mimickit.util.DataFile.EXP_MOTONEURON_ONLY;
import static mimickit.util.DataFile.EXP_SOLEUS_MNDISCHARGE;
import static mimickit.util.DataFile.EXP_SOLEUS_TRIGGER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import mimickit.util.DoubleArrayList;
import mimickit.util.ExactISIGenerator;
import mimickit.util.ISIGenerator;

import org.junit.Test;

public class ExactISIGeneratorTest {

	@Test
	public void testNextISIFromOneFile() {
		try {
			ISIGenerator isiGenerator = new ExactISIGenerator(
					EXP_MOTONEURON_ONLY);
			DoubleArrayList mnDischarges = new DoubleArrayList(EXP_MOTONEURON_ONLY);
			for (int i = 1; i < mnDischarges.size(); i++) {
				double expectedISI = mnDischarges.get(i) - mnDischarges.get(i - 1);
				if ((expectedISI >= isiGenerator.getMinimumISI())
						&& (expectedISI <= isiGenerator.getMaximumISI())) {
					double actualISI = isiGenerator.nextInterspikeInterval();
					assertEquals(expectedISI, actualISI, 0.0);
				}
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testNextISIFromTwoFiles() {
		try {
			ISIGenerator isiGenerator = new ExactISIGenerator(EXP_SOLEUS_TRIGGER, EXP_SOLEUS_MNDISCHARGE);
			DoubleArrayList triggers = new DoubleArrayList(EXP_SOLEUS_TRIGGER);
			DoubleArrayList mnDischarges = new DoubleArrayList(EXP_SOLEUS_MNDISCHARGE);
			
			DoubleArrayList prestimulusISIValues = new DoubleArrayList();
			for (int i = 0; i < triggers.size(); i++) {
				double trigger = triggers.get(i);
				DoubleArrayList subList = mnDischarges.subList(trigger-400, trigger);
				DoubleArrayList subListISIValues = ISIGenerator.calculateInterspikeInterval(subList);
				prestimulusISIValues.addAllOf(subListISIValues);
			}
			
			int indexOfZero = prestimulusISIValues.indexOf(0.0);
			assertEquals(-1, indexOfZero);
						
			for (int i = 0; i < prestimulusISIValues.size(); i++) {
				double expectedISI = prestimulusISIValues.get(i);
				if ((expectedISI >= isiGenerator.getMinimumISI())
						&& (expectedISI <= isiGenerator.getMaximumISI())) {
					double actualISI = isiGenerator.nextInterspikeInterval();
					assertEquals(expectedISI, actualISI, 0.0);
				}
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}
