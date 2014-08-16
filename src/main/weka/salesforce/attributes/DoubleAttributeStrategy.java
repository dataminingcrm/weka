package weka.salesforce.attributes;

import weka.core.Attribute;

import com.sforce.soap.partner.Field;

public class DoubleAttributeStrategy extends AttributeStrategy{

	public DoubleAttributeStrategy(Field f, int i) {
		super(f, i);
	}
	
	@Override
	public Attribute buildAttribute() {
		return new Attribute( sField.getName(), this.getIndex() );
		//System.out.println( ATTRIBUTE + " " + sField.getName() + INDENT + "NUMERIC");
	}
	
	@Override
	public void renderData(Object value) {
		System.out.print(value == null ? "0.0":value.toString());
	}
}