package test.mimickit;

import mimickit.model.SOEASYEnvironment;
import rast.core.RepastScenarioBuilder;

abstract public class SOEasyScenarioBuilder extends RepastScenarioBuilder {

	public SOEasyScenarioBuilder() {
		super();
		getAgentFactory().reset();
	}
	
	protected SOEASYEnvironment getAgentFactory() {		
		return SOEASYEnvironment.getInstance();
	}	

}
