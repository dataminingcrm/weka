package weka.salesforce.filters;

import junit.framework.Assert;

import org.junit.Test;

import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.salesforce.SObjectLoader;
import weka.test.TestBase;

public class FilterTests extends TestBase {

	@Test
	public void classifierTests() throws Exception {
		final SObjectLoader dataLoader = this.getConnectedLoader();
		Assert.assertNull(dataLoader.getClassifier());

		dataLoader.setRelationName("Opportunity");
		dataLoader.setQuery("SELECT * FROM Opportunity LIMIT 5");

		Assert.assertFalse(dataLoader.validate().hasErrors());
		Assert.assertNotNull(dataLoader.getDataSet());

		Instances dataset = dataLoader.getDataSet();
		Attribute stageNameAttrib = dataset.attribute("StageName");
		Assert.assertNotNull(stageNameAttrib);
		Assert.assertEquals("StageName", stageNameAttrib.name());
		Assert.assertEquals(true, stageNameAttrib.isString());
		Assert.assertEquals(false, stageNameAttrib.isNominal());

		dataset = new SalesforceFilter(dataset,
				dataLoader.getAttributeStrategies()).StringsToNominal()
				.getDataSet();

		stageNameAttrib = dataset.attribute("StageName");
		Assert.assertNotNull(stageNameAttrib);
		Assert.assertEquals(false, stageNameAttrib.isString());
		Assert.assertEquals(true, stageNameAttrib.isNominal());

		Attribute accountIdAttrib = dataset.attribute("AccountId");
		Assert.assertNotNull(accountIdAttrib);
		Attribute pricebookIdAttrib = dataset.attribute("Pricebook2Id");
		Assert.assertNotNull(pricebookIdAttrib);

		dataset = new SalesforceFilter(dataset,
				dataLoader.getAttributeStrategies()).RemoveReferences()
				.getDataSet();

		accountIdAttrib = dataset.attribute("AccountId");
		Assert.assertNull(accountIdAttrib);
		pricebookIdAttrib = dataset.attribute("Pricebook2Id");
		Assert.assertNull(pricebookIdAttrib);

		Attribute isClosedAttrib = dataset.attribute("IsClosed");
		Assert.assertNotNull(isClosedAttrib);

		dataset = new SalesforceFilter(dataset,
				dataLoader.getAttributeStrategies()).RemoveField("IsClosed")
				.getDataSet();

		isClosedAttrib = dataset.attribute("IsClosed");
		Assert.assertNull(isClosedAttrib);
	}
}