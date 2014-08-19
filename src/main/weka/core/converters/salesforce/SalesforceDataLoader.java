package weka.core.converters.salesforce;

import java.util.ArrayList;
import java.util.List;

import weka.core.converters.DatabaseLoader;
import weka.salesforce.SalesforceConnection;

public class SalesforceDataLoader extends DatabaseLoader{
	private static final long serialVersionUID = 1L;
	public List<String> Errors = new ArrayList<String>();
	
	public SalesforceDataLoader() throws Exception {
		super();
	}
	
	public boolean hasErrors(){ return Errors.size() > 0; }

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
