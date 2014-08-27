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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.sforce.soap.partner.DescribeSObjectResult;
import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.bind.XmlObject;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;
import weka.salesforce.attributes.AttributeStrategy;
import weka.salesforce.attributes.AttributeStrategyFactory;

public class SObjectLoader extends SalesforceDataLoader {	
	private static final long serialVersionUID = 1L;	
	
	public SObjectLoader() throws Exception {
		super();
	}
	
	public String getRevision() {
		return "1.0";
	}
	
	@Override
	public Instances getDataSet(){
		if( m_structure == null){
			try {
				if( this.getQueryRecords() == null || this.getQueryRecords().size() == 0){
					Errors.add("Query returned 0 rows. Could not generate data set.");
					return this.m_structure;
				}
				m_structure = new Instances(this.getRelationName(), this.getAttributes(), this.getQueryRecords().size() );
				List<SObject> records = this.getQueryRecords();
				for(SObject obj : records){
					Instance instance = new Instance( this.getAttributeStrategies().size() );
					for(String fieldName : this.getAttributeStrategies().keySet()){
						Object value = obj.getField(fieldName);
						if(value == null){
							continue;
						}
						AttributeStrategy strategy = getAttributeStrategies().get(fieldName);
						
						if( strategy.getAttribute().isNominal() && !strategy.containsValue(value) ){
							Attribute newAttrib = strategy.appendNominalValue( (String)value );
							this.getAttributes().setElementAt(newAttrib, newAttrib.index());
						}
						
						if( strategy.getAttribute().isNumeric() || strategy.getAttribute().isDate() ){
							instance.setValue(strategy.getAttribute(), strategy.getNumericValue( obj.getField(fieldName) ) );
						} else {
							instance.setValue(strategy.getAttribute(), strategy.getValue( value ) );
						}
					}
					this.m_structure.add(instance);
				}
			} catch (ConnectionException e) {
				e.printStackTrace();
			}
		}
		return m_structure;		
	}
	
	public SObjectLoader validate(){
		this.Errors.clear();
		if(this.getQuery() == null){
			this.Errors.add("Missing query.");
		}
		if(this.getRelationName() == null){
			this.Errors.add("Missing relation name.");
		}
		if( !this.getConnection().isValid() ){			
			Errors.add( "Salesforce connection is not valid. Check credentials.");
		}
		return this;
	}
	
	private List<SObject> m_SObjects = null;
	public List<SObject> getQueryRecords() throws ConnectionException{
		if(m_SObjects == null){
			if(this.validate().hasErrors()){
				return null;
			}
			m_SObjects = new ArrayList<SObject>();
			String soql = this.getQuery();
			if(soql.toLowerCase().contains("select *")){
				String allFields = this.getConnection().getAllFieldsByObject( this.getRelationName() );
				soql = soql.replace("*", allFields);
				this.setQuery(soql);
			}
			
			QueryResult result = this.getConnection().getPartnerConnection().query( soql );
			boolean done = false;
			if(result.getSize() > 0){
				while(!done){
					SObject[] records = result.getRecords();
					for(SObject obj : records){ m_SObjects.add(obj); }
					if (result.isDone()) {
						done = true;
					} else {
						result = this.getConnection().getPartnerConnection().queryMore(result.getQueryLocator());
					}
				}
			}
		}
		return m_SObjects;
	}
	
	DescribeSObjectResult m_DescribeSObjectResult = null;
	public DescribeSObjectResult getSObjectDescription() throws ConnectionException{
		if(m_DescribeSObjectResult == null){
			m_DescribeSObjectResult = this.getConnection().getPartnerConnection().describeSObject(this.getRelationName());
		}
		return m_DescribeSObjectResult;
	}
	
	private Field getField(String fieldName) throws ConnectionException{
		for(Field f : getSObjectDescription().getFields()){
			if(f.getName().equals(fieldName)){
				return f;
			}
		}
		return null;
	}
	
