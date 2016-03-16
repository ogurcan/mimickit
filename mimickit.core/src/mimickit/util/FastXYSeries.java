package mimickit.util;

import java.util.List;

import org.jfree.data.xy.XYSeries;

public class FastXYSeries extends XYSeries {

	private static final long serialVersionUID = -1694400249345012125L;

	public FastXYSeries(Comparable<?> key) {
		super(key);
	}

	/**
	 * Sets a list of data in a single go
	 */
	public void setData(List<?> data) {
		this.data = data;	
	}

}
