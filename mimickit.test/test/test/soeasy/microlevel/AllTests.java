package test.soeasy.microlevel;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { 
	// innervated neuron
	//test.soeasy.microlevel.innervatedNeuron.nominal.innervationTest.ScenarioExecuter.class,
	
	// resting neuron
	test.soeasy.microlevel.restingNeuron.nominal.conductExcitation01.ScenarioExecuter.class,
	test.soeasy.microlevel.restingNeuron.nominal.conductExcitation02.ScenarioExecuter.class,
	test.soeasy.microlevel.restingNeuron.nominal.conductInhibition01.ScenarioExecuter.class,
	test.soeasy.microlevel.restingNeuron.nominal.conductInhibition02.ScenarioExecuter.class,		
	test.soeasy.microlevel.restingNeuron.nominal.integrateAndFireTestWithoutInhibitoryContributor.ScenarioExecuter.class,
	test.soeasy.microlevel.restingNeuron.nominal.integrateAndFireTestWithInhibitoryContributor.ScenarioExecuter.class,
	test.soeasy.microlevel.restingNeuron.nominal.integrateAndFireTestWithInhibitoryContributor02.ScenarioExecuter.class,
	test.soeasy.microlevel.restingNeuron.adaptive.removeNeuronWhenItHasNoSynapseTest.ScenarioExecuter.class,
	test.soeasy.microlevel.restingNeuron.adaptive.removeNeuronWhenItIsNotActiveTest.ScenarioExecuter.class,
	
	// running neuron
	test.soeasy.microlevel.runningNeuron.nominal.isiTest01.ScenarioExecuter.class,
	test.soeasy.microlevel.runningNeuron.nominal.isiTest02.ScenarioExecuter.class,
	test.soeasy.microlevel.runningNeuron.nominal.isiTest03.ScenarioExecuter.class,
	test.soeasy.microlevel.runningNeuron.nominal.isiTest04.ScenarioExecuter.class,	
	test.soeasy.microlevel.runningNeuron.nominal.conductExcitation.ScenarioExecuter.class,
	test.soeasy.microlevel.runningNeuron.nominal.tonicFiringTuningTest.ScenarioExecuter.class
	})
public class AllTests {
	// why on earth I need this class, I have no idea!
}