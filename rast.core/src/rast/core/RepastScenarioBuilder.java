package rast.core;

import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;

/**
 * @author Önder Gürcan
 * @version $Revision$ $Date$
 *
 */
abstract public class RepastScenarioBuilder implements ContextBuilder<Object> {

	private TestEnvironment testEnvironment;

	@Override
	public Context<Object> build(Context<Object> context) {
		context.addSubContext(getTestEnvironment());			
		
		createAgents();

		String packageName = this.getClass().getPackage().getName();
		getTestEnvironment().createTestAgent(packageName);

		return context;
	}	

	abstract protected void createAgents();
	
	/**
	 * TODO This method should be private.
	 * @return
	 */
	protected TestEnvironment getTestEnvironment() {
		if (this.testEnvironment == null) {
			this.testEnvironment = new TestEnvironment("Test Environment");
		}
		
		return this.testEnvironment;
	}

}
