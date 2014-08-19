package weka.salesforce.attributes;

import weka.core.Attribute;
import weka.core.FastVector;

import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.PicklistEntry;

public class ComboBoxAttributeStrategy extends AttributeStrategy{

	public ComboBoxAttributeStrategy(Field f, int i) {
		super(f, i);
	}
	
	@Override
	public Attribute buildAttribute() {
		int size = this.sField.getPicklistValues().length;
		FastVector attributeValues = new FastVector(size);
		
		for(PicklistEntry entry : this.sField.getPicklistValues()){
			attributeValues.addElement(entry.getValue());
		}
		
		this.setAttribute( new Attribute( sField.getName(), attributeValues,  this.getIndex() ) );
		return this.getAttribute();
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