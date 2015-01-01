package weka.core.converters.salesforce;

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
import java.util.ArrayList;
import java.util.List;

import weka.core.converters.DatabaseLoader;
import weka.salesforce.SalesforceConnection;

public class SalesforceDataLoader extends DatabaseLoader {
	private static final long serialVersionUID = 1L;
	public List<String> Errors = new ArrayList<String>();

	public SalesforceDataLoader() throws Exception {
		super();
	}

	public boolean hasErrors() {
		return Errors.size() > 0;
	}

	private String m_Token = null;

	public void setToken(final String token) {
		m_Token = token;
	}

	public String getToken() {
		return m_Token;
	}

	private SalesforceConnection m_Connection = null;

	public SalesforceConnection getConnection() {
		if (m_Connection == null) {
			m_Connection = new SalesforceConnection().withUsername(getUser())
					.withPassword(getPassword()).withSecurityToken(getToken())
					.withLoginUrl(getUrl()).connectWithUserCredentials();

			if (!m_Connection.isValid()) {
				System.err
				.println("Could not establish Salesforce connection. Please check config.properties file.");
			}
		}
		return m_Connection;
	}
}