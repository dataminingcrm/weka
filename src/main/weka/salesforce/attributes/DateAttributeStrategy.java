package weka.salesforce.attributes;

import com.sforce.soap.partner.Field;

public class DateAttributeStrategy extends AttributeStrategy{

	public DateAttributeStrategy(Field f) {
		super(f);
	}

	@Override
	public void renderAttribute() {
		System.out.println( ATTRIBUTE + " " + sField.getName() + INDENT + "DATE 'yyyy-MM-dd'");
	}

	@Override
	public void renderData(Object value) {
		System.out.print("'" + (String)value + "'" );
	}
}
