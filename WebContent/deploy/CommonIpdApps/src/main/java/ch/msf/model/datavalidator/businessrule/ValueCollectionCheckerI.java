package ch.msf.model.datavalidator.businessrule;

import java.util.List;


public interface ValueCollectionCheckerI {

	public boolean applyLineFilters(List<String> inputLines, List<BusinessRuleCheckerI> businessRules);
}
