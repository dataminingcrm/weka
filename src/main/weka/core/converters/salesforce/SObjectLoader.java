package weka.core.converters.salesforce;

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
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;
import weka.core.converters.DatabaseLoader;
import weka.salesforce.SalesforceConnection;
import weka.salesforce.attributes.AttributeStrategy;
import weka.salesforce.attributes.AttributeStrategyFactory;

public class SObjectLoader extends DatabaseLoader {	
	private static final long serialVersionUID = 1L;
	public List<String> Errors = new ArrayList<String>();
	
	public SObjectLoader() throws Exception {
		super();
	}
	
	public String getRevision() {
		return "1.0";
	}
	
	public boolean hasErrors(){ return Errors.size() > 0; }
	
	@Override
	public Instances getDataSet(){
		try {
			if( this.getQueryResult() == null || this.getQueryResult().getRecords().length == 0){
				Errors.add("Query returned 0 rows. Could not generate data set.");
				return this.m_structure;
			}
			SObject[] records = this.getQueryResult().getRecords();
			
			// Infer column attributes from first record
		} catch (ConnectionException e) {
			e.printStackTrace();
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
	
	private QueryResult m_QueryResult = null;
	public QueryResult getQueryResult() throws ConnectionException{
		if(m_QueryResult == null){
			if(this.validate().hasErrors()){
				return null;
			}
			String soql = this.getQuery();
			if(soql.toLowerCase().contains("select *")){
				String allFields = this.getConnection().getAllFieldsByObject( this.getRelationName() );
				soql = soql.replace("*", allFields);
				this.setQuery(soql);
			}
			// TODO: Incremental loader using queryMore. Return a collection of SObjects
			m_QueryResult = this.getConnection().getPartnerConnection().query( soql );
		}
		return m_QueryResult;
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
			//System.out.println("getField compare " + f.getName() + ":" + fieldName);
			if(f.getName().equals(fieldName)){
				return f;
			}
		}
		return null;
	}
	
	public List<Attribute> getAttributes() throws ConnectionException{
		List<Attribute> attributes = new ArrayList<Attribute>();
		for(String key : this.getAttributeStrategies().keySet()){
			attributes.add( this.getAttributeStrategies().get(key).getAttribute() );			
		}
		return attributes;
	}
	
	private Map<String, AttributeStrategy> m_AttributeStrategy = null;
	public Map<String, AttributeStrategy> getAttributeStrategies() throws ConnectionException{
		if(m_AttributeStrategy == null){
			if(this.validate().hasErrors()){
				return null;
			}
			m_AttributeStrategy = new LinkedHashMap<String, AttributeStrategy>();
			
			Iterator<XmlObject> itr = this.getQueryResult().getRecords()[0].getChildren();
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
				
				Attribute attrib = strategy.buildAttribute();
				//attrib.column columnIndex++
				strategy.setAttribute( attrib );
				//System.out.println("Adding attribute: " + attributeName + " type:" + f.getName() );
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
				
		this.m_User 	= Utils.getOption("username", options);
		this.m_Password = Utils.getOption("password", options);
		this.m_Token	= Utils.getOption("token", options);
		this.m_URL		= Utils.getOption("url", options);
		this.m_RelationName	= Utils.getOption("relation", options);
		this.m_Classifier= Utils.getOption("class", options);
		
		// Note: Utils.getOption also removes the element from the array.		
		String query = Utils.getOption("query", options);
		if(query != null){
			this.setQuery(query);
		}
	}
	
	private String m_Classifier = null;
	public void setClassifer(String c){ this.m_Classifier = c; }
	public String getClassifier(){ return this.m_Classifier; }
	
	private String m_RelationName = null;
	public void setRelationName(String rName){ this.m_RelationName = rName; }
	public String getRelationName(){ return this.m_RelationName; }
	
	private String m_Token = null;
	public void setToken(String token){ this.m_Token = token; }
	public String getToken(){ return this.m_Token; }
	
	private SalesforceConnection m_Connection = null;
	public SalesforceConnection getConnection(){
		if(m_Connection == null){
			m_Connection = new SalesforceConnection()
				.withUsername( this.getUser() )
				.withPassword( this.getPassword() )
				.withSecurityToken( this.getToken() )
				.withLoginUrl( this.getUrl() )
				.connectWithUserCredentials();
			
			if( !m_Connection.isValid() ){
				System.err.println("Could not establish Salesforce connection. Please check config.properties file.");
			}
		}
		return m_Connection;
	}
}