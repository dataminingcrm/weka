package weka.salesforce;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import weka.core.converters.salesforce.SObjectLoader;

public class Sobj2arff {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		new Sobj2arff().withArgs(args).run();
	}
	
	public Sobj2arff(){}
	
	private String[] commandLineArgs = null;
	public Sobj2arff withArgs(String[] args){
		commandLineArgs = args;
		return this;
	}
	
	public int run() throws Exception{
		SObjectLoader dataLoader = new SObjectLoader();
		List<String> optionList = new ArrayList<String>();
		
		/*
		 * -username=username
		 * -password=password
		 * -server=server_URL
		 * -token=token
		 * -classifier=class name
		 * -object=object name
		 */
		//optionList.add("-u" + this.getUserName() );
		
		String[] options = optionList.toArray(new String[optionList.size()]);
		dataLoader.setOptions(options);
		/*
		new ARFFBuilder()
			.withConnection( this.getConnection() )
			.withDataSource( this.getDataSource() )
			.withClass( this.config().getProperty("class") )
			.build();
		*/
		return 0;
	}
	
	private SalesforceConnection connection = null;
	private SalesforceConnection getConnection(){
		if(connection == null){
			connection = new SalesforceConnection()
				.withUsername( config().getProperty("username") )
				.withPassword( config().getProperty("password") )
				.withSecurityToken( config().getProperty("token") )
				.withLoginUrl( config().getProperty("url") )
				.connectWithUserCredentials();
			
			if( !connection.isValid() ){
				System.err.println("Could not establish Salesforce connection. Please check config.properties file.");
			}
		}
		return connection;
	}
	
	private String getDataSource(){
		if(commandLineArgs.length > 0 ){
			return commandLineArgs[0];
		}
		else if( config().getProperty("dataSource") != null){
			return config().getProperty("dataSource");
		} else {
			return "Opportunity";
		}
	}
	
	private Properties configProperties = null;
	private Properties config(){
		if(configProperties == null){
			configProperties = new Properties();
			InputStream input = null;
			try {
				input = new FileInputStream("config.properties");
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
}