package weka.core.converters.salesforce;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import weka.salesforce.SalesforceConnection;
import weka.salesforce.attributes.AttributeStrategy;
import weka.test.TestBase;
import weka.utils.Settings;

import com.sforce.soap.partner.sobject.SObject;

public class SObjectLoaderTests extends TestBase {
	private final String TEST_QUERY = "SELECT * FROM Opportunity WHERE IsWon=true";

	@Test
	public void connectionTests() throws Exception {
		final SalesforceConnection conn = getSalesforceConnection();
		Assert.assertNotNull(conn);
		Assert.assertTrue(conn.isValid());

		// This override prevents writes to STDERR to ensure a clean pipe
		// output.
		System.setErr(new PrintStream(new OutputStream() {
			@Override
			public void write(final int b) {
			}
		}));

		final SObjectLoader dataLoader = new SObjectLoader();

		dataLoader.setUrl(Settings.get(Settings.SALESFORCE_LOGIN_URL));
		dataLoader.setUser(Settings.get(Settings.SALESFORCE_USERNAME));
		dataLoader.setPassword(Settings.get(Settings.SALESFORCE_PASSWORD));
		dataLoader.setToken(Settings.get(Settings.SALESFORCE_TOKEN));

		Assert.assertEquals(true, dataLoader.getConnection().isValid());
	}

	@Test
	public void setOptionsTests() throws Exception {
		final SObjectLoader dataLoader = new SObjectLoader();
		final List<String> optionList = new ArrayList<String>();

		optionList.add("-url");
		optionList.add(Settings.get(Settings.SALESFORCE_LOGIN_URL));

		optionList.add("-username");
		optionList.add(Settings.get(Settings.SALESFORCE_USERNAME));

		optionList.add("-password");
		optionList.add(Settings.get(Settings.SALESFORCE_PASSWORD));

		optionList.add("-token");
		optionList.add(Settings.get(Settings.SALESFORCE_TOKEN));

		optionList.add("-query");
		optionList.add(TEST_QUERY);

		optionList.add("-relation");
		optionList.add("Opportunity");

		optionList.add("class");
		optionList.add("IsWon");

		final String[] options = optionList.toArray(new String[optionList
		                                                       .size()]);

		dataLoader.setOptions(options);

		Assert.assertEquals(Settings.get(Settings.SALESFORCE_LOGIN_URL),
				dataLoader.getUrl());
		Assert.assertEquals(Settings.get(Settings.SALESFORCE_USERNAME),
				dataLoader.getUser());
		Assert.assertEquals(Settings.get(Settings.SALESFORCE_PASSWORD),
				dataLoader.getPassword());
		Assert.assertEquals(TEST_QUERY, dataLoader.getQuery());
		Assert.assertEquals(Settings.get(Settings.SALESFORCE_TOKEN),
				dataLoader.getToken());
		Assert.assertEquals("Opportunity", dataLoader.getRelationName());

		Assert.assertEquals(true, dataLoader.getConnection().isValid());
	}

	@Test
	public void wildcardQueryTests() throws Exception {
		final SObjectLoader dataLoader = getConnectedLoader();
		dataLoader.setQuery("SELECT * FROM Opportunity LIMIT 10");
		List<SObject> records = dataLoader.getQueryRecords();
		// Missing required fields: relationName
		Assert.assertNull(records);
		Assert.assertTrue(dataLoader.hasErrors());

		dataLoader.setRelationName("Opportunity");
		records = dataLoader.getQueryRecords();
		Assert.assertNotNull(records);
		Assert.assertFalse(dataLoader.hasErrors());

		// Wildcard substitution
		Assert.assertFalse(dataLoader.getQuery().contains("*"));
		System.out.println(dataLoader.getQuery());
	}

	@Test
	public void basicQueryTests() throws Exception {
		final SObjectLoader dataLoader = getConnectedLoader();
		dataLoader.setRelationName("Opportunity");
		dataLoader
				.setQuery("SELECT Id, Name, CreatedDate, Amount, StageName FROM Opportunity LIMIT 10");
		final List<SObject> records = dataLoader.getQueryRecords();
		Assert.assertNotNull(records);
		Assert.assertEquals(10, records.size());
		Assert.assertFalse(dataLoader.hasErrors());
		Assert.assertEquals(5, dataLoader.getAttributeStrategies().size());
		Assert.assertEquals(5, dataLoader.getAttributes().size());
	}

	@Test
	public void attributeGenerationTests() throws Exception {
		final SObjectLoader dataLoader = getConnectedLoader();
		dataLoader.setRelationName("Opportunity");
		dataLoader.setQuery("SELECT * FROM Opportunity LIMIT 10");
		Assert.assertFalse(dataLoader.validate().hasErrors());
		final Map<String, AttributeStrategy> attributeStrategies = dataLoader
				.getAttributeStrategies();
		Assert.assertTrue(attributeStrategies.size() > 0);
		Assert.assertTrue(dataLoader.getAttributes().size() > 0);

		Assert.assertNotNull(dataLoader.getAttribute("StageName"));
		Assert.assertNull(dataLoader.getAttribute("foo"));

		for (final AttributeStrategy attrib : attributeStrategies.values()) {
			System.out.println(attrib.getField().getName() + " "
					+ attrib.getField().getType().toString());
		}
	}

	@Test
	public void getDataSetTests() throws Exception {
		final SObjectLoader dataLoader = getConnectedLoader();
		dataLoader.setRelationName("Opportunity");
		dataLoader.setQuery("SELECT * FROM Opportunity LIMIT 10");
		Assert.assertFalse(dataLoader.validate().hasErrors());
		Assert.assertNotNull(dataLoader.getDataSet());
	}
}