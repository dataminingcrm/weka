## Data Mining: Practical Machine Learning Techniques for Customer Relationship Management (CRM)

Weka library with helper classes for Salesforce CRM.

### Getting Started:
```
git clone https://github.com/dataminingcrm/weka.git
```

Create a config.properties file by copying from the provided template.
This file is used for storing Salesforce credentials.
```
cp config.properties.template config.properties
```
Edit config.properties with actual credentials.

Build the Data Mining CRM JAR
```
chmod +x build.sh
./build.sh
```

### Usage:

SObject2ARFF (Short form. Expects config.properties to be in same folder)
```
java -classpath dataminingcrm.jar weka.salesforce.Sobj2arff
```

SObject2ARFF (Long form. Passes in credentials and classifier details via command line parameters)
```
java weka.core.converters.SalesforceDataLoader -username {SFDC username} -password {SFDC password} -token {SFDC token} -url {SFDC Login URL} -query {SOQL dataset to retrieve} -relation {SObject Name (typically FROM clause in SOQL)} -class {Query result field name used as classifier}
```

### Config Files
config.properties
```
url=https://login.salesforce.com	The Salesforce login endpoint. Use https://test.salesforce.com for sandboxes.
username=username@domain.org		Salesforce username.
password=org_password			Salesforce password.
token=security_token			Salesforce security token.
dataSource=Opportunity			The Salesforce object (table) to be converted to ARFF.
query=SELECT * FROM Opportunity  	The SOQL query to be executed.
class=IsWon				The field on dataSource to be used as classifer.
```

test.properties
Same as config.properties, but used by JUnit tests (for Developers making changes to source code).

### Open Source License

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

