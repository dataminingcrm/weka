package weka.salesforce;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import weka.core.Instances;
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
	
	public Sobj2arff run() throws Exception {
		if(this.config() == null){
			System.err.println("Missing config.properties file.");
			System.exit(1);
		}
		List<String> optionList = new ArrayList<String>();
		
		/*
		 * -username=username
		 * -password=password
		 * -url=url
		 * -token=token
		 * -class=class name
		 * -relation=object name
		 * -query=SOQL
		 */
		
		if( this.config().getProperty("username") != null ){
			optionList.add("-username");
			optionList.add( (String) this.config().getProperty("username") );
		}
		
		if( this.config().getProperty("password") != null ){
			optionList.add("-password");
			optionList.add( (String) this.config().getProperty("password") );
		}
		
		if( this.config().getProperty("url") != null ){
			optionList.add("-url");
			optionList.add( (String) this.config().getProperty("url") );
		}
		
		if( this.config().getProperty("token") != null ){
			optionList.add("-token");
			optionList.add( (String) this.config().getProperty("token") );
		}
		
		if( this.config().getProperty("query") != null ){
			optionList.add("-query");
			optionList.add( (String) this.config().getProperty("query") );
		}
		
		if( this.config().getProperty("relation") != null ){
			optionList.add("-relation");
			optionList.add( (String) this.config().getProperty("relation") );
		}
		
		if( this.config().getProperty("class") != null ){
			optionList.add("-class");
			optionList.add( (String) this.config().getProperty("class") );
		}
		
		String[] options = optionList.toArray(new String[optionList.size()]);
		
		// The base data loader throws STDERR messages. Squelch here to ensure a clean pipe output.
		System.setErr(new PrintStream(new OutputStream() {
		    public void write(int b) {
		    }
		}));
		
		SObjectLoader dataLoader = new SObjectLoader();
		dataLoader.setOptions(options);
		Instances data = dataLoader.getDataSet();
		System.out.println( data.toString() );
		
		return this;
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