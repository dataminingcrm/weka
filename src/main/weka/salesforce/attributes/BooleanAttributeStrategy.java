package weka.salesforce.attributes;

import com.sforce.soap.partner.Field;

public class BooleanAttributeStrategy extends AttributeStrategy {

	public BooleanAttributeStrategy(Field f) {
		super(f);
	}

	@Override
	public void renderAttribute() {
		//System.out.println(ATTRIBUTE + " " + sField.getName() + INDENT + "NUMERIC");
		System.out.println(ATTRIBUTE + " " + sField.getName() + INDENT + "{TRUE, FALSE}");		
	}

	@Override
	public void renderData(Object value) {
		//System.out.print( value.toString().toLowerCase().equals("true") ? "1":"0" );
		System.out.print( value.toString().toLowerCase().equals("true") ? "TRUE":"FALSE" );
	}
}