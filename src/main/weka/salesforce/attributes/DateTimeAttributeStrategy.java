package weka.salesforce.attributes;

import com.sforce.soap.partner.Field;

public class DateTimeAttributeStrategy extends AttributeStrategy{

	public DateTimeAttributeStrategy(Field f) {
		super(f);
	}
	
	@Override
	public void renderAttribute() {
		System.out.println( ATTRIBUTE + " " + sField.getName() + INDENT + "DATE [yyyy-MM-dd'T'HH:mm:ss]");
	}
	
	@Override
	public void renderData(Object value) {
		System.out.print("'" + (String)value + "'" );
	}
}