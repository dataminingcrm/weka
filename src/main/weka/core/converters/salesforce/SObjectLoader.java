package weka.core.converters.salesforce;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import com.sforce.soap.partner.DescribeSObjectResult;
import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.PicklistEntry;
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
				if( this.getQueryResult() == null || this.getQueryResult().getRecords().length == 0){
					Errors.add("Query returned 0 rows. Could not generate data set.");
					return this.m_structure;
				}
				m_structure = new Instances(this.getRelationName(), this.getAttributes(), this.getQueryResult().getRecords().length );
				SObject[] records = this.getQueryResult().getRecords();
				for(SObject obj : records){
					Instance instance = new Instance( this.getAttributeStrategies().size() );
					for(String fieldName : this.getAttributeStrategies().keySet()){
						Object value = obj.getField(fieldName);
						if(value == null){
							continue;
						}
						AttributeStrategy strategy = getAttributeStrategies().get(fieldName);
						
						if( strategy.getAttribute().isNominal() ){
							Attribute appendAttrib = appendNominalValue( strategy.getAttribute().name(), (String)value );
							if(appendAttrib != null){
								strategy.setAttribute(appendAttrib);
							}
						}
						
						if( strategy.getAttribute().isNumeric() || strategy.getAttribute().isDate() ){
							System.out.println("Adding numeric field. " + fieldName + "=" + strategy.getNumericValue( value ) + ". Strategy " +  strategy.getClass().toString());
							instance.setValue(strategy.getAttribute(), strategy.getNumericValue( obj.getField(fieldName) ) );
						} else {
							System.out.println("Adding string field. " + fieldName + "=" + strategy.getValue( value ) + ". Strategy " +  strategy.getClass().toString());
							instance.setValue(strategy.getAttribute(), strategy.getValue( value ) );
						}
					}
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
			// TODO: Implement incremental loader using queryMore. Return a collection of SObjects. 
			// Currently limited to 10,000 records.
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
	
	// normalizeNominalValue
	/*
	 * Picklist field metadata defines nominal values.
	 * Actual instances may contain picklist values not defined in metadata.
	 * Check for new nominal values and append them here.
	 */
	public Attribute appendNominalValue(String attributeName, String value) throws ConnectionException{
		Enumeration attribs = this.getAttributes().elements();
		int index = 0;
		System.out.println("Adding value " + value + " to nominal attribute " + attributeName);
		if(value == null || value.equals("")){
			return null;
		}
		while (attribs.hasMoreElements()){
			Attribute attrib = (Attribute) attribs.nextElement();
			if(attrib.name().equals(attributeName)){
				boolean valueFound = false;
				Enumeration values = attrib.enumerateValues();
				int valueCount = 0;
				while (values.hasMoreElements()){
					String s = (String) values.nextElement();
					valueCount++;
					System.out.println("*********Nominal value: " + s);
					if(s.equals(value)){
						valueFound = true;
						break;
					}
				}
				
				if( !valueFound ){
					System.out.println("!!!!!!! Nominal value not found.... appending to attribute.");
					int size = valueCount + 1;
					FastVector attributeValues = new FastVector(size);
					
					// TODO: Extract method
					values = attrib.enumerateValues();
					while (values.hasMoreElements()){
						attributeValues.addElement( (String) values.nextElement() );
					}
					attributeValues.addElement( value );
					
					Attribute newAttrib = new Attribute( attributeName, attributeValues,  attrib.index() );
					
					this.getAttributes().setElementAt(newAttrib, index);
					this.dumpAttribute(attributeName);
					return newAttrib;
				}
			}
			index++;
		}
		return null;
	}
	
	private void dumpAttribute(String name) throws ConnectionException{
		System.out.println("Dumping attribute " + name);
		Attribute attrib = this.getAttribute(name);
		Enumeration values = attrib.enumerateValues();
		while (values.hasMoreElements()){
			String s = (String) values.nextElement();
			System.out.println("*********Nominal value: " + s);
		}
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
		this.setToken( Utils.getOption("token", options) );
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
}