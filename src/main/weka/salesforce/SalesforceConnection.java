package weka.salesforce;

/* Copyright (c) 2014, Salesforce.com, Inc.  All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

- Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
- Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
 - Neither the name of Salesforce.com nor the names of its contributors
   may be used to endorse or promote products derived from this
   software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE
COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.sforce.soap.apex.Connector;
import com.sforce.soap.apex.SoapConnection;
import com.sforce.soap.metadata.MetadataConnection;
import com.sforce.soap.partner.DescribeSObjectResult;
import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.GetServerTimestampResult;
import com.sforce.soap.partner.GetUserInfoResult;
import com.sforce.soap.partner.LimitInfo;
import com.sforce.soap.partner.LoginResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class SalesforceConnection {
	public final static String API_VERSION	= "29.0";
	private String client_id		= "client_id_goes_here"; // For OAuth connections
	private String login_url 		= "https://login.salesforce.com/services/Soap/u/" + this.API_VERSION;
	private String instance_url		= null; // Example: https://na1.salesforce.com/
	private String org_id			= null;
	private String access_token 	= null;
	private String refresh_token 	= null;
	private String username			= null;
	private String password			= null;
	private String security_token	= null;
	private String session_id		= null;
	private String partner_url		= null;
	public List<String> errors		= new ArrayList<String>();
	public boolean wasRefreshed		= false;
	
	private LoginResult loginResult;
	private PartnerConnection partnerConnection;
	private MetadataConnection metadataConnection;
	private SoapConnection apexConnection;
	
	public SalesforceConnection withClientId(String cid){
		this.client_id = cid;
		return this;
	}
	
	public SalesforceConnection withOrgId(String oid){
		this.org_id = oid;
		return this;
	}
	
	public SalesforceConnection withLoginUrl(String url){
		this.login_url = url;
		return this;
	}
	
	public SalesforceConnection withInstanceUrl(String url){
		this.instance_url = url;
		return this;
	}
	
	public SalesforceConnection withAccessToken(String aToken){
		this.access_token = aToken;
		return this;
	}
	
	public SalesforceConnection withRefreshToken(String rToken){
		this.refresh_token = rToken;
		return this;
	}
	
	public SalesforceConnection withUsername(String uname){
		this.username = uname;
		return this;
	}
	
	public SalesforceConnection withPassword(String pw){
		this.password = pw;
		return this;
	}
	
	public SalesforceConnection withSecurityToken(String token){
		this.security_token = token;
		return this;
	}
	
	public SalesforceConnection withSession(String session){
		this.session_id = session;
		return this;
	}
	
	public SalesforceConnection withPartnerUrl(String url){
		this.partner_url = url;
		return this;
	}
	
	public boolean isProduction(){
		return !isSandbox();
	}
	
	public boolean isSandbox(){
		if(this.instance_url == null){
			this.errors.add("isSandbox() and isProduction() cannot be called without first setting withLoginUrl() context");
			return false;
		}	
		boolean result = false;
		try {
			URI loginURI = new URI(this.instance_url);
			result = loginURI.getHost().toLowerCase().startsWith("cs");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private boolean validateInputParams(){
		this.errors.clear();
		if(this.client_id == null || this.client_id == ""){
			errors.add("Missing client_id parameter");
		}
		if(this.instance_url == null || this.instance_url == ""){
			errors.add("Missing server_url parameter");
		}
		if(this.org_id == null || this.org_id == ""){
			errors.add("Missing org_id parameter");
		}
		if(this.access_token == null || this.access_token == ""){
			errors.add("Missing access_token parameter");
		}
		if(this.refresh_token == null || this.refresh_token == ""){
			errors.add("Missing refresh_token parameter");
		}
		return errors.size() == 0;
	}
	
	public boolean hasErrors(){
		return this.errors.size() > 0;
	}
		
	private URL getServerUrl(){
		URL returnURL = null;
		try {
			if(this.partner_url != null && this.partner_url != ""){
				returnURL = new URL( this.partner_url );
			} else {
				returnURL = new URL( this.instance_url + "/services/Soap/u/" + this.API_VERSION + "/" + this.org_id );
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return returnURL;
	}
	
	private URL getLoginUrl(){
		URL returnURL = null;
		try {
			if(this.login_url.contains("/u/")){
				returnURL = new URL( this.login_url );
			} else {
				returnURL = new URL( this.login_url + "/services/Soap/u/" + this.API_VERSION );
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return returnURL;
	}
		
	public SalesforceConnection connectWithToken(){
		if(!validateInputParams()){
			return this;
		}
		loginResult 	= new LoginResult();
		// Format https://na14.salesforce.com/services/Soap/u/29.0/00Dd0000000gzcr
		loginResult.setServerUrl(this.getServerUrl().toExternalForm());
		loginResult.setSessionId(this.access_token);
		
		final ConnectorConfig config = new ConnectorConfig();
        config.setServiceEndpoint(this.getServerUrl().toExternalForm());
        config.setSessionId(this.access_token);
        try {
			this.partnerConnection = new PartnerConnection(config);
			this.metadataConnection = this.createMetadataConnection(loginResult);
			this.apexConnection	= this.createApexConnection(loginResult);
		} catch (ConnectionException e) {
			this.errors.add(e.getMessage());
			e.printStackTrace();
		}
		return this;
	}
	
	/*
	 * This method will connect to Salesforce, attempt to get UserInfo, 
	 * and refresh the connection if the access_token is invalid.
	 */
	public SalesforceConnection connectWithGetUserInfo(){
		if(!validateInputParams()){
			return this;
		}
		loginResult 	= new LoginResult();
		// Format https://na14.salesforce.com/services/Soap/u/29.0/00Dd0000000gzcr
		loginResult.setServerUrl(this.getServerUrl().toExternalForm());
		loginResult.setSessionId(this.access_token);
		
		final ConnectorConfig config = new ConnectorConfig();
        config.setServiceEndpoint(this.getServerUrl().toExternalForm());
        config.setSessionId(this.access_token);
        try {
			this.partnerConnection = new PartnerConnection(config);
			this.setUserInfo(this.partnerConnection.getUserInfo());
			this.metadataConnection = this.createMetadataConnection(loginResult);
			this.apexConnection = this.createApexConnection(loginResult);
		} catch (ConnectionException e) {
			if(e.toString().contains("INVALID_SESSION_ID")){
				// Refresh token and try connection one more time 
				// FIXME: Ugly nested catch blocks going on in here.
				System.out.println("access_token failed for orgid: " + this.org_id + ". Attempting to refresh token...");
				AccessToken refreshToken = new AccessToken().withClientId(client_id).withLoginUrl(instance_url).withRefreshToken(refresh_token).refreshToken();
				if(refreshToken.wasSuccessful()){
					System.out.println("Token refresh was successful");
					this.access_token = refreshToken.getAccessToken();
					this.wasRefreshed = true;
					this.connectWithToken();
					try {
						this.setUserInfo(this.partnerConnection.getUserInfo());
					} catch (ConnectionException e1) {
						e1.printStackTrace();
					}
				}
				else{
					System.err.println("Token refresh failed...");
				}
			} else {
				this.errors.add("Could not establish Salesforce connection. Please check credentials or API limits. " + e.getMessage());
				this.errors.add(e.toString());
				e.printStackTrace();
			}
		}
		return this;
	}
	
	public SalesforceConnection connectWithUserCredentials(){				
		try {
			loginResult = this.loginToSalesforce();
			if(loginResult == null){
				return this;
			}
			this.partnerConnection 	= createPartnerConnection(loginResult);
			this.metadataConnection	= createMetadataConnection(loginResult);
			this.apexConnection		= createApexConnection(loginResult);
		} catch (ConnectionException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return this;
	}
	
	public SalesforceConnection connectWithSession(){		
		loginResult = new LoginResult();
		loginResult.setServerUrl( this.getServerUrl().toExternalForm() );
		loginResult.setSessionId( this.session_id );
		
		final ConnectorConfig config = new ConnectorConfig();
        config.setServiceEndpoint(this.getServerUrl().toExternalForm());
        config.setSessionId(this.session_id);
        
        try {
			this.partnerConnection = new PartnerConnection(config);
			this.metadataConnection = this.createMetadataConnection(loginResult);
			this.apexConnection	= createApexConnection(loginResult);
		} catch (ConnectionException e) {
			this.errors.add(e.getMessage());
			e.printStackTrace();
		}
        
        return this;
	}
	
	private LoginResult loginToSalesforce()  {
		LoginResult result = null;
		try {
			final ConnectorConfig config = new ConnectorConfig();
	        config.setAuthEndpoint( this.getLoginUrl().toExternalForm() );
	        config.setServiceEndpoint( this.getLoginUrl().toExternalForm() );
	        config.setManualLogin(true);
	        //System.out.println("Connecting with " + this.username + " " + this.password + " " + this.security_token);
	        result = (new PartnerConnection(config)).login(this.username, this.password + this.security_token);
		} catch (ConnectionException e) {
			e.printStackTrace();
			result = null;
		}
		return result;
    }
	
	private PartnerConnection createPartnerConnection(final LoginResult loginResult) throws ConnectionException {
		final ConnectorConfig config = new ConnectorConfig();
        config.setServiceEndpoint(loginResult.getServerUrl());
        config.setSessionId(loginResult.getSessionId());
        return new PartnerConnection(config);
    }
	
	private MetadataConnection createMetadataConnection(final LoginResult loginResult) throws ConnectionException{
		String sessionId = loginResult.getSessionId();		
		String url = loginResult.getServerUrl().replaceAll("/u/", "/m/");
		
		final ConnectorConfig config = new ConnectorConfig();
        config.setServiceEndpoint(url);
        config.setSessionId(sessionId);
        
        return new MetadataConnection(config);
	}
	
	private SoapConnection createApexConnection(final LoginResult loginResult) throws ConnectionException{
		String sessionId = loginResult.getSessionId();		
		String url = loginResult.getServerUrl().replaceAll("/u/", "/s/");
		
		final ConnectorConfig config = new ConnectorConfig();
        config.setServiceEndpoint(url);
        config.setSessionId(sessionId);
        
        return Connector.newConnection(config);
	}
	
	public boolean isValid(){
		try {
			if(this.loginResult == null){
				this.errors.add("loginResult is null");
				return false;
			}
			if(this.partnerConnection == null){
				this.errors.add("partnerConnection is null");
				return false;
			}
			GetServerTimestampResult result = this.getPartnerConnection().getServerTimestamp();
			if(result != null){
				return true;
			} else {
				this.errors.add("getServerTimestamp returned null.");
				return false;
			}
		} catch (ConnectionException e) {
			this.errors.add(e.getMessage());
			return false;
		}
	}
	
	public String getAccessToken(){
		return this.access_token;
	}
	
	public SalesforceConnection refresh(){
		this.errors.clear();
		AccessToken refreshToken = new AccessToken().withClientId(client_id).withLoginUrl(instance_url).withRefreshToken(refresh_token).refreshToken();
		if(refreshToken.wasSuccessful()){			
			this.access_token = refreshToken.getAccessToken();
			this.wasRefreshed = true;
			this.connectWithToken();
		}
		return this;
	}
	
	private GetUserInfoResult m_userInfoResult = null;
	public GetUserInfoResult getUserInfo(){
		if(m_userInfoResult == null){
			try {
				m_userInfoResult = this.partnerConnection.getUserInfo();
			} catch (ConnectionException e) {
				e.printStackTrace();
				this.errors.add("Could not retrieve UserInfo. Error message: " + e.getMessage());
				return null;
			}
		}
		return m_userInfoResult;
	}
	
	private SalesforceConnection setUserInfo(GetUserInfoResult info){
		m_userInfoResult = info;
		return this;
	}
	
	public LoginResult getLoginResult(){
		return this.loginResult;
	}
	
	public PartnerConnection getPartnerConnection(){
		return this.partnerConnection;
	}
	
	public MetadataConnection getMetadataConnection() {
		return this.metadataConnection;
	}
	
	public SoapConnection getApexConnection() {
		return this.apexConnection;
	}
	
	public double getCurrentAPIUsage(){
		LimitInfo[] limitInfoResult = this.getPartnerConnection().getLimitInfoHeader().getLimitInfo();
		double limit 					= 0;
		double currentUsage 			= 0;
		double percentageUsed 			= 0.0; 
		String formattedPercentageUsed 	= "0.0";

		for(LimitInfo info : limitInfoResult){
			if(!info.getType().equals("API REQUESTS")){
				System.err.println("Was expecting LimitInfo type to be API REQUESTS. But was " + info.getType());
				continue;
			}
			limit = info.getLimit();
			currentUsage = info.getCurrent();
			percentageUsed = ( currentUsage / limit ) * 100.0;
			System.out.println("percentageUsed: " + formattedPercentageUsed);
			
			break;
		}
		return percentageUsed;
	}
	
	public double getAPILimit(){
		LimitInfo[] limitInfoResult = this.getPartnerConnection().getLimitInfoHeader().getLimitInfo();
		double limit = 0;
		
		for(LimitInfo info : limitInfoResult){
			if(!info.getType().equals("API REQUESTS")){
				System.err.println("Was expecting LimitInfo type to be API REQUESTS. But was " + info.getType());
				continue;
			}
			limit = info.getLimit();
			break;
		}
		return limit;
	}
	
	public String getAllFieldsByObject(String sobjectName){
		String field_list = "";
		
		DescribeSObjectResult describeSObjectResult = null;
		try {
			describeSObjectResult = this.getPartnerConnection().describeSObject(sobjectName);
		} catch (ConnectionException e) {
			e.printStackTrace();
		}
		
		Field[] fields = describeSObjectResult.getFields();
		
		for (int j = 0; j < fields.length; j++) {
			Field field = fields[j];
			field_list += field.getName() + ",";
		}
		return field_list.substring(0, field_list.length() - 1);
	}
	
	public void dumpErrors(){
		for(String err : this.errors){
			System.err.println(err);
		}
	}
}