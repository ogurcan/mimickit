package rast.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import repast.simphony.engine.watcher.WatcheeInstrumentor;
import simphony.util.messages.MessageCenter;
import simphony.util.messages.MessageEvent;
import simphony.util.messages.MessageEventListener;

/**
 * This class tests a given simulation model for a specific scenario by running
 * simulations purely programmatically. To run a simulation purely
 * programmatically with fine control, SimulationRunner class that extends
 * repast.simphony.engine.environment.AbstractRunner is used.
 * 
 * @author Önder Gürcan
 * @version $Revision$ $Date$
 * 
 */
abstract public class AbstractScenarioExecuter {

	private RepastRunner runner;

	// to avoid frozen class exception
	private Set<String> instrumentedNames = new HashSet<String>();

	// data loader file of Repast
	private static String dataLoaderFileName = "repast.simphony.dataLoader.engine.ClassNameDataLoaderAction_0.xml";

	private AssertionError thrownAssertionError = null;

	@Before
	public void initialize() {
		// instrumentedNames.add(Neuron.class.getCanonicalName());
		// instrumentedNames.add(RestingNeuron.class.getCanonicalName());
		// instrumentedNames.add(InnervatedNeuron.class.getCanonicalName());
		// instrumentedNames.add(RunningNeuron.class.getCanonicalName());
		// instrumentedNames.add(WiringViewer.class.getCanonicalName());
		// instrumentedNames.add(ExtracellularFluid.class.getCanonicalName());
		//
		// to avoid frozen class exception
		WatcheeInstrumentor.getInstrumented().addAll(instrumentedNames);

		File file = new File(getScenarioDir());
		initializeScenarioFiles();

		runner = new RepastRunner();
		try {
			// load the scenario
			runner.loadScenario(file);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// initialize the run
		runner.runInitialize();
	}

	/**
	 * Runs a given scenario for once until a certain amount of time unless it
	 * finished by itself.
	 * 
	 * @param runUntil
	 *            the time in which the simulation run is supposed to finish.
	 */
	public void executeTestScenario(double runUntil) {
		try {
			// loop until the last action is left
			while (runner.go() && runner.getActionCount() > 0) {
				if (runner.getModelActionCount() == 0) {
					runner.setFinishing(true);
				}

				// if this run exceeds the given time, the test fails.
				double currentTick = SimulationEnvironment.getInstance()
						.getCurrentTick();
				if ((currentTick >= runUntil) || (thrownAssertionError != null)) {
					runner.stop();
				} else {
					// execute all scheduled actions at next tick
					runner.step();
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void executeTestScenario(String sweepFile, double runUntil) {
		SimulationEnvironment.getInstance().loadParameters(sweepFile);
		executeTestScenario(runUntil);
	}

	@After
	public void stop() {
		// to avoid frozen class exception
		instrumentedNames.addAll(WatcheeInstrumentor.getInstrumented());

		// execute any actions scheduled at run end
		runner.stop();

		runner.cleanUpRun();

		// clean up the batch after all runs complete
		runner.cleanUpBatch();

		// remove all scenario files
		cleanUpScenarioFiles();
	}

	private void initializeScenarioFiles() {
		initializeDataLoaderFile();
		initializeContextXMLFile();
		initializeScenarioXMLFile();
		// initializeUserPathXMLFile();
	}

	// private void initializeUserPathXMLFile() {
	// String userPathXML = "user_path.xml";
	// File userPathXMLFile = new File(getScenarioDir() + "/" + userPathXML);
	// //
	// try {
	// BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
	// userPathXMLFile));
	// bufferedWriter.write("<model name=\"SO-EASY\">\n");
	// bufferedWriter.write("<classpath>\n");
	// bufferedWriter
	// .write("<agents path=\"../../../../../../SO-EASY/bin\" ");
	// bufferedWriter.write("filter=\"" + Neuron.class.getCanonicalName()
	// + "\"/>\n");
	// bufferedWriter
	// .write("<agents path=\"../../../../../../SO-EASY/bin\" ");
	// bufferedWriter.write("filter=\""
	// + WiringViewer.class.getCanonicalName() + "\"/>\n");
	// bufferedWriter.write("<agents path=\"../../../../../../SO-EASY/bin\" ");
	// bufferedWriter.write("filter=\""
	// + ExtracellularFluid.class.getCanonicalName() + "\"/>\n");
	// bufferedWriter.write("<agents path=\"../../../../../../SO-EASY/bin\" ");
	// bufferedWriter.write("filter=\"" + getScenarioPackage()
	// + ".TestAgent\"/>\n");
	// bufferedWriter.write("</classpath>\n");
	// bufferedWriter.write("</model>");
	// bufferedWriter.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	private void initializeScenarioXMLFile() {
		String scenarioXML = "scenario.xml";
		File scenarioXMLFile = new File(getScenarioDir() + "/" + scenarioXML);
		//
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
					scenarioXMLFile));
			bufferedWriter
					.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
			bufferedWriter.write("<Scenario>\n");
			bufferedWriter
					.write("<repast.simphony.dataLoader.engine.ClassNameDataLoaderAction ");
			bufferedWriter.write("file=\"");
			bufferedWriter.write(dataLoaderFileName);
			bufferedWriter.write("\" context=\"SO-EASY\" />\n");
			bufferedWriter.write("</Scenario>");
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initializeContextXMLFile() {
		String contextXML = "context.xml";
		File contextXMLFile = new File(getScenarioDir() + "/" + contextXML);
		//
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
					contextXMLFile));
			bufferedWriter.write("<context id=\"SO-EASY\">\n");
			bufferedWriter.write("</context>");
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initializeDataLoaderFile() {
		File dataLoaderFile = new File(getScenarioDir() + "/"
				+ dataLoaderFileName);
		String dataLoader = "<string>" + this.getClass().getPackage().getName()
				+ ".ScenarioBuilder</string>";
		//
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
					dataLoaderFile));
			bufferedWriter.write(dataLoader);
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void cleanUpScenarioFiles() {
		File dataLoaderFile = new File(getScenarioDir() + "/"
				+ dataLoaderFileName);
		dataLoaderFile.delete();

		String contextXML = "context.xml";
		File contextXMLFile = new File(getScenarioDir() + "/" + contextXML);
		contextXMLFile.delete();

		String scenarioXML = "scenario.xml";
		File scenarioXMLFile = new File(getScenarioDir() + "/" + scenarioXML);
		scenarioXMLFile.delete();

		// String userPathXML = "user_path.xml";
		// File userPathXMLFile = new File(getScenarioDir() + "/" +
		// userPathXML);
		// userPathXMLFile.delete();
	}

	protected String getScenarioPackage() {
		return this.getClass().getPackage().getName();
	}

	protected String getScenarioDir() {
		String packageName = this.getClass().getPackage().getName();
		String scenarioDir = "./test/" + packageName.replace('.', '/') + "/";
		return scenarioDir;
	}

	protected String getDataDir() {
		String packageName = this.getClass().getPackage().getName();
		String dataDir = "./test/" + packageName.replace('.', '/') + "/data/";
		return dataDir;
	}

	@Test
	public void mainTest() throws Exception {
		MessageCenter.addMessageListener(new MessageEventListener() {
			@Override
			public void messageReceived(MessageEvent messageEvent) {
				if ((thrownAssertionError == null)
						&& (messageEvent.getThrowable() != null)) {
					Throwable cause = messageEvent.getThrowable().getCause();
					if (cause instanceof AssertionError) {
						double currentTick = SimulationEnvironment
								.getInstance().getCurrentTick();
						AssertionError assertionError = new AssertionError(
								cause.getMessage() + " at tick " + currentTick);
						assertionError.setStackTrace(cause.getStackTrace());
						thrownAssertionError = assertionError;
					}
				}
			}
		});

		runTest();

		if (thrownAssertionError != null) {
			throw thrownAssertionError;
		}
	}

	abstract public void runTest() throws Exception;
}
