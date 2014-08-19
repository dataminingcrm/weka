package weka.salesforce.attributes;

import weka.core.Attribute;
import weka.core.FastVector;

import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.PicklistEntry;

public class BooleanAttributeStrategy extends AttributeStrategy {

	public BooleanAttributeStrategy(Field f, int i) {
		super(f, i);
	}

	@Override
	public Attribute buildAttribute() {
		FastVector attributeValues = new FastVector(2);
		attributeValues.addElement("TRUE");
		attributeValues.addElement("FALSE");
		
		this.setAttribute( new Attribute( sField.getName(), attributeValues,  this.getIndex() ) );
		return this.getAttribute();
	}
	
	@Override
	public String getValue(Object value) {
		return value.toString().toLowerCase().equals("true") ? "TRUE":"FALSE";
	}

	@Override
	public boolean isNumeric() {		
		return false;
	}

	@Override
	public Double getNumericValue(Object value) {
		return value.toString().toLowerCase().equals("true") ? 1.0:0.0;
	}

	@Override
	public boolean isString() {
		return true;
	}
}