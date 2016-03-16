package test.soeasy.mesolevel;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { 	
	// meso-level tests
	test.soeasy.mesolevel.effectOnMotoneuronTest.ScenarioExecuter.class,
	test.soeasy.mesolevel.effectOnMotoneuronTest02.ScenarioExecuter.class,
	test.soeasy.mesolevel.effectOnMotoneuronTest03.ScenarioExecuter.class,
	test.soeasy.mesolevel.effectOnMotoneuronTest04.ScenarioExecuter.class,
	test.soeasy.mesolevel.compareEffectsOnMotoneuronWithFixedFrequency.ScenarioExecuter.class,
	test.soeasy.mesolevel.compareEffectsOnMotoneuronWithVariableFrequency01.ScenarioExecuter.class,
	test.soeasy.mesolevel.compareEffectsOnMotoneuronWithVariableFrequency02.ScenarioExecuter.class,
	test.soeasy.mesolevel.creationOfSynapseExcBy2ndLevelNeighbour01.ScenarioExecuter.class,
	test.soeasy.mesolevel.creationOfSynapseExcBy2ndLevelNeighbour03.ScenarioExecuter.class,
	test.soeasy.mesolevel.creationOfSynapseExcBy3rdLevelNeighbour.ScenarioExecuter.class,
	test.soeasy.mesolevel.creationOfSynapseInhBy3rdLevelNeighbour.ScenarioExecuter.class,
	test.soeasy.mesolevel.nominalBehTest.ScenarioExecuter.class,
	test.soeasy.mesolevel.scenario10.ScenarioExecuter.class
	})
public class AllTests {
	// why on earth I need this class, I have no idea!
}