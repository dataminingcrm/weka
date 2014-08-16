package weka.salesforce.attributes;

import weka.core.Attribute;

import com.sforce.soap.partner.Field;

public class DateAttributeStrategy extends AttributeStrategy{

	public DateAttributeStrategy(Field f, int i) {
		super(f, i);
	}

	@Override
	public Attribute buildAttribute() {
		return new Attribute( sField.getName(), this.getIndex() );
		//System.out.println( ATTRIBUTE + " " + sField.getName() + INDENT + "DATE 'yyyy-MM-dd'");
	}

	@Override
	public void renderData(Object value) {
		System.out.print("'" + (String)value + "'" );
	}
}
