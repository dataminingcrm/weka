package weka.datagenerators.salesforce;

import java.util.Enumeration;
import java.util.Vector;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;
import weka.salesforce.SalesforceConnection;

public class SalesforceDataGenerator extends weka.datagenerators.DataGenerator {

	public String getRevision() {		
		return "1.0";
	}
	
	@Override
	public void setOptions(String[] options) throws Exception{		
		super.clearBlacklist();
		super.setOptions(options);
				
		this.m_username = Utils.getOption("username", options);
		this.m_password = Utils.getOption("password", options);
		this.m_token	= Utils.getOption("token", options);
		this.m_url		= Utils.getOption("url", options);
		
		// Note: Utils.getOption also removes the element from the array.
		String relationName = Utils.getOption("object", options);
		if(relationName != null){
			this.setRelationName( relationName );
		}
	}
	
	private String m_username 	= null;
	public String getUsername(){
		return this.m_username;
	}
	
	private String m_password 	= null;
	public String getPassword(){
		return this.m_password;
	}
	
	private String m_token		= null;
	public String getToken(){
		return this.m_token;
	}
		
	private String m_url		= null;
	public String getURL(){
		return this.m_url;
	}
	
	private SalesforceConnection connection = null;
	public SalesforceConnection getConnection(){
		if(connection == null){
			connection = new SalesforceConnection()
				.withUsername( this.getUsername() )
				.withPassword( this.getPassword() )
				.withSecurityToken( this.getToken() )
				.withLoginUrl( this.getURL() )
				.connectWithUserCredentials();
			
			if( !connection.isValid() ){
				System.err.println("Could not establish Salesforce connection. Please check config.properties file.");
			}
		}
		return connection;
	}
	
	/*
	 * (non-Javadoc)
	 * @see weka.datagenerators.DataGenerator#setOptions(java.lang.String[])
	 * Options reserved by super class:
	 * r = relation name
	 * o = output
	 * d = debug
	 * S = random seed
	 * 
	 *  Options supported by this generator:
	 *  username = username
	 *  password = password
	 *  token = token
	 *  url = login url
	 *  c = classifier
	 */
	public Enumeration<Option> listOptions(){
		Vector result = new Vector();
		
		result.addElement(new Option(
	          "\tSalesforce Username.",
	          "username", 1, "-username"));
		
		result.addElement(new Option(
		          "\tSalesforce Password.",
		          "password", 1, "-password"));

		result.addElement(new Option(
		          "\tSalesforce Security Token.",
		          "token", 1, "-token"));
		
		result.addElement(new Option(
		          "\tSalesforce Login URL.",
		          "url", 1, "-url"));
		
		result.addElement(new Option(
		          "\tSalesforce Object to be converted to ARFF.",
		          "object", 1, "-object"));
		
		result.addElement(new Option(
		          "\tSalesforce Field on -object to be used as classifier.",
		          "class", 1, "-class"));
		
		return result.elements();
	}
	
	@Override
	public Instance generateExample() throws Exception {
		return null;
	}

	@Override
	public Instances generateExamples() throws Exception {
		return null;
	}

	@Override
	public String generateFinished() throws Exception {
		return null;
	}

	@Override
	public String generateStart() throws Exception {
		return null;
	}

	@Override
	public boolean getSingleModeFlag() throws Exception {
		return false;
	}
}