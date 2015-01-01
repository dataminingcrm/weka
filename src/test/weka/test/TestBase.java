package weka.test;
import weka.core.converters.salesforce.SObjectLoader;
import weka.salesforce.SalesforceConnection;
import weka.utils.Settings;

public class TestBase {

	protected SalesforceConnection getSalesforceConnection() {
		return new SalesforceConnection()
		.withUsername(Settings.get(Settings.SALESFORCE_USERNAME))
		.withPassword(Settings.get(Settings.SALESFORCE_PASSWORD))
		.withLoginUrl(Settings.get(Settings.SALESFORCE_LOGIN_URL))
		.withSecurityToken(Settings.get(Settings.SALESFORCE_TOKEN))
				.connectWithUserCredentials();
	}

	// Util method for constructing a Salesforce connection
	protected SObjectLoader getConnectedLoader() throws Exception {
		final String SFDC_USERNAME = Settings.get(Settings.SALESFORCE_USERNAME);
		final String SFDC_PASSWORD = Settings.get(Settings.SALESFORCE_PASSWORD);
		final String SFDC_TOKEN = Settings.get(Settings.SALESFORCE_TOKEN);
		final String SFDC_URL = Settings.get(Settings.SALESFORCE_LOGIN_URL);

		final SObjectLoader dataLoader = new SObjectLoader();

		dataLoader.setUrl(SFDC_URL);
		dataLoader.setUser(SFDC_USERNAME);
		dataLoader.setPassword(SFDC_PASSWORD);
		dataLoader.setToken(SFDC_TOKEN);

		dataLoader.getConnection().isValid();

		return dataLoader;
	}
}