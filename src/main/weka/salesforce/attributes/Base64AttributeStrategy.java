package weka.salesforce.attributes;

import com.sforce.soap.partner.Field;

public class Base64AttributeStrategy extends AttributeStrategy {

	public Base64AttributeStrategy(Field f) {
		super(f);
	}

	@Override
	public void renderAttribute() {
		System.out.println( ATTRIBUTE + " " + sField.getName() + INDENT + "STRING");
	}

	@Override
	public void renderData(Object value) {
		System.out.print("'" + (String)value + "'" );
	}
}