package weka.salesforce.attributes;

import weka.core.Attribute;

import com.sforce.soap.partner.Field;

public abstract class AttributeStrategy {
	public int m_Index = -1;
	
	protected Field sField;
	public AttributeStrategy(Field f, int i) {
		sField = f;
		m_Index = i;
		this.setAttribute( this.buildAttribute() );
	}
	
	private Attribute m_attribute = null;
	public void setAttribute(Attribute attrib){ this.m_attribute = attrib; }
	public Attribute getAttribute(){
		if(this.m_attribute == null){
			this.m_attribute = this.buildAttribute();
		}
		return this.m_attribute; 
	}
	
	public AttributeStrategy withIndex(int i){ m_Index = i; return this; }
	public int getIndex(){ return m_Index; }
	public abstract Attribute buildAttribute();
		
	public Double getNumericValue(Object value){
		return (value == null ? 0.0 : Double.valueOf(value.toString()) );
	}
		
	public String getValue(Object value){
		return (value == null ? "" : (String)value);
	};
}