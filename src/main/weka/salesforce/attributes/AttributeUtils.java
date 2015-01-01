package weka.salesforce.attributes;

import java.util.Enumeration;

import weka.core.Attribute;

public class AttributeUtils {

	public static Attribute get(Enumeration attributes, String name) {
		while (attributes.hasMoreElements()) {
			Attribute attrib = (Attribute) attributes.nextElement();
			if (attrib.name().equals(name)) {
				return attrib;
			}
		}
		return null;
	}
}