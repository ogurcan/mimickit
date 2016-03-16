package rast.core;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import repast.simphony.engine.environment.RunEnvironment;

public class SimulationEnvironment {
	
	private static SimulationEnvironment instance;
	
	protected Properties parameters;
	
	protected SimulationEnvironment() {
		initializeParameters();
		
		try {
			FileWriter writer = new FileWriter("./parameters.txt");
			this.parameters.store(writer, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void initializeParameters() {		
		this.parameters = new Properties();
	}

	/**
	 * Returns the instance of the singleton SimulationEnvironment class.
	 * @return
	 */
	public static SimulationEnvironment getInstance() {
		if (instance == null) {
			instance = new SimulationEnvironment();
		}
		
		return instance;
	}
	
	public double getCurrentTick() {
		return RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
	}
	
	public void loadParameters(String sweepFileName) {		
		try {
			File sweepFile = new File(sweepFileName);
			FileReader reader = new FileReader(sweepFile);
			this.parameters.load(reader);
			System.out.println(sweepFile.getAbsolutePath() + " loaded.");
			this.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	@Override
	public String toString() {		
		System.out.println("-----------Parameter Values------------");
		Iterator<String> iterator = this.parameters.stringPropertyNames().iterator();
		while (iterator.hasNext()) {
			String propertyName = iterator.next();
			String property = this.parameters.getProperty(propertyName);
			System.out.println(propertyName + " = " + property);
		}
		System.out.println("--------------------------------------");
		return super.toString();
	}

}
