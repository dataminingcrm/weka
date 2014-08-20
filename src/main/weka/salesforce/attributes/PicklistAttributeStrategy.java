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
		// TODO: This attribute only includes pre-defined picklist values.
		// Ad-hoc values may appear in the data that are not defined here.
		// Consider a 2-pass filter that sets range of nominal values based on actual data.
		int size = this.sField.getPicklistValues().length;
		FastVector attributeValues = new FastVector(size);
		
		for(PicklistEntry entry : this.sField.getPicklistValues()){
			attributeValues.addElement(entry.getValue());
		}
		
		return new Attribute( sField.getName(), attributeValues,  this.getIndex() );
	}
	
	@Override
	public String getValue(Object value) {
		/* BUG: Actual picklist values may not be defined in PicklistEntry collection when arbitrary values are allowed.
		 * Resolution options:
		 * a) Pre-scan with 2 pass filter on training set to accumulate list of actual picklist values
		 * b) Ignore arbitrary picklist values 
		 */
		return value == null ? "" : (String)value;
	}
}