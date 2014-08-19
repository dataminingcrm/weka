package weka.salesforce.attributes;

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
			timestamp = this.getAttribute().parseDate( value.toString() );
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return timestamp;
	}
	
	@Override
	public boolean isNumeric() {
		return true;
	}
		
	@Override
	public boolean isString() {		
		return false;
	}
}