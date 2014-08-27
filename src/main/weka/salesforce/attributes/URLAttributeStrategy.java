package weka.salesforce.attributes;
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
import weka.core.Attribute;
import weka.core.FastVector;

import com.sforce.soap.partner.Field;

public class URLAttributeStrategy extends AttributeStrategy{
	
	public URLAttributeStrategy(Field f, int i) {
		super(f, i);
	}
	
	@Override
	public Attribute buildAttribute() {
		// String attribute type
		return new Attribute( sField.getName(), (FastVector) null, this.getIndex() );
	}
}