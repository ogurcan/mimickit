package mimickit.scenario;

import java.io.File;

import repast.simphony.batch.BatchScenarioLoader;
import repast.simphony.engine.controller.Controller;
import repast.simphony.engine.controller.DefaultController;
import repast.simphony.engine.environment.AbstractRunner;
import repast.simphony.engine.environment.ControllerRegistry;
import repast.simphony.engine.environment.DefaultRunEnvironmentBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.environment.RunEnvironmentBuilder;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.parameter.DefaultParameters;
import repast.simphony.parameter.SweeperProducer;
import simphony.util.messages.MessageCenter;

/**
 * @author Önder Gürcan
 * @version $Revision$ $Date$
 * 
 */
public class RepastRunner extends AbstractRunner {

	private static MessageCenter msgCenter = MessageCenter
			.getMessageCenter(RepastRunner.class);

	private RunEnvironmentBuilder runEnvironmentBuilder;
	protected Controller controller;
	protected boolean pause = false;
	protected Object monitor = new Object();
	protected SweeperProducer producer;
	private ISchedule schedule;

	public RepastRunner() {
		runEnvironmentBuilder = new DefaultRunEnvironmentBuilder(this, true);
		controller = new DefaultController(runEnvironmentBuilder);
		controller.setScheduleRunner(this);
	}

	public void loadScenario(File scenarioDir) throws Exception {
		if (scenarioDir.exists()) {
			BatchScenarioLoader loader = new BatchScenarioLoader(scenarioDir);
			ControllerRegistry registry = loader.load(runEnvironmentBuilder);
			controller.setControllerRegistry(registry);
			//
			controller.batchInitialize();
			controller.runParameterSetters(loader.getParameters());
		} else {
			msgCenter.error("Scenario not found", new IllegalArgumentException(
					"Invalid scenario " + scenarioDir.getAbsolutePath()));
			System.out.println("Scenario not found");
			return;
		}
	}

	public void runInitialize() {
		DefaultParameters defaultParameters = new DefaultParameters();
		defaultParameters.addParameter("randomSeed", "Default Random Seed",
				Number.class, 1, true);
		controller.runInitialize(defaultParameters);
		
		schedule = RunState.getInstance().getScheduleRegistry()
				.getModelSchedule();
	}

	public void cleanUpRun() {
		controller.runCleanup();
	}

	public void cleanUpBatch() {
		controller.batchCleanup();
	}

	// returns the tick count of the next scheduled item
	public double getNextScheduledTime() {
		return ((Schedule) RunEnvironment.getInstance().getCurrentSchedule())
				.peekNextAction().getNextTime();
	}

	// returns the number of model actions on the schedule
	public int getModelActionCount() {
		return schedule.getModelActionCount();
	}

	// returns the number of non-model actions on the schedule
	public int getActionCount() {
		return schedule.getActionCount();
	}

	// Step the schedule
	public void step() {
		schedule.execute();
	}

	// stop the schedule
	public void stop() {
		super.stop();
		if (schedule != null)
			schedule.executeEndActions();
	}

	public void setFinishing(boolean fin) {
		schedule.setFinishing(fin);
	}

	public void execute(RunState toExecuteOn) {
		// required AbstractRunner stub. We will control the
		// schedule directly.
	}
}