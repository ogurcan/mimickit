package rast.core;

import java.lang.reflect.Constructor;

import repast.simphony.context.DefaultContext;

/**
 * 
 * @author Önder Gürcan
 *
 */
public class TestEnvironment extends DefaultContext<Object> {
	
	// TODO remove this constructor
	public TestEnvironment() {
		//
	}
		
	public TestEnvironment(Object name) {
		super(name);	
	}

	/**
	 * 
	 * @param packageName
	 */
	public void createTestAgent(String packageName) {
		String className = packageName + ".TestAgent";
		try {
			Class<?> cls = Class.forName(className);
			Constructor<?> constructor = cls.getConstructor();
			Object testAgent = constructor.newInstance();
			this.add(testAgent);			
		} catch (Throwable e) {
			System.err.println("Error while creating the Tester agent");
			e.printStackTrace();
		}
	}
}
