package weka.salesforce.classifiers;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.salesforce.SObjectLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.RemoveType;
import weka.salesforce.filters.SalesforceFilter;
import weka.test.TestBase;

public class BayesClassifierTests extends TestBase {

	@Test
	public void classifierTests() throws Exception {
		final SObjectLoader dataLoader = getConnectedLoader();
		Assert.assertNull(dataLoader.getClassifier());

		dataLoader.setRelationName("Opportunity");
		dataLoader.setQuery("SELECT * FROM Opportunity LIMIT 20");

		Assert.assertFalse(dataLoader.validate().hasErrors());
		Assert.assertNotNull(dataLoader.getDataSet());

		Instances dataset = dataLoader.getDataSet();
		System.out.println(dataset.toString());

		// Get list of attributes of string type
		final List<Integer> stringIndices = new ArrayList<Integer>();
		final Enumeration attributes = dataset.enumerateAttributes();
		while (attributes.hasMoreElements()) {
			final Attribute attrib = (Attribute) attributes.nextElement();
			System.out.println("Attrib (post-filter):" + attrib.name()
					+ " Index:" + attrib.index());
			if (attrib.isString()) {
				stringIndices.add(attrib.index() + 1);
			}
		}

		// Convert strings to nominal
		System.out.println("Converting " + stringIndices.size()
				+ " string indices to nominal: " + stringIndices.toString());

		dataset = new SalesforceFilter(dataset,
				dataLoader.getAttributeStrategies()).StringsToNominal()
				.getDataSet();

		// Remove date attributes
		final String[] filterOptions = new String[2];
		filterOptions[0] = "-T";
		filterOptions[1] = "date";
		final RemoveType remove = new RemoveType();
		remove.setOptions(filterOptions);
		remove.setInputFormat(dataset);
		dataset = Filter.useFilter(dataset, remove);

		dataset = removeSystemFields(dataset);
		System.out.println(dataset.toString());

		final Attribute stageNameAttrib = dataset.attribute("StageName");
		dataset.setClass(stageNameAttrib);

		final NaiveBayes bayes = new NaiveBayes();
		bayes.buildClassifier(dataset);
		System.out.println(bayes);
	}

	private Instances removeSystemFields(final Instances dataset) {
		Attribute attrib = SObjectLoader.getAttribute(
				dataset.enumerateAttributes(), "Id");
		dataset.deleteAttributeAt(attrib.index());

		attrib = SObjectLoader.getAttribute(dataset.enumerateAttributes(),
				"OwnerId");
		if (attrib != null) {
			dataset.deleteAttributeAt(attrib.index());
		}

		attrib = SObjectLoader.getAttribute(dataset.enumerateAttributes(),
				"CreatedById");
		if (attrib != null) {
			dataset.deleteAttributeAt(attrib.index());
		}

		attrib = SObjectLoader.getAttribute(dataset.enumerateAttributes(),
				"LastModifiedById");
		if (attrib != null) {
			dataset.deleteAttributeAt(attrib.index());
		}

		return dataset;
	}
}