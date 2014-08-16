package weka.salesforce.attributes;

import com.sforce.soap.partner.Field;

public abstract class AttributeStrategy {
	public final static String ATTRIBUTE = "@ATTRIBUTE";
	public final static String INDENT = "  ";
	
	protected Field sField;
	public AttributeStrategy(Field f) {
		sField = f;
	}
	
	public abstract void renderAttribute();
	
	public abstract void renderData(Object value);
}