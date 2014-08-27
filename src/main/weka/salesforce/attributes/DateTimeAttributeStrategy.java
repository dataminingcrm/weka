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
import java.text.ParseException;

import org.joda.time.DateTime;

import weka.core.Attribute;

import com.sforce.soap.partner.Field;

public class DateTimeAttributeStrategy extends AttributeStrategy{

	public DateTimeAttributeStrategy(Field f, int i) {
		super(f, i);
	}
	
	@Override
	public Attribute buildAttribute() {
		return new Attribute(sField.getName(), "yyyy-MM-dd'T'HH:mm:ss", this.getIndex()); //ISO-8601 compliant date string
	}
	
	@Override
	public String getValue(Object value){
		DateTime dt = new DateTime(value);
		return dt.toString("yyyy-MM-dd");
	}
	
	@Override
	public Double getNumericValue(Object value){
		Double timestamp = 0.0;
		try {
			if( value == null || value.equals("") ){
				timestamp = 0.0;
			} else {
				timestamp = this.getAttribute().parseDate( value.toString() );
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return timestamp;
	}
}