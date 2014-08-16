package weka.salesforce.attributes;

import weka.core.Attribute;

import com.sforce.soap.partner.Field;

public abstract class AttributeStrategy {
	public final static String ATTRIBUTE = "@ATTRIBUTE";
	public final static String INDENT = "  ";
	public int m_Index = -1;
	
	protected Field sField;
	public AttributeStrategy(Field f, int i) {
		sField = f;
		m_Index = i;
	}
	
	private Attribute m_attribute = null;
	public void setAttribute(Attribute attrib){ this.m_attribute = attrib; }
	public Attribute getAttribute(){ return this.m_attribute; }
	
	public AttributeStrategy withIndex(int i){ m_Index = i; return this; }
	public int getIndex(){ return m_Index; }
	public abstract Attribute buildAttribute();	
	public abstract void renderData(Object value);
}