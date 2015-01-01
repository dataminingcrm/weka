package weka.salesforce.filters;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import weka.core.Attribute;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToNominal;
import weka.salesforce.attributes.AttributeStrategy;

public class StringsToNominal extends FilterBase {

	public StringsToNominal(Instances dataset,
			Map<String, AttributeStrategy> attributeStrategies) {
		super(dataset, attributeStrategies);
	}

	@Override
	public Instances filter() throws Exception {

		List<Integer> stringIndices = new ArrayList<Integer>();
		Enumeration<Attribute> attributes = this.m_instances
				.enumerateAttributes();

		while (attributes.hasMoreElements()) {
			Attribute attrib = attributes.nextElement();
			AttributeStrategy strat = this.m_attributeStrategies.get(attrib
					.name());
			if (attrib.isString()) {
				stringIndices.add(attrib.index() + 1);
			}
		}

		for (Integer i : stringIndices) {
			StringToNominal stringFilter = new StringToNominal();
			String[] options = new String[2];
			options[0] = "-R";
			options[1] = i.toString();
			stringFilter.setOptions(options);
			stringFilter.setInputFormat(this.m_instances);
			this.m_instances = Filter.useFilter(this.m_instances, stringFilter);
		}
		return this.m_instances;
	}
}