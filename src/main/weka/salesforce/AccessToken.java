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