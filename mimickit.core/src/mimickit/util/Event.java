package mimickit.util;

public class Event {
	
	public static final String EXCITATORY = "EXCITATORY";
	public static final String INHIBITORY = "INHIBITORY";
	
	private double latency;
	private double duration;
	private String type;
	
	public Event(String type, double latency, double duration) {
		this.type = type;
		this.latency = latency;
		this.duration = duration;
	}

	public double getLatency() {
		return latency;
	}
	
	public double getDuration() {
		return duration;
	}

	public String getType() {
		return type;
	}
	
	

}
