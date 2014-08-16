package weka.core.converters.salesforce;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import junit.framework.Assert;
import org.junit.Test;

import com.sforce.soap.partner.QueryResult;

import weka.core.Attribute;
import weka.datagenerators.salesforce.ConfigurableTest;
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
	public void basicQueryTests() throws Exception{
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
	public void attributeGenerationTests() throws Exception{
		SObjectLoader dataLoader = this.getConnectedLoader();
		dataLoader.setRelationName("Opportunity");
		dataLoader.setQuery("SELECT * FROM Opportunity LIMIT 10");
		Assert.assertFalse( dataLoader.validate().hasErrors() );
		Map<String, AttributeStrategy> attributeStrategies = dataLoader.getAttributeStrategies();
		Assert.assertTrue( attributeStrategies.size() > 0 );
		Assert.assertTrue( dataLoader.getAttributes().size() > 0 );
	}
	
	private SObjectLoader getConnectedLoader() throws Exception{
		Properties props = this.config();
		
		final String SFDC_USERNAME 	= props.getProperty("username");
		final String SFDC_PASSWORD 	= props.getProperty("password");
		final String SFDC_TOKEN 	= props.getProperty("token");
		final String SFDC_URL 		= props.getProperty("url");
		
		SObjectLoader dataLoader = new SObjectLoader();
		
		dataLoader.setUrl( SFDC_URL );
		dataLoader.setUser( SFDC_USERNAME );
		dataLoader.setPassword( SFDC_PASSWORD );
		dataLoader.setToken( SFDC_TOKEN );
		
		dataLoader.getConnection().isValid();
		
		return dataLoader;
	}
}