package weka.salesforce.attributes;

import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.PicklistEntry;

public class PicklistAttributeStrategy extends AttributeStrategy{

	public PicklistAttributeStrategy(Field f) {
		super(f);
	}
	
	@Override
	public void renderAttribute() {
		String nominalValues = "";
		for(PicklistEntry entry : this.sField.getPicklistValues()){
			nominalValues += "'" + entry.getValue() + "',";
		}
		nominalValues = nominalValues.substring(0, nominalValues.length() - 1);
		System.out.println(ATTRIBUTE + " " + sField.getName() + INDENT + "{" + nominalValues + "}");
	}
	
	@Override
	public void renderData(Object value) {
		/* BUG: Actual picklist values may not be defined in PicklistEntry collection when arbitrary values are allowed.
		 * Resolution options:
		 * a) Pre-scan with 2 pass filter on training set to accumulate list of actual picklist values
		 * b) Ignore arbitrary picklist values 
		 */
		System.out.print("'" + (String)value + "'" );
	}
}