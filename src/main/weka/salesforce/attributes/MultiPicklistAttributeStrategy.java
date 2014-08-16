package weka.salesforce.attributes;

import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.PicklistEntry;

public class MultiPicklistAttributeStrategy extends AttributeStrategy{

	public MultiPicklistAttributeStrategy(Field f) {
		super(f);
	}

	@Override
	public void renderAttribute() {
		String nominalValues = "";
		for(PicklistEntry entry : this.sField.getPicklistValues()){
			nominalValues += entry.getValue() + ",";
		}
		nominalValues = nominalValues.substring(0, nominalValues.length() - 1);
		System.out.println(ATTRIBUTE + " " + sField.getName() + INDENT + "{" + nominalValues + "}");		
	}
	
	@Override
	public void renderData(Object value) {
		System.out.print("'" + (String)value + "'" );
	}
}
