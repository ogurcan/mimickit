package test.mimickit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ 
	test.soeasy.util.AllTests.class,
	test.soeasy.microlevel.AllTests.class,
	test.soeasy.mesolevel.AllTests.class 
	//test.soeasy.macrolevel.AllTests.class
	})
public class AllTests {
	// why on earth I need this class, I have no idea!
}