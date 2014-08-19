package weka.salesforce.attributes;

import weka.core.Attribute;
import weka.core.FastVector;

import com.sforce.soap.partner.Field;

public class Base64AttributeStrategy extends AttributeStrategy {

	public Base64AttributeStrategy(Field f, int i) {
		super(f, i);
	}

	@Override
	public Attribute buildAttribute() {
		// String attribute type
		return new Attribute( sField.getName(), (FastVector) null, this.getIndex() );
	}

	@Override
	public boolean isNumeric() {		
		return false;
	}

	@Override
	public boolean isString() {		
		return true;
	}
}