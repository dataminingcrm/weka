package weka.salesforce.attributes;

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
	public boolean isNumeric() {
		return false;
	}
		
	@Override
	public boolean isString() {		
		return true;
	}
}