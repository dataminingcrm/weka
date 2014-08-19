package weka.salesforce.attributes;

import org.joda.time.DateTime;

import weka.core.Attribute;

import com.sforce.soap.partner.Field;

public class DateAttributeStrategy extends AttributeStrategy{
	
	public DateAttributeStrategy(Field f, int i) {
		super(f, i);
	}
	
	@Override
	public Attribute buildAttribute() {
		return new Attribute(sField.getName(), "yyyy-MM-dd", this.getIndex()); //ISO-8601 compliant date string
	}
	
	@Override
	public String getValue(Object value){
		DateTime dt = new DateTime(value);
		return dt.toString("yyyy-MM-dd");
	}
	
	@Override
	public boolean isNumeric() {		
		return false;
	}
	
	@Override
	public boolean isString() {		
		return true;
	}
}