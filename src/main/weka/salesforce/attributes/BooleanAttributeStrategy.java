package weka.salesforce.attributes;

import weka.core.Attribute;

import com.sforce.soap.partner.Field;

public class BooleanAttributeStrategy extends AttributeStrategy {

	public BooleanAttributeStrategy(Field f, int i) {
		super(f, i);
	}

	@Override
	public Attribute buildAttribute() {
		return new Attribute( sField.getName(), this.getIndex() );
		//System.out.println(ATTRIBUTE + " " + sField.getName() + INDENT + "NUMERIC");
		//System.out.println(ATTRIBUTE + " " + sField.getName() + INDENT + "{TRUE, FALSE}");		
	}

	@Override
	public void renderData(Object value) {
		//System.out.print( value.toString().toLowerCase().equals("true") ? "1":"0" );
		System.out.print( value.toString().toLowerCase().equals("true") ? "TRUE":"FALSE" );
	}
}