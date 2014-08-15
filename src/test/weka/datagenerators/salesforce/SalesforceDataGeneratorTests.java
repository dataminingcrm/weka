package weka.datagenerators.salesforce;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;

import weka.classifiers.meta.RandomCommittee;
import weka.core.Utils;
import weka.datagenerators.clusterers.BIRCHCluster;

public class SalesforceDataGeneratorTests extends ConfigurableTest {
	
	@Test
	public void optionParsingTests() throws Exception{
		Properties props = this.config();
		Assert.assertNotNull(props);
		Assert.assertNotNull(props.getProperty("username"), "Expected to find test.properties file with a setting for 'username'");
		Assert.assertNotNull(props.getProperty("password"), "Expected to find test.properties file with a setting for 'password'");
		Assert.assertNotNull(props.getProperty("url"), "Expected to find test.properties file with a setting for 'url'");
		
		final String SFDC_USERNAME 	= props.getProperty("username");
		System.out.println("USERNAME: " + SFDC_USERNAME);
		final String SFDC_PASSWORD 	= props.getProperty("password"); //System.getenv("SFDC_PASSWORD");
		final String SFDC_TOKEN 	= props.getProperty("token"); //System.getenv("SFDC_TOKEN");
		final String SFDC_URL 		= props.getProperty("url"); //System.getenv("SFDC_URL");
		
		List<String> optionList = new ArrayList<String>();
		optionList.add("-username");
		optionList.add(SFDC_USERNAME );
		optionList.add("-password");
		optionList.add(SFDC_PASSWORD);
		optionList.add("-token"); 
		optionList.add(SFDC_TOKEN);
		optionList.add("-url");
		optionList.add(SFDC_URL);
		
		String[] options = optionList.toArray(new String[optionList.size()]);
		SalesforceDataGenerator gen = new SalesforceDataGenerator();
		Assert.assertEquals(null, gen.getUsername() );
		
		gen.setOptions(options);
		Assert.assertEquals(SFDC_USERNAME, gen.getUsername() );
		Assert.assertEquals(SFDC_PASSWORD, gen.getPassword() );
		Assert.assertEquals(SFDC_TOKEN, gen.getToken() );
		Assert.assertEquals(SFDC_URL, gen.getURL() );
		
		Assert.assertEquals(true, gen.getConnection().isValid() ); // Future tests require a valid connection.
	}
}