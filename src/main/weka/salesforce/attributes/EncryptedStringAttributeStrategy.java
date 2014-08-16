package weka.salesforce.attributes;

import weka.core.Attribute;

import com.sforce.soap.partner.Field;

public class EncryptedStringAttributeStrategy extends AttributeStrategy{

	public EncryptedStringAttributeStrategy(Field f, int i) {
		super(f, i);
	}

	@Override
	public Attribute buildAttribute() {
		return new Attribute( sField.getName(), this.getIndex() );
		//System.out.println( ATTRIBUTE + " " + sField.getName() + INDENT + "STRING");
	}

	@Override
	public void renderData(Object value) {
		System.out.print("'" + (String)value + "'" );
	}
}