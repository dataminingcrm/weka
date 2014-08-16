package weka.salesforce.attributes;

import com.sforce.soap.partner.Field;

public class DoubleAttributeStrategy extends AttributeStrategy{

	public DoubleAttributeStrategy(Field f) {
		super(f);
	}
	
	@Override
	public void renderAttribute() {
		System.out.println( ATTRIBUTE + " " + sField.getName() + INDENT + "NUMERIC");
	}
	
	@Override
	public void renderData(Object value) {
		System.out.print(value == null ? "0.0":value.toString());
	}
}