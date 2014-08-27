package weka.core.converters.salesforce;
/*
Weka machine learning library for Salesforce SObjects.
Copyright (C) 2014  Michael Leach

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
*/
import java.util.List;
import java.util.Map;
import java.util.Properties;
import junit.framework.Assert;
import org.junit.Test;

import com.sforce.soap.partner.sobject.SObject;

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
		List<SObject> records = dataLoader.getQueryRecords();
		// Missing required fields: relationName
		Assert.assertNull(records);
		Assert.assertTrue(dataLoader.hasErrors());
				
		dataLoader.setRelationName("Opportunity");
		records = dataLoader.getQueryRecords();
		Assert.assertNotNull(records);
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
		List<SObject> records = dataLoader.getQueryRecords();
		Assert.assertNotNull(records);
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
		
		// J28 classifier doesn't work with string attributes.
		// Use filter to remove string types.
		String[] options = new String[1];
		options[0] = "-U";
		J48 tree = new J48();
		tree.setOptions(options);
		tree.buildClassifier(newData);	
		System.out.println(tree);
	}
}