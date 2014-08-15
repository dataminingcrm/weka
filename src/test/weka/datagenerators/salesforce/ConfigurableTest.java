package weka.datagenerators.salesforce;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
}
