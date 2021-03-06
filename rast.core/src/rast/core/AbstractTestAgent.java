package rast.core;


/**
 * @author Önder Gürcan
 * @version $Revision$ $Date$
 *
 */
abstract public class AbstractTestAgent { 

	protected String getDataDir() {
		String packageName = this.getClass().getPackage().getName();
		String dataDir = "./test/" + packageName.replace('.', '/') + "/data/";
		return dataDir;
	}

}
