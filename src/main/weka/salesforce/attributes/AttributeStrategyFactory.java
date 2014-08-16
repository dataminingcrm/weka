package weka.salesforce.attributes;

import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.FieldType;

public class AttributeStrategyFactory {
	
	/*
	 // 1. set up attributes
	FastVector attributes = new FastVector();
		
	// add nominal attribute
	FastVector catVals = new FastVector(3);
		catVals.addElement("sports");
		catVals.addElement("finance");
		catVals.addElement("news");
	attributes.addElement(new Attribute("category (att1)", catVals));

     // add numeric attributes
	attributes.addElement(new Attribute("visits (att2)"));
	
	// add string attribute
	attributes.addElement(new Attribute("title (att3)", (FastVector) null));
	
	// add date attribute
	attributes.addElement(new Attribute("posted (att4)", "yyyy-MM-dd")); //ISO-8601 compliant date string
	*/
	public static AttributeStrategy buildStrategy(Field f, int index){
		if( f.getType().equals( FieldType._boolean) ){
			return new BooleanAttributeStrategy(f, index);
		}
		else if( f.getType().equals( FieldType._double ) ){
			return new DoubleAttributeStrategy(f, index);
		}
		else if( f.getType().equals( FieldType._int ) ){
			return new IntAttributeStrategy(f, index);
		}
		else if( f.getType().equals( FieldType.base64 ) ){
			return new Base64AttributeStrategy(f, index);
		}
		else if( f.getType().equals( FieldType.combobox ) ){
			return new ComboBoxAttributeStrategy(f, index);
		}
		else if( f.getType().equals( FieldType.currency ) ){
			return new CurrencyAttributeStrategy(f, index);
		}
		else if( f.getType().equals( FieldType.date ) ){
			return new DateAttributeStrategy(f, index);
		}
		else if( f.getType().equals( FieldType.datetime ) ){
			return new DateTimeAttributeStrategy(f, index);
		}
		else if( f.getType().equals( FieldType.email ) ){
			return new EmailAttributeStrategy(f, index);
		}
		else if( f.getType().equals( FieldType.encryptedstring ) ){
			return new EncryptedStringAttributeStrategy(f, index);
		}
		else if( f.getType().equals( FieldType.id ) ){
			return new DefaultAttributeStrategy(f, index);
		}
		else if( f.getType().equals( FieldType.location ) ){
			return new LocationAttributeStrategy(f, index);
		}
		else if( f.getType().equals( FieldType.multipicklist ) ){
			return new MultiPicklistAttributeStrategy(f, index);
		}
		else if( f.getType().equals( FieldType.percent ) ){
			return new PercentAttributeStrategy(f, index);
		}
		else if( f.getType().equals( FieldType.phone ) ){
			return new PhoneAttributeStrategy(f, index);
		}
		else if( f.getType().equals( FieldType.picklist ) ){
			return new PicklistAttributeStrategy(f, index);
		}
		else if( f.getType().equals( FieldType.reference ) ){
			return new ReferenceAttributeStrategy(f, index);
		}
		else if( f.getType().equals( FieldType.string ) ){
			return new DefaultAttributeStrategy(f, index);
		}
		else if( f.getType().equals( FieldType.textarea ) ){
			return new DefaultAttributeStrategy(f, index);
		}
		else if( f.getType().equals( FieldType.time ) ){
			return new TimeAttributeStrategy(f, index);
		}
		else if( f.getType().equals( FieldType.url ) ){
			return new URLAttributeStrategy(f, index);
		}
		else {
			return new DefaultAttributeStrategy(f, index);
		}
	}
}