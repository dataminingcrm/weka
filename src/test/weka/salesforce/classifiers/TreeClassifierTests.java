package weka.salesforce.classifiers;

import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.salesforce.SObjectLoader;
import weka.salesforce.filters.SalesforceFilter;
import weka.test.TestBase;

public class TreeClassifierTests extends TestBase {

	@Test
	public void classifierWithFiltersTests() throws Exception {
		final SObjectLoader dataLoader = getConnectedLoader();
		Assert.assertNull(dataLoader.getClassifier());

		dataLoader.setRelationName("Opportunity");
		dataLoader.setQuery("SELECT * FROM Opportunity LIMIT 50");

		Assert.assertFalse(dataLoader.validate().hasErrors());
		Assert.assertNotNull(dataLoader.getDataSet());

		Instances dataset = dataLoader.getDataSet();
		System.out.println(dataset.toString());
		Attribute stageNameAttrib = dataset.attribute("StageName");
		Assert.assertNotNull(stageNameAttrib);
		Assert.assertEquals("StageName", stageNameAttrib.name());

		dataset = new SalesforceFilter(dataset,
				dataLoader.getAttributeStrategies()).StringsToNominal()
				.RemoveReferences().RemoveField("IsClosed")
				.RemoveField("IsWon").RemoveField("Probability")
				.RemoveField("ForecastCategory")
				.RemoveField("ForecastCategoryName").getDataSet();

		System.out.println("Attributes post StringToNominal filter. \r"
				+ dataset.toString());

		stageNameAttrib = dataset.attribute("StageName");
		Assert.assertNotNull(stageNameAttrib);
		Assert.assertEquals("StageName", stageNameAttrib.name());
		dataset.setClass(stageNameAttrib);

		final J48 tree = new J48();
		tree.setUnpruned(true);
		tree.buildClassifier(dataset);
		System.out.println(tree);
		// tree.distributionForInstance(instance)
	}

	@Test
	public void classifierSimpleQueryTests() throws Exception {
		final SObjectLoader dataLoader = getConnectedLoader();
		Assert.assertNull(dataLoader.getClassifier());

		dataLoader.setRelationName("Opportunity");
		dataLoader
		.setQuery("SELECT Id, StageName, Amount, ExpectedRevenue, TotalOpportunityQuantity, Type, LeadSource FROM Opportunity LIMIT 50");

		Assert.assertFalse(dataLoader.validate().hasErrors());
		Assert.assertNotNull(dataLoader.getDataSet());

		Instances dataset = dataLoader.getDataSet();
		Attribute stageNameAttrib = dataset.attribute("StageName");
		Assert.assertNotNull(stageNameAttrib);
		Assert.assertEquals("StageName", stageNameAttrib.name());

		dataset = new SalesforceFilter(dataset,
				dataLoader.getAttributeStrategies()).StringsToNominal()
				.getDataSet();

		System.out.println("Attributes post StringToNominal filter. \r"
				+ dataset.toString());

		stageNameAttrib = dataset.attribute("StageName");
		Assert.assertNotNull(stageNameAttrib);
		Assert.assertEquals("StageName", stageNameAttrib.name());
		dataset.setClass(stageNameAttrib);

		final J48 tree = new J48();
		tree.setUnpruned(true);
		tree.buildClassifier(dataset);
		System.out.println(tree);

		// Emit Java source code for this decision tree
		final String source = tree.toSource("treeSource");
		System.out.println(source);
	}

	@Test
	public void modelEvaluationTests() throws Exception {
		System.out
		.println("------------------------------------------------------------------------");
		final SObjectLoader dataLoader = getConnectedLoader();
		Assert.assertNull(dataLoader.getClassifier());

		dataLoader.setRelationName("Opportunity");
		dataLoader
		.setQuery("SELECT Id, StageName, Amount, ExpectedRevenue, TotalOpportunityQuantity, Type, LeadSource FROM Opportunity LIMIT 50");

		Assert.assertFalse(dataLoader.validate().hasErrors());
		Assert.assertNotNull(dataLoader.getDataSet());

		Instances dataset = dataLoader.getDataSet();
		Attribute stageNameAttrib = dataset.attribute("StageName");
		Assert.assertNotNull(stageNameAttrib);
		Assert.assertEquals("StageName", stageNameAttrib.name());

		dataset = new SalesforceFilter(dataset,
				dataLoader.getAttributeStrategies()).RemoveField("Id")
				.StringsToNominal().getDataSet();

		stageNameAttrib = dataset.attribute("StageName");
		Assert.assertNotNull(stageNameAttrib);
		Assert.assertEquals("StageName", stageNameAttrib.name());
		dataset.setClass(stageNameAttrib);

		final J48 tree = new J48();
		tree.setUnpruned(true);
		tree.buildClassifier(dataset);

		final Instances evalDataset = new Instances(dataset);

		Evaluation eval = new Evaluation(dataset);
		eval.crossValidateModel(tree, dataset, 10, new Random(1));
		System.out.println(eval.toSummaryString());

		eval = new Evaluation(evalDataset);
		eval.evaluateModel(tree, evalDataset);
		System.out.println(eval.toSummaryString("\nResults\n======\n", false));
	}

	@Test
	public void modelPredictionTests() throws Exception {
		System.out
		.println("------------------------------------------------------------------------");
		final SObjectLoader dataLoader = getConnectedLoader();
		Assert.assertNull(dataLoader.getClassifier());

		dataLoader.setRelationName("Opportunity");
		dataLoader
		.setQuery("SELECT Id, StageName, Amount, ExpectedRevenue, TotalOpportunityQuantity, Type, LeadSource FROM Opportunity LIMIT 50");

		Assert.assertFalse(dataLoader.validate().hasErrors());
		Assert.assertNotNull(dataLoader.getDataSet());

		Instances dataset = dataLoader.getDataSet();
		Attribute stageNameAttrib = dataset.attribute("StageName");
		Assert.assertNotNull(stageNameAttrib);
		Assert.assertEquals("StageName", stageNameAttrib.name());

		dataset = new SalesforceFilter(dataset,
				dataLoader.getAttributeStrategies()).RemoveField("Id")
				.StringsToNominal().getDataSet();

		stageNameAttrib = dataset.attribute("StageName");
		dataset.setClass(stageNameAttrib);

		final J48 tree = new J48();
		tree.setUnpruned(true);
		tree.buildClassifier(dataset);
		System.out.println(tree);

		System.out
		.println("Simulating prediction loop over existing data set...");
		for (int i = 0; i < dataset.numInstances(); i++) {
			final double clsLabel = tree.classifyInstance(dataset.instance(i));

			System.out.println("\tPredicted: "
					+ stageNameAttrib.value((int) clsLabel)
					+ "\t|\tActual:"
					+ stageNameAttrib.value((int) dataset.instance(i).value(
							stageNameAttrib)));
		}
	}
}