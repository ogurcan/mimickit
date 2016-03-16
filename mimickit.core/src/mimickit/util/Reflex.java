package mimickit.util;

/**
 * A reflex, is an involuntary and nearly instantaneous movement in response to
 * a stimulus.
 * 
 * @author Önder Gürcan
 * 
 */
public class Reflex {

	private double latency;
	private double duration;

	public Reflex() {
		setLatency(0);
		this.duration = 1;
	}

	public double getLatency() {
		return this.latency;
	}

	public double getDuration() {
		return this.duration;
	}

	public void setLatency(double latency) {
		this.latency = latency;
	}

}
