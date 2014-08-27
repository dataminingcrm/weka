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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.google.gson.Gson;

public class AccessToken {
	private String refresh_url = "https://login.salesforce.com/services/oauth2/token";
	private String access_token;
	private String refresh_token;
	private String login_url;
	private String client_id;
	private RefreshTokenResponse refreshResponse = null;

	public AccessToken withRefreshToken(String rtoken){
		this.refresh_token = rtoken;
		return this;
	}

	public AccessToken withLoginUrl(String url){
		this.login_url = url;
		return this;
	}

	public AccessToken withClientId(String cid){
		this.client_id = cid;
		return this;
	}

	public String getAccessToken(){
		return this.access_token;
	}

	public boolean wasSuccessful(){
		return this.access_token != null;
	}

	private String refreshParams(){
		return "grant_type=refresh_token&client_id=" + this.client_id + "&refresh_token=" + this.refresh_token;
	}

	public AccessToken refreshToken(){
		try {
			URL endpoint = new URL(refresh_url);
			URLConnection conn = endpoint.openConnection();
			conn.setDoOutput(true);

			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

			writer.write(this.refreshParams());
			writer.flush();

			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			while ((line = reader.readLine()) != null) {
				refreshResponse= new Gson().fromJson(line, RefreshTokenResponse.class);
				if(refreshResponse != null){
					this.access_token = refreshResponse.access_token;
				}
			}
			writer.close();
			reader.close();
		} catch (MalformedURLException e) {
			this.access_token = null;
			e.printStackTrace();
		} catch (IOException e) {
			this.access_token = null;
			System.err.println("Token refresh failed for clientId: " + this.client_id + ". refresh_token:" + this.refresh_token);
			System.err.println("Reason: " + e.getMessage());
		}
		return this;
	}

	public class RefreshTokenResponse {
		public String id;
		public String issued_at;
		public String scope;
		public String instance_url;
		public String signature;
		public String access_token;
	}
}