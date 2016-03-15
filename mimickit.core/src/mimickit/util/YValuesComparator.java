package mimickit.util;

import java.util.Comparator;

import org.jfree.data.xy.XYDataItem;

public class YValuesComparator implements Comparator<XYDataItem> {

	@Override
	public int compare(XYDataItem arg0, XYDataItem arg1) {
		double y0 = arg0.getYValue();
		double y1 = arg1.getYValue();
		if (y1 == y0) {
			return 0;
		} else if (y0 > y1) {
			return Integer.MAX_VALUE;
		} else {
			return Integer.MIN_VALUE;
		}
	}

}
