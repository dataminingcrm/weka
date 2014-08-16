package weka.salesforce.attributes;

import weka.core.Attribute;

import com.sforce.soap.partner.Field;

public class DateTimeAttributeStrategy extends AttributeStrategy{

	public DateTimeAttributeStrategy(Field f, int i) {
		super(f, i);
	}
	
	@Override
	public Attribute buildAttribute() {
		return new Attribute( sField.getName(), this.getIndex() );
		//System.out.println( ATTRIBUTE + " " + sField.getName() + INDENT + "DATE [yyyy-MM-dd'T'HH:mm:ss]");
	}
	
	@Override
	public void renderData(Object value) {
		System.out.print("'" + (String)value + "'" );
	}
}