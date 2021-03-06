package mimickit.model;

import repast.simphony.engine.schedule.IAction;
import repast.simphony.engine.schedule.ScheduleParameters;

/**
 * The fluid present in blood and in the spaces surrounding cells.
 * 
 * @author Önder Gürcan
 *
 */
public class ExtracellularFluid {
	
	private double chargeDuration;

	public boolean charged;

	protected ExtracellularFluid() {
		this.chargeDuration = 2.0;
		this.charged = false;
	}

	/**
	 * Charges this extracellular fluid for 2 ticks.
	 */
	public void charge() {
		// charge extracellular fluid
		this.charged = true;

		// and discharge it after 2 ms
		double currentTick = SOEASYParameters.getInstance().getCurrentTick();
		SOEASYParameters.schedule(
				ScheduleParameters.createOneTime(currentTick + chargeDuration, 0, 0),
				new IAction() {
					public void execute() {
						discharge();
					}
				});
	}

	/**
	 * Discharges this extracellular fluid.
	 */
	public void discharge() {
		this.charged = false;
	}
	
	public boolean isCharged() {
		return charged;
	}
	
	public double getChargeDuration() {
		return chargeDuration;
	}

}
