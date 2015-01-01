package weka.salesforce.filters;

import java.util.Enumeration;
import java.util.Map;

import weka.core.Attribute;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.salesforce.attributes.AttributeStrategy;
import weka.salesforce.attributes.AttributeUtils;

import com.sforce.soap.partner.FieldType;

public class SalesforceFilter {
	private Instances m_instances;
	private Map<String, AttributeStrategy> m_attributeStrategies;

	public SalesforceFilter(Instances dataset,
			Map<String, AttributeStrategy> map) {
		this.m_instances = dataset;
		this.m_attributeStrategies = map;
	}

	public Instances getDataSet() {
		return this.m_instances;
	}

	public SalesforceFilter StringsToNominal() throws Exception {
		this.m_instances = new StringsToNominal(this.m_instances,
				this.m_attributeStrategies).filter();
		return this;
	}

	public SalesforceFilter RemoveEmptyNominals() throws Exception {
		return RemoveNominalsWithValueCount(0);
	}

	public SalesforceFilter RemoveUnaryNominals() throws Exception {
		return RemoveNominalsWithValueCount(1);
	}

	public SalesforceFilter RemoveId() throws Exception {
		Attribute attrib = AttributeUtils.get(
				this.m_instances.enumerateAttributes(), "Id");
		if (attrib != null) {
			this.m_instances.deleteAttributeAt(attrib.index());
		}
		return this;
	}

	public SalesforceFilter RemoveReferences() throws Exception {
		this.m_instances = this.removeByFieldType(this.m_instances,
				this.m_attributeStrategies, FieldType.reference);

		return this;
	}

	public SalesforceFilter RemoveDates() throws Exception {
		this.m_instances = this.removeByFieldType(this.m_instances,
				this.m_attributeStrategies, FieldType.date);

		this.m_instances = this.removeByFieldType(this.m_instances,
				this.m_attributeStrategies, FieldType.datetime);

		return this;
	}

	public SalesforceFilter RemoveField(String fieldName) {
		Attribute attrib = AttributeUtils.get(
				this.m_instances.enumerateAttributes(), fieldName);
		if (attrib != null) {
			this.m_instances.deleteAttributeAt(attrib.index());
		}
		return this;
	}

	private Instances removeByFieldType(Instances dataset,
			Map<String, AttributeStrategy> attributeStrategies, FieldType fType)
			throws Exception {

		String removeList = "";
		Enumeration<Attribute> attributes = dataset.enumerateAttributes();

		while (attributes.hasMoreElements()) {
			Attribute attrib = attributes.nextElement();
			AttributeStrategy strat = attributeStrategies.get(attrib.name());
			if (strat.getField().getType().equals(fType)) {
				removeList += String.valueOf(attrib.index() + 1) + ",";
			}
		}
		removeList = removeList.substring(0, removeList.length() - 1);

		String[] options = new String[2];
		options[0] = "-R";
		options[1] = removeList;

		Remove removeFilter = new Remove();
		removeFilter.setOptions(options);
		removeFilter.setInputFormat(dataset);
		dataset = Filter.useFilter(dataset, removeFilter);

		return dataset;
	}

	public SalesforceFilter RemoveNominalsWithValueCount(int count)
			throws Exception {
		String removeList = "";
		Enumeration<Attribute> attributes = this.m_instances
				.enumerateAttributes();

		while (attributes.hasMoreElements()) {
			Attribute attrib = attributes.nextElement();
			if (attrib.isNominal() && attrib.numValues() == count) {
				removeList += String.valueOf(attrib.index() + 1) + ",";
			}
		}
		removeList = removeList.substring(0, removeList.length() - 1);

		String[] options = new String[2];
		options[0] = "-R";
		options[1] = removeList;

		Remove removeFilter = new Remove();
		removeFilter.setOptions(options);
		removeFilter.setInputFormat(m_instances);
		m_instances = Filter.useFilter(m_instances, removeFilter);

		return this;
	}
}