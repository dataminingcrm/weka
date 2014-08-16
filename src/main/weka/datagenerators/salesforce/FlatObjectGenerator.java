package weka.datagenerators.salesforce;

import weka.core.Instances;
import weka.datagenerators.salesforce.SalesforceDataGenerator;

/*
 * TODO: Investigate refactoring of separate Classification and Regression generators.
 */
public class FlatObjectGenerator extends SalesforceDataGenerator {
	
	@Override
	public Instances defineDataFormat() {
		// See http://programcreek.com/java-api-examples/index.php?example_code_path=weka-weka.datagenerators.clusterers-SubspaceCluster.java
		// for example.
		return null;
	}
}