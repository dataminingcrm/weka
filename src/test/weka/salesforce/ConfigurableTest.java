package weka.salesforce;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import weka.core.converters.salesforce.SObjectLoader;

public class ConfigurableTest {

	private Properties configProperties = null;
	protected Properties config(){
		if(configProperties == null){
			configProperties = new Properties();
			InputStream input = null;
			try {
				input = new FileInputStream("test.properties");
				configProperties.load(input);
			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return configProperties;
	}
	
	// Util method for constructing a Salesforce connection
	protected SObjectLoader getConnectedLoader() throws Exception{
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