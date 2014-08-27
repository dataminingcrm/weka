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
import com.sforce.soap.partner.PicklistEntry;

public class BooleanAttributeStrategy extends AttributeStrategy {

	public BooleanAttributeStrategy(Field f, int i) {
		super(f, i);
	}

	@Override
	public Attribute buildAttribute() {
		FastVector attributeValues = new FastVector(2);
		attributeValues.addElement("TRUE");
		attributeValues.addElement("FALSE");
		
		this.setAttribute( new Attribute( sField.getName(), attributeValues,  this.getIndex() ) );
		return this.getAttribute();
	}
	
	@Override
	public String getValue(Object value) {
		return value.toString().toLowerCase().equals("true") ? "TRUE":"FALSE";
	}

	@Override
	public Double getNumericValue(Object value) {
		return value.toString().toLowerCase().equals("true") ? 1.0:0.0;
	}
}