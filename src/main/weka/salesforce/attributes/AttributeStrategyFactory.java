package weka.salesforce.attributes;

import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.FieldType;

public class AttributeStrategyFactory {
	
	public static AttributeStrategy buildStrategy(Field f){
		if( f.getType().equals( FieldType._boolean) ){
			return new BooleanAttributeStrategy(f);
		}
		else if( f.getType().equals( FieldType._double ) ){
			return new DoubleAttributeStrategy(f);
		}
		else if( f.getType().equals( FieldType._int ) ){
			return new IntAttributeStrategy(f);
		}
		else if( f.getType().equals( FieldType.base64 ) ){
			return new Base64AttributeStrategy(f);
		}
		else if( f.getType().equals( FieldType.combobox ) ){
			return new ComboBoxAttributeStrategy(f);
		}
		else if( f.getType().equals( FieldType.currency ) ){
			return new CurrencyAttributeStrategy(f);
		}
		else if( f.getType().equals( FieldType.date ) ){
			return new DateAttributeStrategy(f);
		}
		else if( f.getType().equals( FieldType.datetime ) ){
			return new DateTimeAttributeStrategy(f);
		}
		else if( f.getType().equals( FieldType.email ) ){
			return new EmailAttributeStrategy(f);
		}
		else if( f.getType().equals( FieldType.encryptedstring ) ){
			return new EncryptedStringAttributeStrategy(f);
		}
		else if( f.getType().equals( FieldType.id ) ){
			return new DefaultAttributeStrategy(f);
		}
		else if( f.getType().equals( FieldType.location ) ){
			return new LocationAttributeStrategy(f);
		}
		else if( f.getType().equals( FieldType.multipicklist ) ){
			return new MultiPicklistAttributeStrategy(f);
		}
		else if( f.getType().equals( FieldType.percent ) ){
			return new PercentAttributeStrategy(f);
		}
		else if( f.getType().equals( FieldType.phone ) ){
			return new PhoneAttributeStrategy(f);
		}
		else if( f.getType().equals( FieldType.picklist ) ){
			return new PicklistAttributeStrategy(f);
		}
		else if( f.getType().equals( FieldType.reference ) ){
			return new ReferenceAttributeStrategy(f);
		}
		else if( f.getType().equals( FieldType.string ) ){
			return new DefaultAttributeStrategy(f);
		}
		else if( f.getType().equals( FieldType.textarea ) ){
			return new DefaultAttributeStrategy(f);
		}
		else if( f.getType().equals( FieldType.time ) ){
			return new TimeAttributeStrategy(f);
		}
		else if( f.getType().equals( FieldType.url ) ){
			return new URLAttributeStrategy(f);
		}
		else {
			return new DefaultAttributeStrategy(f);
		}
	}
}