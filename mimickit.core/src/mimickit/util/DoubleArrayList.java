package mimickit.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import cern.colt.list.AbstractDoubleList;

/**
 * 
 * @author Önder Gürcan
 * 
 */
public class DoubleArrayList extends cern.colt.list.DoubleArrayList {

	private static final long serialVersionUID = 1L;

	public DoubleArrayList() {
		super();
	}

	public DoubleArrayList(double[] arg0) {
		super(arg0);
	}

	public DoubleArrayList(int arg0) {
		super(arg0);
	}

	public DoubleArrayList(String fileName) {
		super();
		readFromFile(fileName);
	}

	private void readFromFile(String fileName) {
		try {
			// fill in trigger vector
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				// convert to milliseconds
				double lineInSeconds = Double.parseDouble(line);
				double lineInMilliseconds = lineInSeconds * 1000;
				this.add(lineInMilliseconds);
			}
			bufferedReader.close();
		} catch (NumberFormatException e) {
			Exception nfe = new NumberFormatException(
					e.getMessage()
							+ " : "
							+ fileName
							+ " - Check the beginning of the file for headers and the end of the file for empty lines.");
			nfe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeToFile(String fileName) {
		try {
			File eventFile = new File(fileName);
			FileWriter fileWriter = new FileWriter(eventFile);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			for (int i = 0; i < this.size(); i++) {
				double signal = this.get(i);
				bufferedWriter.write("" + (signal / 1000) + "\n");
			}
			bufferedWriter.close();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public double lastElement() {
		return this.get(this.size - 1);
	}

	public DoubleArrayList subList(int beginIndex, int endIndex) {
		AbstractDoubleList partFromTo = super.partFromTo(beginIndex, endIndex);
		return new DoubleArrayList(partFromTo.elements());
	}

	/**
	 * Returns the sublist between beginValue and endValue on a sorted list. In
	 * case the list is not sorted, it should be sorted by the methods provided
	 * by this class.
	 * 
	 * @param beginValue
	 * @param endValue
	 * @return
	 */
	public DoubleArrayList subList(double beginValue, double endValue) {
		// find the begin index first
		int beginIndex = 0;
		for (int i = 0; i < this.size(); i++) {
			if (this.get(i) >= beginValue) {
				beginIndex = i;
				break;
			}
		}

		// then find the end index
		int endIndex = this.size() - 1;
		for (int i = beginIndex; i < this.size(); i++) {
			if (this.get(i) >= endValue) {
				if (this.get(i) == endValue) {
					endIndex = i;
				} else {
					endIndex = i - 1;
				}
				break;
			}
		}
		// finally get the sublist using these indexes
		return subList(beginIndex, endIndex);
	}

	public int binarySearchNearest(double searchValue) {
		int midPt = -1;
		int start = 0;
		int end = this.size() - 1;
		while (start <= end) {
			midPt = (start + end) / 2;
			if (this.get(midPt) == searchValue) {
				return midPt;
			} else if (this.get(midPt) < searchValue) {
				start = midPt + 1;
			} else {
				end = midPt - 1;
			}
		}
		return midPt;
	}

	protected DoubleArrayList getSweepVector4PSTH(double minValue,
			double maxValue, double trigger) {
		DoubleArrayList result = new DoubleArrayList();
		//
		int lowerIndex = this.binarySearchNearest(minValue);
		if (lowerIndex > 0) {
			if (get(lowerIndex) < minValue) {
				lowerIndex++;
			}
		}

		int upperIndex = this.binarySearchNearest(maxValue + 1.0);
		if (upperIndex > 0) {
			if (get(upperIndex) > maxValue) {
				upperIndex--;
			}
		}

		if (lowerIndex > 0 && upperIndex > 0) {
			for (int index = lowerIndex; index <= upperIndex; index++) {
				Double event = this.get(index);
				double latency = event - trigger;
				result.add(latency);
			}
		}
		//
		return result;
	}

	/**
	 * For rate calculate we also need the outer events.
	 * 
	 * @param minValue
	 * @param maxValue
	 * @param trigger
	 * @return
	 */
	protected DoubleArrayList getSweepVector4PSF(double minValue,
			double maxValue, double trigger) {
		DoubleArrayList result = new DoubleArrayList();
		//
		int lowerIndex = this.binarySearchNearest(minValue);
		if (lowerIndex > 0) {
			if (get(lowerIndex) >= minValue) {
				lowerIndex--;
			}
		}

		int upperIndex = this.binarySearchNearest(maxValue + 1.0);
		if ((upperIndex > 0) && (upperIndex < size() - 1)) {
			if (get(upperIndex) <= maxValue) {
				upperIndex++;
			}
		} else if (upperIndex < 0) {
			upperIndex = Integer.MIN_VALUE;
		}

		for (int index = lowerIndex; index <= upperIndex; index++) {
			try {
				Double event = this.get(index);
				double latency = event - trigger;
				result.add(latency);
			} catch (Exception e) {				
				e.printStackTrace();
			}
		}
		//
		return result;
	}
}
