package weka.core.converters.salesforce;

import java.util.Map;
import java.util.Properties;
import junit.framework.Assert;
import org.junit.Test;

import com.sforce.soap.partner.QueryResult;

import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.RemoveType;
import weka.salesforce.ConfigurableTest;
import weka.salesforce.attributes.AttributeStrategy;

public class SObjectLoaderTests extends ConfigurableTest {

	@Test
	public void connectionTests() throws Exception{
		Properties props = this.config();
		Assert.assertNotNull(props);
		Assert.assertNotNull(props.getProperty("username"), "Expected to find test.properties file with a setting for 'username'");
		Assert.assertNotNull(props.getProperty("password"), "Expected to find test.properties file with a setting for 'password'");
		Assert.assertNotNull(props.getProperty("url"), "Expected to find test.properties file with a setting for 'url'");
		
		final String SFDC_USERNAME 	= props.getProperty("username");
		final String SFDC_PASSWORD 	= props.getProperty("password");
		final String SFDC_TOKEN 	= props.getProperty("token");
		final String SFDC_URL 		= props.getProperty("url");
		
		SObjectLoader dataLoader = new SObjectLoader();
		
		dataLoader.setUrl( SFDC_URL );
		dataLoader.setUser( SFDC_USERNAME );
		dataLoader.setPassword( SFDC_PASSWORD );
		dataLoader.setToken( SFDC_TOKEN );
		
		Assert.assertEquals(true, dataLoader.getConnection().isValid() ); // Future tests require a valid connection.
	}
	
	@Test
	public void wildcardQueryTests() throws Exception{
		SObjectLoader dataLoader = this.getConnectedLoader();
		dataLoader.setQuery("SELECT * FROM Opportunity LIMIT 10");
		QueryResult result = dataLoader.getQueryResult();
		// Missing required fields: relationName
		Assert.assertNull(result);
		Assert.assertTrue(dataLoader.hasErrors());
				
		dataLoader.setRelationName("Opportunity");
		result = dataLoader.getQueryResult();
		Assert.assertNotNull(result);
		Assert.assertFalse(dataLoader.hasErrors());
		
		// Wildcard substitution
		Assert.assertFalse( dataLoader.getQuery().contains("*") );
		System.out.println( dataLoader.getQuery() );
	}
	
	@Test
	public void basicQueryTests() throws Exception{
		SObjectLoader dataLoader = this.getConnectedLoader();
		dataLoader.setRelationName("Opportunity");
		dataLoader.setQuery("SELECT Id, Name, CreatedDate, Amount, StageName FROM Opportunity LIMIT 10");		
		QueryResult result = dataLoader.getQueryResult();
		Assert.assertNotNull(result);
		Assert.assertFalse(dataLoader.hasErrors());
		Assert.assertEquals(5, dataLoader.getAttributeStrategies().size());
		Assert.assertEquals(5, dataLoader.getAttributes().size());
	}
	
	@Test
	public void attributeGenerationTests() throws Exception{
		SObjectLoader dataLoader = this.getConnectedLoader();
		dataLoader.setRelationName("Opportunity");
		dataLoader.setQuery("SELECT * FROM Opportunity LIMIT 10");
		Assert.assertFalse( dataLoader.validate().hasErrors() );
		Map<String, AttributeStrategy> attributeStrategies = dataLoader.getAttributeStrategies();
		Assert.assertTrue( attributeStrategies.size() > 0 );
		Assert.assertTrue( dataLoader.getAttributes().size() > 0 );
		
		Assert.assertNotNull( dataLoader.getAttribute("StageName") );
		Assert.assertNull( dataLoader.getAttribute("foo") );
		
		// Modify Attribute tests	
	}
	
	@Test
	public void getDataSetTests() throws Exception{
		SObjectLoader dataLoader = this.getConnectedLoader();
		dataLoader.setRelationName("Opportunity");
		dataLoader.setQuery("SELECT * FROM Opportunity LIMIT 10");
		Assert.assertFalse( dataLoader.validate().hasErrors() );
		Assert.assertNotNull( dataLoader.getDataSet() );
	}
	
	@Test
	public void classifierTests() throws Exception{
		SObjectLoader dataLoader = this.getConnectedLoader();
		Assert.assertNull( dataLoader.getClassifier() );
		
		dataLoader.setRelationName("Opportunity");
		dataLoader.setQuery("SELECT * FROM Opportunity LIMIT 20");
		Assert.assertFalse( dataLoader.validate().hasErrors() );
		Assert.assertNotNull( dataLoader.getDataSet() );
		
		dataLoader.setClassifer("StageName");
		Assert.assertNotNull( dataLoader.getClassifier() );
		Assert.assertEquals("StageName", dataLoader.getClassifier() );
		
		Instances dataset = dataLoader.getDataSet();
		System.out.println( dataset.toString() );
		
		String[] filterOptions = new String[2];
		filterOptions[0] = "-T";                            // "Type"
		filterOptions[1] = "string";						// "string"
		RemoveType remove = new RemoveType();               // new instance of filter
		remove.setOptions(filterOptions);                   // set options
		remove.setInputFormat(dataset);                        // inform filter about dataset **AFTER** setting options
		Instances newData = Filter.useFilter(dataset, remove);   // apply filter
		
		// This classifier won't work with string attributes.
		// TODO: Need to test filters. Can they dynamically be applied/unapplied?
		
		// weka.filters.unsupervised.attribute.RemoveType(string) 
		String[] options = new String[1];
		options[0] = "-U";
		J48 tree = new J48();
		tree.setOptions(options);
		tree.buildClassifier(newData);	
		System.out.println(tree);
	}
}