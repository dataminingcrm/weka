package weka.salesforce.attributes;

import weka.core.Attribute;
import weka.core.FastVector;

import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.PicklistEntry;

public class PicklistAttributeStrategy extends AttributeStrategy{

	public PicklistAttributeStrategy(Field f, int i) {
		super(f, i);
	}
	
	@Override
	public Attribute buildAttribute() {
		int size = this.sField.getPicklistValues().length;
		FastVector attributeValues = new FastVector(size);
		
		for(PicklistEntry entry : this.sField.getPicklistValues()){
			attributeValues.addElement(entry.getValue());
		}
		
		return new Attribute( sField.getName(), attributeValues,  this.getIndex() );
	}
	
	@Override
	public String getValue(Object value) {
		return value == null ? "" : (String)value;
	}
}