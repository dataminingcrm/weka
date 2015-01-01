package weka.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Settings {

	public static final String SALESFORCE_LOGIN_URL = "url";
	public static final String SALESFORCE_PASSWORD = "password";
	public static final String SALESFORCE_TOKEN = "token";
	public static final String SALESFORCE_USERNAME = "username";
	public static final String RELATION = "relation";
	public static final String QUERY = "query";
	public static final String CLASSIFIER = "class";

	public static String get(final String key) {
		final String val = System.getenv(key);

		if (val != null && val != "") {
			return val;
		}

		// Look in local config file
		final Properties props = Settings.loadProperties();
		if (props == null) {
			return null;
		} else {
			return (String) props.get(key);
		}
	}

	public static Properties loadProperties() {
		final Properties envProperties = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream("config.properties");
			envProperties.load(input);
		} catch (final IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}
		return envProperties;
	}
}