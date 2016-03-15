package mimickit.scenario;

import mimickit.model.SOEASYEnvironment;
import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;

/**
 * @author Önder Gürcan
 * @version $Revision$ $Date$
 *
 */
abstract public class AbstractScenarioBuilder implements ContextBuilder<Object> {

	private SOEASYEnvironment agentFactory;

	@Override
	public Context<Object> build(Context<Object> context) {		
		agentFactory = SOEASYEnvironment.getInstance();
		context.addSubContext(agentFactory);
		agentFactory.reset();		

		createFunctionalAgents();

		return context;
	}

	protected SOEASYEnvironment getAgentFactory() {
		return agentFactory;
	}

	abstract protected void createFunctionalAgents();

}