	FastVector m_Attributes = null;
	public FastVector getAttributes() throws ConnectionException{
		if ( m_Attributes == null ){
			m_Attributes = new FastVector();
			for(String key : this.getAttributeStrategies().keySet()){
				m_Attributes.addElement( this.getAttributeStrategies().get(key).getAttribute() );			
			}
		}
		return m_Attributes;
	}
	
	public Attribute getAttribute(String name) throws ConnectionException{
		Enumeration v = this.getAttributes().elements();
		while (v.hasMoreElements()){
			Attribute attrib = (Attribute) v.nextElement();
			if(attrib.name().equals(name)){
				return attrib;
			}
		}
		return null;
	}
	
	private Map<String, AttributeStrategy> m_AttributeStrategy = null;
	public Map<String, AttributeStrategy> getAttributeStrategies() throws ConnectionException{
		if(m_AttributeStrategy == null){
			if(this.validate().hasErrors()){
				return null;
			}
			m_AttributeStrategy = new LinkedHashMap<String, AttributeStrategy>();
			
			Iterator<XmlObject> itr = this.getQueryRecords().get(0).getChildren();
			int columnIndex = 0;
			while(itr.hasNext()) {
				XmlObject element = (XmlObject) itr.next();
				String attributeName = element.getName().getLocalPart();
				Field f = this.getField(attributeName);
				if( f == null ){
					System.out.println("Couldn't find AttributeStrategy for field: " + attributeName);
					continue;
				}
				AttributeStrategy strategy = AttributeStrategyFactory.buildStrategy(f, columnIndex++);
				m_AttributeStrategy.put( attributeName, strategy );
			}
		}
		return m_AttributeStrategy;
	}
	
	/*
	 * (non-Javadoc)
	 * @see weka.datagenerators.DataGenerator#setOptions(java.lang.String[])
	 * Options reserved by super class:
	 * r = relation name
	 * o = output
	 * d = debug
	 * S = random seed  
	 */
	@SuppressWarnings("unchecked")
	public Enumeration<Option> listOptions(){
		@SuppressWarnings("rawtypes")
		Vector result = new Vector();
		
		// TODO: Add proxy support
		result.addElement(new Option( "\tSalesforce Username.", "username", 1, "-username"));		
		result.addElement(new Option( "\tSalesforce Password.", "password", 1, "-password"));
		result.addElement(new Option( "\tSalesforce Security Token.", "token", 1, "-token"));		
		result.addElement(new Option( "\tSalesforce Login URL.", "url", 1, "-url"));		
		result.addElement(new Option( "\tSalesforce SOQL Query to be executed.", "query", 1, "-query"));		
		result.addElement(new Option( "\tARFF Relation (Typically Salesforce object name).", "relation", 1, "-relation"));		
		result.addElement(new Option( "\tSalesforce Field in query to be used as classifier.", "class", 1, "-class"));
		
		return result.elements();
	}
	
	@Override
	public void setOptions(String[] options) throws Exception{
		super.setOptions(options);
		
		// Note: Utils.getOption also removes the element from the array.
		this.m_User 	= Utils.getOption("username", options);
		this.m_Password = Utils.getOption("password", options);
		this.setToken( Utils.getOption("token", options) );
		this.m_URL		= Utils.getOption("url", options);
		this.setRelationName( Utils.getOption("relation", options) );
		this.setClassifer( Utils.getOption("class", options) );
					
		String query = Utils.getOption("query", options);
		if(query != null){
			this.setQuery(query);
		}
	}
	
	private String m_Classifier = null;
	public void setClassifer(String c) throws ConnectionException{ 
		this.m_Classifier = c;
		Attribute classAttribute = this.getAttribute(c);
		if(classAttribute != null && this.getDataSet() != null){
			this.getDataSet().setClass(classAttribute);
		}
	}
	public String getClassifier(){ return this.m_Classifier; }
	
	private String m_RelationName = null;
	public void setRelationName(String rName){ this.m_RelationName = rName; }
	public String getRelationName(){ return this.m_RelationName; }
}