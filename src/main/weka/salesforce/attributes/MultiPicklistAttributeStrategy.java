package weka.salesforce.attributes;

import weka.core.Attribute;

import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.PicklistEntry;

public class MultiPicklistAttributeStrategy extends AttributeStrategy{

	public MultiPicklistAttributeStrategy(Field f, int i) {
		super(f, i);
	}

	@Override
	public Attribute buildAttribute() {
		return new Attribute( sField.getName(), this.getIndex() );
		/*
		String nominalValues = "";
		for(PicklistEntry entry : this.sField.getPicklistValues()){
			nominalValues += entry.getValue() + ",";
		}
		nominalValues = nominalValues.substring(0, nominalValues.length() - 1);
		System.out.println(ATTRIBUTE + " " + sField.getName() + INDENT + "{" + nominalValues + "}");
		*/		
	}
	
	@Override
	public void renderData(Object value) {
		System.out.print("'" + (String)value + "'" );
	}
}
